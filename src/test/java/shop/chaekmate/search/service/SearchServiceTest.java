package shop.chaekmate.search.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import shop.chaekmate.search.api.AiApiClient;
import shop.chaekmate.search.document.Book;
import shop.chaekmate.search.dto.EmbeddingResponse;
import shop.chaekmate.search.dto.SearchResponse;
import shop.chaekmate.search.repository.BookRepository;

@ExtendWith(MockitoExtension.class)
class SearchServiceTest {
    @Mock
    BookRepository bookRepository;
    @Mock
    AiApiClient aiApiClient;
    @Mock
    ObjectMapper objectMapper;
    @InjectMocks
    SearchService searchService;

    @Test
    void getSearch() throws JsonProcessingException {
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

        List<SearchResponse> books = searchService.search(keyword);
        assertEquals(1, books.size());
        verify(bookRepository).searchByKeyword(keyword);
        verify(bookRepository).searchByVector(any());
        verify(aiApiClient).createEmbedding(keyword);
        verify(aiApiClient).createSearch(eq(keyword), anyString(), anyString());
    }
}