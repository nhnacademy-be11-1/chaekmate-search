package shop.chaekmate.search.repository;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch._types.FieldValue;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import co.elastic.clients.elasticsearch.core.search.Hit;
import java.util.Arrays;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import shop.chaekmate.search.document.Book;

import java.io.IOException;
import java.util.List;
import shop.chaekmate.search.dto.EmbeddingResponse;

@Repository
@RequiredArgsConstructor
public class BookRepositoryImpl implements BookRepositoryCustom {
    private final ElasticsearchClient client;

    @Override
    public List<Book> searchByKeyword(String keyword) {
        try {
            SearchResponse<Book> response = client.search(s -> s
                            .index("books")
                            .query(q -> q
                                    .multiMatch(m -> m
                                            .query(keyword)
                                            .fields("title^100", "description^10", "tags^50")
                                    )
                            ),
                    Book.class
            );
            return response.hits().hits().stream()
                    .map(Hit::source)
                    .toList();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<Book> searchByVector(EmbeddingResponse embedding) {
        Float[] vector = embedding.getEmbedding();

        try {
            SearchResponse<Book> response = client.search(s -> s
                            .index("books")
                            .knn(knn -> knn
                                    .field("embedding")
                                    .queryVector(Arrays.asList(vector))
                                    .k(10)
                                    .numCandidates(100)
                            ),
                    Book.class
            );

            return response.hits().hits().stream()
                    .map(Hit::source)
                    .toList();

        } catch (IOException e) {
            throw new RuntimeException("Vector search failed", e);
        }
    }

    @Override
    public List<Book> searchByBookIds(List<Long> ids) {
        try {
            SearchResponse<Book> response = client.search(s -> s
                            .index("books")
                            .query(q -> q
                                    .terms(t -> t
                                            .field("id")
                                            .terms(ts -> ts.value(ids.stream()
                                                    .map(FieldValue::of)
                                                    .toList()))
                                    )
                            )
                            .size(ids.size()),
                    Book.class
            );
            return response.hits().hits().stream()
                    .map(Hit::source)
                    .toList();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
