package shop.chaekmate.search.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.cache.Cache;
import org.springframework.cache.Cache.ValueWrapper;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.redis.cache.RedisCacheManager;
import shop.chaekmate.search.api.AiApiClient;
import shop.chaekmate.search.document.Book;
import shop.chaekmate.search.document.KeywordGroup;
import shop.chaekmate.search.document.KeywordGroupMapping;
import shop.chaekmate.search.dto.EmbeddingResponse;
import shop.chaekmate.search.dto.RecommendKeywordResponse;
import shop.chaekmate.search.dto.SearchResponse;
import shop.chaekmate.search.event.CreateGroupEvent;
import shop.chaekmate.search.repository.BookRepository;

@ExtendWith(MockitoExtension.class)
class SearchServiceTest {
    @Mock
    BookRepository bookRepository;
    @Mock
    AiApiClient aiApiClient;
    @Mock
    ObjectMapper objectMapper;
    @Mock
    ApplicationEventPublisher publisher;
    @Mock
    RedisCacheManager redisCacheManager;
    @InjectMocks
    SearchService searchService;
    ValueWrapper wrapper;

    @BeforeEach
    void init() {
        wrapper = mock(ValueWrapper.class);
    }

    @Test
    void 검색_캐시미스() throws JsonProcessingException {
        String keyword = "zz";

        List<Book> keywordResults = List.of(Book.builder()
                .id(1L)
                .title("zzz")
                .author("zzz")
                .price(18000)
                .description("zzzzz")
                .categories(List.of("zz", "zzz"))
                .tags(List.of("zzz", "zzz"))
                .publicationDatetime(LocalDateTime.now())
                .embedding(new Float[]{0.1f, 0.2f, 0.3f})
                .build());
        List<Book> vectorResults = List.of(Book.builder()
                .id(1L)
                .title("zzz")
                .author("zzz")
                .price(18000)
                .description("zzzzz")
                .categories(List.of("zz", "zzz"))
                .tags(List.of("zzz", "zzz"))
                .publicationDatetime(LocalDateTime.now())
                .embedding(new Float[]{0.1f, 0.2f, 0.3f})
                .build());
        EmbeddingResponse embedding = new EmbeddingResponse();
        embedding.setEmbedding(new Float[]{0.1f, 0.2f, 0.3f});

        when(bookRepository.searchByKeyword(any())).thenReturn(keywordResults);
        when(bookRepository.searchByVector(any())).thenReturn(vectorResults);
        when(aiApiClient.createEmbedding(keyword)).thenReturn(embedding);
        when(objectMapper.writeValueAsString(any())).thenReturn("[{\"id\":1}]");
        when(bookRepository.searchByBookIds(any())).thenReturn(keywordResults);
        String rerankResponse = "[1]";
        when(aiApiClient.createSearch(eq(keyword), anyString(), anyString()))
                .thenReturn(rerankResponse);

        when(objectMapper.readValue(eq(rerankResponse), any(TypeReference.class)))
                .thenReturn(List.of(1L));
        doNothing().when(publisher).publishEvent(any(CreateGroupEvent.class));
        List<SearchResponse> books = searchService.search(keyword);
        assertEquals(1, books.size());
        verify(bookRepository).searchByKeyword(keyword);
        verify(bookRepository).searchByVector(any());
        verify(aiApiClient).createEmbedding(keyword);
        verify(aiApiClient).createSearch(eq(keyword), anyString(), anyString());
        verify(publisher).publishEvent(any(CreateGroupEvent.class));
    }

    @Test
    void 검색_캐시히트() throws JsonProcessingException {
        EmbeddingResponse embedding = new EmbeddingResponse();
        embedding.setEmbedding(new Float[]{0.1f, 0.2f, 0.3f});
        String keyword = "소설추천해줘";
        List<Book> keywordResults = List.of(Book.builder()
                .id(1L)
                .title("zzz")
                .author("zzz")
                .price(18000)
                .description("zzzzz")
                .categories(List.of("zz", "zzz"))
                .tags(List.of("zzz", "zzz"))
                .publicationDatetime(LocalDateTime.now())
                .embedding(new Float[]{0.1f, 0.2f, 0.3f})
                .build());
        Cache groupCache = mock(Cache.class);
        UUID uuid = UUID.randomUUID();
        KeywordGroup keywordGroup = KeywordGroup.builder().id(uuid).embedding(new Float[]{0.9f}).build();
        KeywordGroupMapping keywordGroupMapping = new KeywordGroupMapping(uuid, List.of(1L, 2L), "소설추천", 1);
        when(bookRepository.searchByKeywordGroupVector(any(), anyInt())).thenReturn(List.of(keywordGroup));
        when(aiApiClient.createEmbedding(keyword)).thenReturn(embedding);
        when(redisCacheManager.getCache(anyString())).thenReturn(groupCache);
        when(groupCache.get(any())).thenReturn(wrapper);
        when(wrapper.get()).thenReturn(keywordGroupMapping);
        when(bookRepository.searchByBookIds(any())).thenReturn(keywordResults);
        when(objectMapper.convertValue(any(), eq(KeywordGroupMapping.class))).thenReturn(keywordGroupMapping);

        List<SearchResponse> books = searchService.search(keyword);
        assertEquals(1, books.size());
        verify(aiApiClient).createEmbedding(keyword);

    }

    @Test
    void 키워드_추천() {
        String keyword = "소설";
        UUID uuid = UUID.randomUUID();
        List<KeywordGroup> keywordGroups = List.of(
                KeywordGroup.builder().id(uuid).embedding(new Float[]{0.1F}).build());
        KeywordGroupMapping keywordGroupMapping = new KeywordGroupMapping(uuid, List.of(1L, 2L), "소설추천",
                1);
        Cache groupCache = mock(Cache.class);
        when(redisCacheManager.getCache(anyString())).thenReturn(groupCache);

        EmbeddingResponse embedding = new EmbeddingResponse();
        embedding.setEmbedding(new Float[]{0.1f, 0.2f, 0.3f});
        when(aiApiClient.createEmbedding(keyword)).thenReturn(embedding);
        when(bookRepository.searchByKeywordGroupVector(any(), anyInt())).thenReturn(keywordGroups);
        when(wrapper.get()).thenReturn(keywordGroupMapping);
        when(groupCache.get(any(UUID.class))).thenReturn(wrapper);
        when(objectMapper.convertValue(any(), eq(KeywordGroupMapping.class))).thenReturn(keywordGroupMapping);
        RecommendKeywordResponse recommendKeywordResponse = searchService.recommendKeyword(keyword);
        assertEquals(1, recommendKeywordResponse.recommendKeyword().size());
        assertEquals("소설추천", recommendKeywordResponse.recommendKeyword().getFirst());
    }

}