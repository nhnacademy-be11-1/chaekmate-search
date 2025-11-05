package shop.chaekmate.search.service;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
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

@Service
@RequiredArgsConstructor
public class BookIndexService {
    private final AiApiClient aiApiClient;
    private final BookRepository bookRepository;
    private final ApplicationEventPublisher publisher;

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
                .bookImages(bookInfoRequest.getBookImages())
                .categories(bookInfoRequest.getCategories())
                .publicationDatetime(bookInfoRequest.getPublicationDatetime())
                .tags(bookInfoRequest.getTags())
                .embedding(embedding)
                .build();
    }


    public Book update(BookInfoRequest bookInfoRequest) {
        Book bookIndex = Valid.isBook(bookRepository.findById(bookInfoRequest.getId()));
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
