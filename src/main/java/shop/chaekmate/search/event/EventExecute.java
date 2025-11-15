package shop.chaekmate.search.event;

import static java.util.UUID.randomUUID;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.Cache;
import org.springframework.cache.Cache.ValueWrapper;
import org.springframework.context.event.EventListener;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;
import shop.chaekmate.search.document.Book;
import shop.chaekmate.search.document.ExpiringGroup;
import shop.chaekmate.search.document.KeywordGroup;
import shop.chaekmate.search.document.KeywordGroupMapping;
import shop.chaekmate.search.repository.BookRepository;
import shop.chaekmate.search.repository.KeywordGroupRepository;
import shop.chaekmate.search.task.queue.ExpiringGroupManager;

@Slf4j
@Component
@RequiredArgsConstructor
public class EventExecute {
    private final KeywordGroupRepository keywordGroupRepository;
    private final RedisCacheManager redisCacheManager;
    private final ObjectMapper objectMapper;
    private final BookRepository bookRepository;
    private static final String GROUP_MAPPING = "group-mapping";
    private static final String GROUP = "group";

    private final ExpiringGroupManager expiringGroupManager;

    @EventListener(CreateGroupEvent.class)
    @Async
    public void createGroupEvent(CreateGroupEvent createGroupEvent) {
        List<Long> ids = createGroupEvent.ids();
        List<Book> books = createGroupEvent.books();
        UUID id = randomUUID();
        KeywordGroup keywordGroup = KeywordGroup.builder().id(id).embedding(createGroupEvent.vector()).build();

        Cache groupCache = getCache(GROUP);
        Cache mappingCache = getCache(GROUP_MAPPING);
        if (groupCache != null && mappingCache != null) {
            groupCache.put(id, new KeywordGroupMapping(id, ids, 0));
            keywordGroupRepository.save(keywordGroup);

            for (Book book : books) {
                Set<UUID> groupIds = new HashSet<>(Optional.ofNullable(mappingCache.get(book.getId(), List.class))
                        .orElse(Collections.emptyList()));
                groupIds.add(id);
                mappingCache.put(book.getId(), new ArrayList<>(groupIds));
            }
            expiringGroupManager.offer(id, Duration.ofHours(3));
        }
    }

    // 생성 이후에 요청
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void updateGroupEvent(UpdateGroupEvent updateGroupEvent) {
        List<KeywordGroup> keywordGroups = bookRepository.searchByKeywordGroupVector(
                updateGroupEvent.embeddingResponse(), 100);
        Book book = updateGroupEvent.book();
        Cache groupCache = getCache(GROUP);
        Cache mappingCache = getCache(GROUP_MAPPING);
        if (groupCache == null || mappingCache == null) {
            return;
        }
        Set<UUID> oldGroupIds = new HashSet<>(Optional.ofNullable(mappingCache.get(book.getId(), List.class))
                .orElse(Collections.emptyList()));
        if (!oldGroupIds.isEmpty()) {
            for (UUID groupId : oldGroupIds) {
                KeywordGroupMapping mapping = toKeywordGroupMapping(groupCache, groupId);
                if (mapping != null) {
                    mapping.getIds().removeIf(id -> id.equals(book.getId()));
                    groupCache.put(groupId, mapping);
                }

                mappingCache.evict(book.getId());
            }

            Set<UUID> newGroupIds = new HashSet<>();
            for (KeywordGroup group : keywordGroups) {
                newGroupIds.add(group.getId());
                KeywordGroupMapping mapping = toKeywordGroupMapping(groupCache, group.getId());
                if (mapping != null) {
                    mapping.getIds().add(book.getId());
                    groupCache.put(group.getId(), mapping);
                }
            }
            if (!newGroupIds.isEmpty()) {
                mappingCache.put(book.getId(), new ArrayList<>(newGroupIds));
            }
        }
    }

    @EventListener(DeleteGroupEvent.class)
    @Async
    public void deleteBookGroupEvent(DeleteGroupEvent deleteGroupEvent) {
        Cache groupCache = getCache(GROUP);
        Cache mappingCache = getCache(GROUP_MAPPING);
        if (groupCache == null || mappingCache == null) {
            return;
        }
        Set<UUID> oldGroupIds = new HashSet<>(Optional.ofNullable(mappingCache.get(deleteGroupEvent.id(), List.class))
                .orElse(Collections.emptyList()));
        if (!oldGroupIds.isEmpty()) {
            for (UUID groupId : oldGroupIds) {
                KeywordGroupMapping mapping = toKeywordGroupMapping(groupCache, groupId);
                if (mapping != null) {
                    mapping.getIds().removeIf(id -> id.equals(deleteGroupEvent.id()));
                    groupCache.put(groupId, mapping);
                }

            }
            mappingCache.evict(deleteGroupEvent.id());
        }

    }

    @EventListener(ExpiringGroup.class)
    @Async
    public void expiringGroup(ExpiringGroup expiringGroup) {
        Cache groupCache = getCache(GROUP);
        Cache mappingCache = getCache(GROUP_MAPPING);
        if (groupCache == null || mappingCache == null) {
            return;
        }
        KeywordGroupMapping mapping = toKeywordGroupMapping(groupCache, expiringGroup.getUuid());
        if (mapping == null) {
            return;
        }
        keywordGroupRepository.findById(mapping.getId()).ifPresent(keywordGroupRepository::delete);
        for (Long id : mapping.getIds()) {
            Set<UUID> changeGroupIds = new HashSet<>(Optional.ofNullable(mappingCache.get(id, List.class))
                    .orElse(Collections.emptyList()));
            if (changeGroupIds.isEmpty()) {
                continue;
            }
            changeGroupIds.remove(mapping.getId());
            mappingCache.put(id, new ArrayList<>(changeGroupIds));
        }

    }

    private Cache getCache(String name) {
        return redisCacheManager.getCache(name);
    }

    private KeywordGroupMapping toKeywordGroupMapping(Cache groupCache, UUID groupId) {
        ValueWrapper wrapper = groupCache.get(groupId);
        if (wrapper == null) {
            return null;
        }
        return objectMapper.convertValue(wrapper.get(),
                KeywordGroupMapping.class);
    }
}
