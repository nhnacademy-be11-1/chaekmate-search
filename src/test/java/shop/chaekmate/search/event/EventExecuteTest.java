package shop.chaekmate.search.event;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.anyInt;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.cache.Cache;
import org.springframework.cache.Cache.ValueWrapper;
import org.springframework.data.redis.cache.RedisCacheManager;
import shop.chaekmate.search.api.AiApiClient;
import shop.chaekmate.search.document.Book;
import shop.chaekmate.search.document.ExpiringGroup;
import shop.chaekmate.search.document.KeywordGroup;
import shop.chaekmate.search.document.KeywordGroupMapping;
import shop.chaekmate.search.dto.EmbeddingResponse;
import shop.chaekmate.search.dto.GroupNameDto;
import shop.chaekmate.search.repository.BookRepository;
import shop.chaekmate.search.repository.KeywordGroupRepository;
import shop.chaekmate.search.task.queue.ExpiringGroupManager;

@ExtendWith(MockitoExtension.class)
class EventExecuteTest {
    @Mock
    KeywordGroupRepository keywordGroupRepository;
    @Mock
    AiApiClient aiApiClient;
    @Mock
    RedisCacheManager redisCacheManager;
    @Mock
    BookRepository bookRepository;
    @Mock
    ExpiringGroupManager expiringGroupManager;
    @Mock
    ObjectMapper objectMapper;
    @InjectMocks
    EventExecute eventExecute;

    Book book;
    KeywordGroup keywordGroup;
    Cache groupCache;
    Cache groupMappingCache;
    ValueWrapper wrapper;
    @BeforeEach
    void init() {
        book = Book.builder().id(1).author("test").categories(List.of("test1", "test2")).description("test")
                .embedding(new Float[]{0.9f, 0.2f}).price(10000).publicationDatetime(
                        LocalDateTime.now()).tags(List.of("test", "test2")).title("test").bookImages(List.of("tset"))
                .build();
        keywordGroup = KeywordGroup.builder().id(UUID.randomUUID()).embedding(new Float[]{0.9f}).build();
        groupCache = Mockito.mock(Cache.class);
        groupMappingCache = Mockito.mock(Cache.class);
        when(redisCacheManager.getCache("group-mapping")).thenReturn(groupMappingCache);
        when(redisCacheManager.getCache("group")).thenReturn(groupCache);
        wrapper = mock(ValueWrapper.class);

    }

    @Test
    void 그룹생성이벤트() throws JsonProcessingException {
        CreateGroupEvent createGroupEvent = new CreateGroupEvent("소설책추천해줘", List.of(book), List.of(1L),
                new Float[]{0.9F});
        when(objectMapper.writeValueAsString(any()))
                .thenReturn("mocked-json");
        when(objectMapper.readValue(anyString(), eq(GroupNameDto.class)))
                .thenReturn(new GroupNameDto("소설책"));

        when(aiApiClient.groupName(any(), any(), any()))
                .thenReturn("{\"groupName\":\"소설책\"}");
        when(aiApiClient.createEmbedding(any()))
                .thenReturn(new EmbeddingResponse());

        doNothing().when(groupCache).put(any(), any());
        when(keywordGroupRepository.save(any())).thenReturn(KeywordGroup.builder().build());
        when(groupMappingCache.get(any(), (Class<Object>) any()))
                .thenReturn(null);
        eventExecute.createGroupEvent(createGroupEvent);
        verify(keywordGroupRepository, times(1)).save(any());
        verify(groupMappingCache, times(1)).put(any(), any());
        verify(groupCache, times(1)).put(any(), any());
        verify(expiringGroupManager, times(1)).offer(any(), any());
    }

    @Test
    void 그룹생성이벤트실패_getCacheNull() throws JsonProcessingException {
        groupCache = Mockito.mock(Cache.class);
        groupMappingCache = Mockito.mock(Cache.class);
        when(redisCacheManager.getCache("group-mapping")).thenReturn(null);
        when(redisCacheManager.getCache("group")).thenReturn(null);

        CreateGroupEvent createGroupEvent = new CreateGroupEvent("소설책추천해줘", List.of(book), List.of(1L),
                new Float[]{0.9F});
        when(objectMapper.writeValueAsString(any()))
                .thenReturn("mocked-json");
        when(objectMapper.readValue(anyString(), eq(GroupNameDto.class)))
                .thenReturn(new GroupNameDto("소설책"));

        when(aiApiClient.groupName(any(), any(), any()))
                .thenReturn("{\"groupName\":\"소설책\"}");
        when(aiApiClient.createEmbedding(any()))
                .thenReturn(new EmbeddingResponse());
        eventExecute.createGroupEvent(createGroupEvent);

        verify(expiringGroupManager, times(0)).offer(any(), any());

    }

    @Test
    void 그룹업데이트이벤트() {
        EmbeddingResponse embeddingResponse = new EmbeddingResponse();
        embeddingResponse.setEmbedding(new Float[]{0.9f});
        UpdateGroupEvent updateGroupEvent = new UpdateGroupEvent(embeddingResponse, book);
        UUID uuid = UUID.randomUUID();
        KeywordGroupMapping keywordGroupMapping = new KeywordGroupMapping(uuid, new ArrayList<>(List.of(1L)), "test",
                1);
        when(bookRepository.searchByKeywordGroupVector(any(), anyInt())).thenReturn(List.of(keywordGroup));
        when(groupMappingCache.get(any(), (Class<Object>) any()))
                .thenReturn(List.of(uuid));

        when(wrapper.get()).thenReturn(keywordGroupMapping);
        when(groupCache.get(any(UUID.class))).thenReturn(wrapper);
        when(objectMapper.convertValue(any(), eq(KeywordGroupMapping.class))).thenReturn(keywordGroupMapping);
        doNothing().when(groupCache).put(any(), any());
        doNothing().when(groupMappingCache).evict(any());

        eventExecute.updateGroupEvent(updateGroupEvent);
        verify(groupMappingCache, times(1)).put(any(), any());
    }

    @Test
    void 그룹업데이터이벤트실패_getCacheNull() {
        EmbeddingResponse embeddingResponse = new EmbeddingResponse();
        embeddingResponse.setEmbedding(new Float[]{0.9f});
        UpdateGroupEvent updateGroupEvent = new UpdateGroupEvent(embeddingResponse, book);
        when(bookRepository.searchByKeywordGroupVector(any(), anyInt())).thenReturn(List.of(keywordGroup));
        groupCache = Mockito.mock(Cache.class);
        groupMappingCache = Mockito.mock(Cache.class);
        when(redisCacheManager.getCache("group-mapping")).thenReturn(null);
        when(redisCacheManager.getCache("group")).thenReturn(null);
        eventExecute.updateGroupEvent(updateGroupEvent);
        verify(groupMappingCache, times(0)).put(any(), any());
    }

    @Test
    void 북삭제이벤트() {
        UUID uuid = UUID.randomUUID();
        KeywordGroupMapping keywordGroupMapping = new KeywordGroupMapping(uuid, new ArrayList<>(List.of(1L)), "test",
                1);
        when(wrapper.get()).thenReturn(keywordGroupMapping);
        when(groupCache.get(any(UUID.class))).thenReturn(wrapper);
        when(objectMapper.convertValue(any(), eq(KeywordGroupMapping.class))).thenReturn(keywordGroupMapping);

        when(groupMappingCache.get(any(), (Class<Object>) any()))
                .thenReturn(List.of(uuid));
        doNothing().when(groupCache).put(any(), any());
        doNothing().when(groupMappingCache).evict(any());
        eventExecute.deleteBookGroupEvent(new DeleteGroupEvent(1));
        verify(groupCache, times(1)).put(any(), any());
        verify(groupMappingCache, times(1)).evict(any());

    }

    @Test
    void 그룹만료이벤트() {
        UUID uuid = UUID.randomUUID();
        ExpiringGroup expiringGroup = new ExpiringGroup(uuid, Duration.ofMinutes(1));

        KeywordGroupMapping keywordGroupMapping = new KeywordGroupMapping(expiringGroup.getUuid(),
                new ArrayList<>(List.of(1L)), "test",
                1);
        when(keywordGroupRepository.findById(any())).thenReturn(Optional.of(keywordGroup));
        doNothing().when(keywordGroupRepository).delete(any());
        when(wrapper.get()).thenReturn(keywordGroupMapping);
        when(groupCache.get(any(UUID.class))).thenReturn(wrapper);
        when(objectMapper.convertValue(any(), eq(KeywordGroupMapping.class))).thenReturn(keywordGroupMapping);
        when(groupMappingCache.get(any(), (Class<Object>) any()))
                .thenReturn(List.of(uuid));
        doNothing().when(groupMappingCache).put(any(), any());

        eventExecute.expiringGroup(expiringGroup);
        verify(groupMappingCache, times(1)).put(any(), any());

    }

}