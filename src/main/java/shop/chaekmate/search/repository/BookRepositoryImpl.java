package shop.chaekmate.search.repository;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import co.elastic.clients.elasticsearch.core.search.Hit;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import shop.chaekmate.search.document.Book;

import java.io.IOException;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class BookRepositoryImpl implements BookRepositoryCustom{
    private final ElasticsearchClient client;

    @Override
    public List<Book> searchByKeyword(String keyword) {
        try {
            SearchResponse<Book> res = client.search(s -> s
                            .index("books")
                            .query(q -> q
                                    .multiMatch(m -> m
                                            .query(keyword)
                                            .fields("title^100", "description^10", "tags^50")
                                    )
                            ),
                    Book.class
            );
            return res.hits().hits().stream()
                    .map(Hit::source)
                    .toList();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
