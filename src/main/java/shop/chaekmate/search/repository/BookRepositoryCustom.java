package shop.chaekmate.search.repository;

import org.springframework.data.domain.Pageable;
import shop.chaekmate.search.document.Book;
import shop.chaekmate.search.document.KeywordGroup;
import shop.chaekmate.search.dto.EmbeddingResponse;

import java.util.List;

public interface BookRepositoryCustom {
    List<Book> searchByKeyword(String keyword);

    List<Book> searchByVector(EmbeddingResponse embedding);

    List<Book> searchByBookIds(List<Long> ids, Pageable pageable);

    List<KeywordGroup> searchByKeywordGroupVector(Float[] embedding,int k );
    void saveAll(List<Book> books);
    void delete(Long id);
}
