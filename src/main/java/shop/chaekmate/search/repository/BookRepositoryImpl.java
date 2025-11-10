package shop.chaekmate.search.repository;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch._types.FieldValue;
import co.elastic.clients.elasticsearch._types.SortOrder;
import co.elastic.clients.elasticsearch._types.query_dsl.Operator;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import co.elastic.clients.elasticsearch.core.search.Hit;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import shop.chaekmate.search.document.Book;
import shop.chaekmate.search.document.KeywordGroup;
import shop.chaekmate.search.dto.EmbeddingResponse;

@Repository
@RequiredArgsConstructor
public class BookRepositoryImpl implements BookRepositoryCustom {
    private final ElasticsearchClient client;
    private static final String BOOK_INDEX = "books";
    private static final String KEYWORD_GROUP_INDEX = "keywordgroups";

    @Override
    public List<Book> searchByKeyword(String keyword) {
        try {
            SearchResponse<Book> response = client.search(s -> s
                            .index(BOOK_INDEX)
                            .query(q -> q
                                    .multiMatch(m -> m
                                            .query(keyword)
                                            .fields("title^100", "description^10", "tags^50")
                                            .analyzer("korean_english_analyzer")
                                            .operator(Operator.Or)
                                    )
                            )
                            .size(100),

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
                            .index(BOOK_INDEX)
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
    public List<Book> searchByBookIds(List<Long> ids, Pageable pageable) {
        try {
            SearchResponse<Book> response = client.search(s -> {
                s.index(BOOK_INDEX)
                        .query(q -> q
                                .terms(t -> t
                                        .field("id")
                                        .terms(ts -> ts.value(ids.stream()
                                                .map(FieldValue::of)
                                                .toList()))
                                )
                        )
                        .from(pageable.getPageNumber() * pageable.getPageSize())
                        .size(pageable.getPageSize());
                pageable.getSort().stream().findFirst().ifPresent(order ->
                        s.sort(so -> so.field(f -> f
                                .field(order.getProperty())
                                .order(order.isAscending() ? SortOrder.Asc : SortOrder.Desc)
                        ))
                );
                return s;
            }, Book.class);
            return response.hits().hits().stream()
                    .map(Hit::source)
                    .toList();

        } catch (IOException e) {
            throw new RuntimeException("Book search failed", e);
        }
    }

    @Override
    public List<KeywordGroup> searchByKeywordGroupVector(EmbeddingResponse embedding, int k) {
        Float[] vector = embedding.getEmbedding();

        try {
            SearchResponse<KeywordGroup> response = client.search(s -> s
                            .index(KEYWORD_GROUP_INDEX)
                            .knn(knn -> knn
                                    .field("embedding")
                                    .queryVector(Arrays.asList(vector))
                                    .k(k)
                                    .numCandidates(100)
                            ),
                    KeywordGroup.class
            );

            return response.hits().hits().stream()
                    .filter(hit -> hit.score() != null && hit.score() >= 0.78f)
                    .sorted((a, b) -> Double.compare(b.score(), a.score()))
                    .limit(k)
                    .map(Hit::source)
                    .toList();


        } catch (IOException e) {
            throw new RuntimeException("Vector search failed", e);
        }
    }

}
