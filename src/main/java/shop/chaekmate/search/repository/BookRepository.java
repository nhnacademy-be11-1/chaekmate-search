package shop.chaekmate.search.repository;

import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import shop.chaekmate.search.document.Book;

public interface BookRepository extends ElasticsearchRepository<Book, Long> , BookRepositoryCustom {
}
