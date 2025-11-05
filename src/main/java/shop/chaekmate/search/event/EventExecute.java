package shop.chaekmate.search.event;

import static java.util.UUID.randomUUID;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.Cache;
import org.springframework.context.event.EventListener;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import shop.chaekmate.search.api.AiApiClient;
import shop.chaekmate.search.document.Book;
import shop.chaekmate.search.document.KeywordGroup;
import shop.chaekmate.search.document.KeywordGroupMapping;
import shop.chaekmate.search.dto.EmbeddingResponse;
import shop.chaekmate.search.dto.GroupNameDto;
import shop.chaekmate.search.repository.BookRepository;
import shop.chaekmate.search.repository.KeywordGroupRepository;

@Slf4j
@Component
@RequiredArgsConstructor
public class EventExecute {
    private final KeywordGroupRepository keywordGroupRepository;
    private final AiApiClient aiApiClient;
    private final RedisCacheManager redisCacheManager;
    private final ObjectMapper objectMapper;
    private final BookRepository bookRepository;
    private static  final String GROUP_MAPPING = "group-mapping";
    private static final String GROUP = "group";
    @EventListener(CreateGroupEvent.class)
    @Async
    public void createGroupEvent(CreateGroupEvent createGroupEvent) throws JsonProcessingException {
        List<Book> books = createGroupEvent.books();
        List<Long> ids = createGroupEvent.ids();
        String keywordJson = objectMapper.writeValueAsString(createGroupEvent.keyword());
        String booksJson = objectMapper.writeValueAsString(books.stream().map(Book::toJson).toList());
        String keywordVector = objectMapper.writeValueAsString(createGroupEvent.vector());
        String groupJson = aiApiClient.groupName(keywordJson, booksJson, keywordVector);

        GroupNameDto groupNameDto = objectMapper.readValue(groupJson, GroupNameDto.class);
        EmbeddingResponse embeddingResponse = aiApiClient.createEmbedding(groupNameDto.getGroupName());
        UUID id = randomUUID();
        KeywordGroup keywordGroup = KeywordGroup.builder().id(id).embedding(embeddingResponse.getEmbedding()).build();

        Cache groupCache = getCache(GROUP);
        Cache mappingCache = getCache(GROUP_MAPPING);
        if (groupCache != null && mappingCache != null) {
            groupCache.put(id, new KeywordGroupMapping(id, ids, groupNameDto.getGroupName(), 0));
            keywordGroupRepository.save(keywordGroup);
            for (Book book : books) {
                Set<UUID> groupIds = Optional.ofNullable(mappingCache.get(book.getId(), Set.class))
                        .map(s -> (Set<UUID>) s)
                        .orElse(new HashSet<>());
                groupIds.add(id);
                mappingCache.put(book.getId(), groupIds);
            }
        }
    }

    @EventListener(UpdateGroupEvent.class)
    @Async
    public void updateGroupEvent(UpdateGroupEvent updateGroupEvent) {
        List<KeywordGroup> keywordGroups = bookRepository.searchByKeywordGroupVector(
                updateGroupEvent.embeddingResponse(), 100);
        Book book = updateGroupEvent.book();
        Cache groupCache = getCache(GROUP);
        Cache mappingCache = getCache(GROUP_MAPPING);
        if (groupCache == null || mappingCache == null) {
            return;
        }

        Set<UUID> oldGroupIds = mappingCache.get(book.getId(), Set.class);
        if (oldGroupIds != null && !oldGroupIds.isEmpty()) {
            for (UUID groupId : oldGroupIds) {
                KeywordGroupMapping mapping = groupCache.get(groupId, KeywordGroupMapping.class);
                if (mapping != null) {
                    mapping.getIds().removeIf(id -> id.equals(book.getId()));
                    groupCache.put(groupId, mapping);
                }
            }
            mappingCache.evict(book.getId());
        }

        Set<UUID> newGroupIds = new HashSet<>();
        for (KeywordGroup group : keywordGroups) {
            newGroupIds.add(group.getId());
            KeywordGroupMapping mapping = groupCache.get(group.getId(), KeywordGroupMapping.class);
            if (mapping != null) {
                mapping.getIds().add(book.getId());
                groupCache.put(group.getId(), mapping);
            }
        }
        mappingCache.put(book.getId(), newGroupIds);
    }

    @EventListener(DeleteGroupEvent.class)
    @Async
    public void deleteBookGroupEvent(DeleteGroupEvent deleteGroupEvent) {
        Cache groupCache = getCache(GROUP);
        Cache mappingCache = getCache(GROUP_MAPPING);
        if (groupCache == null || mappingCache == null) {
            return;
        }

        Set<UUID> oldGroupIds = mappingCache.get(deleteGroupEvent.id(), Set.class);
        if (oldGroupIds != null && !oldGroupIds.isEmpty()) {
            for (UUID groupId : oldGroupIds) {
                KeywordGroupMapping mapping = groupCache.get(groupId, KeywordGroupMapping.class);
                if (mapping != null) {
                    mapping.getIds().removeIf(id -> id.equals(deleteGroupEvent.id()));
                    groupCache.put(groupId, mapping);
                }
            }
            mappingCache.evict(deleteGroupEvent.id());
        }

    }
    private Cache getCache(String name) {
        Cache cache = redisCacheManager.getCache(name);
        if (cache == null) {
            return null;
        }
        return cache;
    }
}
