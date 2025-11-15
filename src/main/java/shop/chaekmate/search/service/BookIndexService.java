package shop.chaekmate.search.service;

import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.retry.support.RetryTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import shop.chaekmate.search.api.AiApiClient;
import shop.chaekmate.search.common.EmbeddingTextBuilder;
import shop.chaekmate.search.common.Valid;
import shop.chaekmate.search.document.Book;
import shop.chaekmate.search.dto.BookDeleteRequest;
import shop.chaekmate.search.dto.BookInfoRequest;
import shop.chaekmate.search.dto.EmbeddingResponse;
import shop.chaekmate.search.event.DeleteGroupEvent;
import shop.chaekmate.search.event.UpdateGroupEvent;
import shop.chaekmate.search.repository.BookRepository;
import java.util.List;

@Service
@RequiredArgsConstructor
public class BookIndexService {
    private final AiApiClient aiApiClient;
    private final BookRepository bookRepository;
    private final ApplicationEventPublisher publisher;

    private final RetryTemplate retryTemplate;

    @Transactional
    public Book insert(BookInfoRequest bookInfoRequest) {
        Valid.existBook(bookRepository.findById(bookInfoRequest.getId()));
        String text = EmbeddingTextBuilder.toText(bookInfoRequest);
        Float[] embedding = aiApiClient.createEmbedding(text).getEmbedding();

        return Book.builder()
                .id(bookInfoRequest.getId())
                .title(bookInfoRequest.getTitle())
                .author(bookInfoRequest.getAuthor())
                .price(bookInfoRequest.getPrice())
                .description(bookInfoRequest.getDescription())
                .isbn(bookInfoRequest.getIsbn())
                .publisher(bookInfoRequest.getPublisher())
                .bookImages(bookInfoRequest.getBookImages())
                .categories(bookInfoRequest.getCategories())
                .publicationDatetime(bookInfoRequest.getPublicationDatetime())
                .tags(bookInfoRequest.getTags())
                .reviewSummary(bookInfoRequest.getReviewSummary())
                .reviewCnt(bookInfoRequest.getReviewCnt())
                .rating(bookInfoRequest.getRating())
                .embedding(embedding)
                .build();
    }


    @Transactional
    public Book update(BookInfoRequest bookInfoRequest) {

        // RetryTemplate 사용 (책 생성 시 인덱싱 전에 썸네일 등록 요청)
        Book bookIndex = retryTemplate.execute(context ->
             bookRepository.findById(bookInfoRequest.getId())
                    .orElseThrow(() -> new IllegalArgumentException("Book not found: " + bookInfoRequest.getId()))
        );

        String text = EmbeddingTextBuilder.toText(bookInfoRequest);
        EmbeddingResponse embeddingResponse = aiApiClient.createEmbedding(text);
        bookIndex.update(bookInfoRequest, embeddingResponse.getEmbedding());
        publisher.publishEvent(new UpdateGroupEvent(embeddingResponse, bookIndex));
        return bookIndex;
    }


    public void delete(BookDeleteRequest bookDeleteRequest) {
        Book bookIndex = Valid.isBook(bookRepository.findById(bookDeleteRequest.getId()));
        bookRepository.delete(bookIndex);
        publisher.publishEvent(new DeleteGroupEvent(bookIndex.getId()));
    }

    @Transactional
    public void saveAll(List<Book> bookList) {
        bookRepository.saveAll(bookList);
    }

}
