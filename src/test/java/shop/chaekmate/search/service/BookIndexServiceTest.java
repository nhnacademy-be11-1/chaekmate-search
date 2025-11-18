package shop.chaekmate.search.service;


import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;
import shop.chaekmate.search.api.AiApiClient;
import shop.chaekmate.search.document.Book;
import shop.chaekmate.search.dto.BookDeleteRequest;
import shop.chaekmate.search.dto.BookInfoRequest;
import shop.chaekmate.search.dto.EmbeddingResponse;
import shop.chaekmate.search.event.DeleteGroupEvent;
import shop.chaekmate.search.repository.BookRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BookIndexServiceTest {
    @Mock
    BookRepository bookRepository;
    @Mock
    AiApiClient aiApiClient;
    @Mock
    ApplicationEventPublisher publisher;
    @InjectMocks
    BookIndexService bookIndexService;

    @Test
    void 삽입() {
        BookInfoRequest bookInfoRequest = new BookInfoRequest();
        bookInfoRequest.setId(1);
        bookInfoRequest.setAuthor("test");
        bookInfoRequest.setBookImages("test");
        bookInfoRequest.setCategories(List.of());
        bookInfoRequest.setTags(List.of());
        bookInfoRequest.setDescription("test");
        bookInfoRequest.setPrice(1000);

        when(bookRepository.findById(any())).thenReturn(Optional.empty());
        EmbeddingResponse embeddingResponse = new EmbeddingResponse();
        embeddingResponse.setEmbedding(new Float[]{0.1F, 0.2F});
        when(aiApiClient.createEmbedding(anyString())).thenReturn(embeddingResponse);
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
                .publicationDatetime(LocalDate.now())
                .embedding(new Float[]{0.1f, 0.2f, 0.3f})
                .build();

        BookInfoRequest bookInfoRequest = new BookInfoRequest();
        bookInfoRequest.setId(1);
        bookInfoRequest.setAuthor("test");
        bookInfoRequest.setBookImages("test");
        bookInfoRequest.setCategories(List.of());
        bookInfoRequest.setTags(List.of());
        bookInfoRequest.setDescription("test");
        bookInfoRequest.setPrice(1000);
        when(bookRepository.findById(any())).thenReturn(Optional.of(book));
        EmbeddingResponse embeddingResponse = new EmbeddingResponse();
        embeddingResponse.setEmbedding(new Float[]{0.1F, 0.2F});
        when(aiApiClient.createEmbedding(anyString())).thenReturn(embeddingResponse);

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
                .publicationDatetime(LocalDate.now())
                .embedding(new Float[]{0.1f, 0.2f, 0.3f})
                .build();
        when(bookRepository.findById(any())).thenReturn(Optional.of(book));
        doNothing().when(bookRepository).delete(anyLong());
        bookIndexService.delete(new BookDeleteRequest());
        verify(bookRepository, times(1)).delete(anyLong());
        verify(publisher, times(1)).publishEvent(any(DeleteGroupEvent.class));
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
                .publicationDatetime(LocalDate.now())
                .embedding(new Float[]{0.1f, 0.2f, 0.3f})
                .build());
        doNothing().when(bookRepository).saveAll(anyList());
        bookIndexService.saveAll(mockList);
        verify(bookRepository, times(1)).saveAll(any());
    }
}