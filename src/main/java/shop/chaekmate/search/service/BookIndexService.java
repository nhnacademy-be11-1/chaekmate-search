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

@Service
@RequiredArgsConstructor
public class BookIndexService {
    private final AiApiClient aiApiClient;
    private final BookRepository bookRepository;

    @Transactional
    public void insert(BookInfoRequest bookInfoRequest) {
        Valid.existBook(bookRepository.findById(bookInfoRequest.getId()));
        String text = EmbeddingTextBuilder.toText(bookInfoRequest);
        float[] embedding = aiApiClient.createEmbedding(text).getEmbedding();

        Book bookIndex = Book.builder()
                .id(bookInfoRequest.getId())
                .title(bookInfoRequest.getTitle())
                .author(bookInfoRequest.getAuthor())
                .price(bookInfoRequest.getPrice())
                .description(bookInfoRequest.getDescription())
                .categories(bookInfoRequest.getCategories())
                .publicationDatetime(bookInfoRequest.getPublicationDatetime())
                .tags(bookInfoRequest.getTags())
                .embedding(embedding)
                .build();
        bookRepository.save(bookIndex);
    }


    public void update(BookInfoRequest bookInfoRequest) {
        Book bookIndex = Valid.isBook(bookRepository.findById(bookInfoRequest.getId()));
        String text = EmbeddingTextBuilder.toText(bookInfoRequest);
        float[] embedding = aiApiClient.createEmbedding(text).getEmbedding();
        bookIndex.update(bookInfoRequest, embedding);
        bookRepository.save(bookIndex);

    }

    public void delete(BookDeleteRequest bookDeleteRequest) {
        Book bookIndex = Valid.isBook(bookRepository.findById(bookDeleteRequest.getId()));
        bookRepository.delete(bookIndex);
    }
}
