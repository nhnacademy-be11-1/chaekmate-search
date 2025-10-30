package shop.chaekmate.search.service;


import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import shop.chaekmate.search.api.AiApiClient;
import shop.chaekmate.search.document.Book;
import shop.chaekmate.search.dto.BookDeleteRequest;
import shop.chaekmate.search.dto.BookInfoRequest;
import shop.chaekmate.search.dto.EmbeddingResponse;
import shop.chaekmate.search.repository.BookRepository;

@ExtendWith(MockitoExtension.class)
class BookIndexServiceTest {
    @Mock
    BookRepository bookRepository;
    @Mock
    AiApiClient aiApiClient;
    @InjectMocks
    BookIndexService bookIndexService;

    @Test
    void 삽입() {
        BookInfoRequest bookInfoRequest = new BookInfoRequest();
        bookInfoRequest.setId(1);
        bookInfoRequest.setAuthor("test");
        bookInfoRequest.setBookImages(List.of());
        bookInfoRequest.setCategories(List.of());
        bookInfoRequest.setTags(List.of());
        bookInfoRequest.setDescription("test");
        bookInfoRequest.setPrice(1000);

        Mockito.when(bookRepository.findById(any())).thenReturn(Optional.empty());
        EmbeddingResponse embeddingResponse = new EmbeddingResponse();
        embeddingResponse.setEmbedding(new Float[]{0.1F, 0.2F});
        Mockito.when(aiApiClient.createEmbedding(anyString())).thenReturn(embeddingResponse);
        Book book = bookIndexService.insert(bookInfoRequest);
        Assertions.assertEquals(1, (long) book.getId());

    }

    @Test
    void 업데이트() {
        Book book = Book.builder()
                .id(1L)
                .title("zzzzz")
                .author("zzz")
                .price(20000)
                .description("zzzzz")
                .categories(List.of("zz", "zzz"))
                .tags(List.of("zzz", "zzz"))
                .publicationDatetime(LocalDateTime.now())
                .embedding(new Float[]{0.1f, 0.2f, 0.3f})
                .build();

        BookInfoRequest bookInfoRequest = new BookInfoRequest();
        bookInfoRequest.setId(1);
        bookInfoRequest.setAuthor("test");
        bookInfoRequest.setBookImages(List.of());
        bookInfoRequest.setCategories(List.of());
        bookInfoRequest.setTags(List.of());
        bookInfoRequest.setDescription("test");
        bookInfoRequest.setPrice(1000);
        Mockito.when(bookRepository.findById(any())).thenReturn(Optional.of(book));
        EmbeddingResponse embeddingResponse = new EmbeddingResponse();
        embeddingResponse.setEmbedding(new Float[]{0.1F, 0.2F});
        Mockito.when(aiApiClient.createEmbedding(anyString())).thenReturn(embeddingResponse);

        Book result = bookIndexService.update(bookInfoRequest);
        Assertions.assertEquals("zzzzz", result.getTitle());

    }

    @Test
    void 삭제() {
        Book book = Book.builder()
                .id(1L)
                .title("zzzzz")
                .author("zzz")
                .price(20000)
                .description("zzzzz")
                .categories(List.of("zz", "zzz"))
                .tags(List.of("zzz", "zzz"))
                .publicationDatetime(LocalDateTime.now())
                .embedding(new Float[]{0.1f, 0.2f, 0.3f})
                .build();
        Mockito.when(bookRepository.findById(any())).thenReturn(Optional.of(book));
        Mockito.doNothing().when(bookRepository).delete(any());
        bookIndexService.delete(new BookDeleteRequest());
        Mockito.verify(bookRepository, Mockito.times(1)).delete(any());
    }

    @Test
    void 세이브() {
        List<Book> mockList = List.of(Book.builder()
                .id(1L)
                .title("zzzzz")
                .author("zzz")
                .price(20000)
                .description("zzzzz")
                .categories(List.of("zz", "zzz"))
                .tags(List.of("zzz", "zzz"))
                .publicationDatetime(LocalDateTime.now())
                .embedding(new Float[]{0.1f, 0.2f, 0.3f})
                .build());
        Mockito.when(bookRepository.saveAll(any())).thenReturn(mockList);
        bookIndexService.saveAll(List.of());
        Mockito.verify(bookRepository, Mockito.times(1)).saveAll(any());
    }
}