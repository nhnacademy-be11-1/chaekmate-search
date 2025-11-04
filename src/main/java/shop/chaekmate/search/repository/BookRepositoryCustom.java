package shop.chaekmate.search.repository;

import shop.chaekmate.search.document.Book;

import java.util.List;
import shop.chaekmate.search.dto.EmbeddingResponse;

public interface BookRepositoryCustom {
    List<Book> searchByKeyword(String keyword);

    List<Book> searchByVector(EmbeddingResponse embedding);
    List<Book> searchByBookIds(List<Long> ids);
}
