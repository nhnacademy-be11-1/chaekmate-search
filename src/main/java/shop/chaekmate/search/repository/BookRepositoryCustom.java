package shop.chaekmate.search.repository;

import shop.chaekmate.search.document.Book;

import java.util.List;

public interface BookRepositoryCustom {
    List<Book> searchByKeyword(String keyword);

}
