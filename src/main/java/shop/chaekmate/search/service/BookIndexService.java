package shop.chaekmate.search.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import shop.chaekmate.search.api.AiApiClient;
import shop.chaekmate.search.common.EmbeddingTextBuilder;
import shop.chaekmate.search.common.Valid;
import shop.chaekmate.search.document.Book;
import shop.chaekmate.search.dto.BookDeleteRequest;
import shop.chaekmate.search.dto.BookInfoRequest;
import shop.chaekmate.search.repository.BookRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BookIndexService {
    private final AiApiClient aiApiClient;
    private final BookRepository bookRepository;

    @Transactional
    public Book insert(BookInfoRequest bookInfoRequest) {
        Valid.existBook(bookRepository.findById(bookInfoRequest.getId()));
        String text = EmbeddingTextBuilder.toText(bookInfoRequest);
        Float[] embedding = aiApiClient.createEmbedding(text).getEmbedding();

        Book bookIndex = Book.builder()
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
        return bookIndex;
    }


    public Book update(BookInfoRequest bookInfoRequest) {
        Book bookIndex = Valid.isBook(bookRepository.findById(bookInfoRequest.getId()));
        String text = EmbeddingTextBuilder.toText(bookInfoRequest);
        Float[] embedding = aiApiClient.createEmbedding(text).getEmbedding();
        bookIndex.update(bookInfoRequest, embedding);
        return bookIndex;

    }

    public void delete(BookDeleteRequest bookDeleteRequest) {
        Book bookIndex = Valid.isBook(bookRepository.findById(bookDeleteRequest.getId()));
        bookRepository.delete(bookIndex);
    }

    public Void saveAll(List<Book> bookList) {
        bookRepository.saveAll(bookList);
        return null;
    }
}
