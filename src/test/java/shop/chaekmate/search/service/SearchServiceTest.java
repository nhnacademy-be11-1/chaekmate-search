package shop.chaekmate.search.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.LocalDate;
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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.redis.cache.RedisCacheManager;
import shop.chaekmate.search.api.AiApiClient;
import shop.chaekmate.search.document.Book;
import shop.chaekmate.search.document.KeywordGroup;
import shop.chaekmate.search.document.KeywordGroupMapping;
import shop.chaekmate.search.dto.EmbeddingResponse;
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
        Pageable pageable = PageRequest.of(0, 10);

        List<Book> keywordResults = List.of(Book.builder()
                .id(1L)
                .title("zzz")
                .author("zzz")
                .price(18000)
                .description("zzzzz")
                .categories(List.of("zz", "zzz"))
                .tags(List.of("zzz", "zzz"))
                .publicationDatetime(LocalDate.now())
                .publisher("")
                .isbn("")
                .rating(0.0)
                .reviewCnt(1)
                .reviewSummary("test")

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
                .publicationDatetime(LocalDate.now())
                .embedding(new Float[]{0.1f, 0.2f, 0.3f})
                .build());
        EmbeddingResponse embedding = new EmbeddingResponse();
        embedding.setEmbedding(new Float[]{0.1f, 0.2f, 0.3f});

        when(bookRepository.searchByKeyword(any())).thenReturn(keywordResults);
        when(bookRepository.searchByVector(any())).thenReturn(vectorResults);
        when(aiApiClient.createEmbedding(keyword)).thenReturn(embedding);
        when(objectMapper.writeValueAsString(any())).thenReturn("[{\"id\":1}]");
        when(bookRepository.searchByBookIds(any(),any())).thenReturn(keywordResults);
        String rerankResponse = "[1]";
        when(aiApiClient.createSearch(eq(keyword), anyString(), anyString()))
                .thenReturn(rerankResponse);
        when(aiApiClient.rerank(anyString(),anyList())).thenReturn(vectorResults);

        when(objectMapper.readValue(eq(rerankResponse), any(TypeReference.class)))
                .thenReturn(List.of(1L));
        doNothing().when(publisher).publishEvent(any(CreateGroupEvent.class));
        Page<SearchResponse> books = searchService.search(keyword, pageable);
        assertEquals(1, books.getTotalElements());
        verify(bookRepository).searchByKeyword(keyword);
        verify(bookRepository).searchByVector(any());
        verify(aiApiClient).createEmbedding(keyword);
        verify(aiApiClient).createSearch(eq(keyword), anyString(), anyString());
        verify(publisher).publishEvent(any(CreateGroupEvent.class));
    }

    @Test
    void 검색_캐시히트() throws JsonProcessingException {
        Pageable pageable = PageRequest.of(0, 10);

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
                .reviewCnt(0)
                .publicationDatetime(LocalDate.now())
                .embedding(new Float[]{0.1f, 0.2f, 0.3f})
                .build());
        Cache groupCache = mock(Cache.class);
        UUID uuid = UUID.randomUUID();
        KeywordGroup keywordGroup = KeywordGroup.builder().id(uuid).embedding(new Float[]{0.9f}).build();
        KeywordGroupMapping keywordGroupMapping = new KeywordGroupMapping(uuid, List.of(1L, 2L),  1);
        when(bookRepository.searchByKeywordGroupVector(any(), anyInt())).thenReturn(List.of(keywordGroup));
        when(aiApiClient.createEmbedding(keyword)).thenReturn(embedding);
        when(redisCacheManager.getCache(anyString())).thenReturn(groupCache);
        when(groupCache.get(any())).thenReturn(wrapper);
        when(wrapper.get()).thenReturn(keywordGroupMapping);
        when(bookRepository.searchByBookIds(any(),any())).thenReturn(keywordResults);
        when(objectMapper.convertValue(any(), eq(KeywordGroupMapping.class))).thenReturn(keywordGroupMapping);

        Page<SearchResponse> books = searchService.search(keyword, pageable);
        assertEquals(1, books.getTotalElements());
        verify(aiApiClient).createEmbedding(keyword);

    }

}