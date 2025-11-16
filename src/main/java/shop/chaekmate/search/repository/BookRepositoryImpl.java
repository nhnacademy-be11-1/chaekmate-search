package shop.chaekmate.search.repository;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch._types.FieldValue;
import co.elastic.clients.elasticsearch._types.Refresh;
import co.elastic.clients.elasticsearch._types.SortOrder;
import co.elastic.clients.elasticsearch._types.query_dsl.Operator;
import co.elastic.clients.elasticsearch.core.BulkRequest;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import co.elastic.clients.elasticsearch.core.search.Hit;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import shop.chaekmate.search.document.Book;
import shop.chaekmate.search.document.KeywordGroup;
import shop.chaekmate.search.dto.EmbeddingResponse;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

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
    public List<KeywordGroup> searchByKeywordGroupVector(Float[] vector, int k) {

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
                    .filter(hit -> hit.score() != null && hit.score() >= 0.89f)
                    .sorted((a, b) -> Double.compare(b.score(), a.score()))
                    .limit(k)
                    .map(Hit::source)
                    .toList();


        } catch (IOException e) {
            throw new RuntimeException("Vector search failed", e);
        }
    }

    @Override
    public void saveAll(List<Book> books) {
        BulkRequest.Builder bulk = new BulkRequest.Builder();
        for (Book book : books) {
            bulk.operations(op -> op
                    .index(idx -> idx
                            .index(BOOK_INDEX)
                            .id(String.valueOf(book.getId()))
                            .document(book)
                    )
            );
        }
        bulk.refresh(Refresh.WaitFor);
        try {
            client.bulk(bulk.build());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void delete(Long id) {
        try {
            client.delete(d -> d
                    .index(BOOK_INDEX)
                    .id(String.valueOf(id))
                    .refresh(Refresh.WaitFor)
            );
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
