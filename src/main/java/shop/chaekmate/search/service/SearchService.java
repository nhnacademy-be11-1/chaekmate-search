package shop.chaekmate.search.service;

import static org.springframework.cache.Cache.ValueWrapper;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.Cache;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.stereotype.Service;
import shop.chaekmate.search.api.AiApiClient;
import shop.chaekmate.search.document.Book;
import shop.chaekmate.search.document.KeywordGroup;
import shop.chaekmate.search.document.KeywordGroupMapping;
import shop.chaekmate.search.dto.EmbeddingResponse;
import shop.chaekmate.search.dto.RecommendKeywordResponse;
import shop.chaekmate.search.dto.SearchResponse;
import shop.chaekmate.search.event.CreateGroupEvent;
import shop.chaekmate.search.repository.BookRepository;

@Service
@RequiredArgsConstructor
public class SearchService {
    private final BookRepository bookRepository;
    private final AiApiClient aiApiClient;
    private final ObjectMapper objectMapper;
    private final RedisCacheManager redisCacheManager;
    private final ApplicationEventPublisher publisher;

    public List<SearchResponse> search(String keyword) throws JsonProcessingException {
        EmbeddingResponse embeddingResponse = aiApiClient.createEmbedding(keyword);
        List<KeywordGroup> keywordGroups = bookRepository.searchByKeywordGroupVector(embeddingResponse, 20);
        if (!keywordGroups.isEmpty()) {
            Cache cache = redisCacheManager.getCache("group");
            if (cache != null) {
                KeywordGroupMapping mapping = toKeywordGroupMapping(cache, keywordGroups.getFirst().id);
                if (mapping != null) {
                    List<Book> books = resultsBooks(mapping.getIds());
                    return responseBooks(books);
                }
            }
        }
        List<Book> vectorBooks = bookRepository.searchByVector(embeddingResponse);
        List<Book> keywordBooks = bookRepository.searchByKeyword(keyword);

        String keywordJson = objectMapper.writeValueAsString(keywordBooks.stream().map(Book::toJson).toList());
        String vectorJson = objectMapper.writeValueAsString(vectorBooks.stream().map(Book::toJson).toList());

        String searchResults = aiApiClient.createSearch(keyword, keywordJson, vectorJson);
        List<Long> ids = objectMapper.readValue(
                searchResults,
                new TypeReference<>() {
                });
        List<Book> books = resultsBooks(ids);
        publisher.publishEvent(new CreateGroupEvent(keyword, books, ids, embeddingResponse.getEmbedding()));
        return responseBooks(books);
    }

    public RecommendKeywordResponse recommendKeyword(String keyword) {
        EmbeddingResponse embeddingResponse = aiApiClient.createEmbedding(keyword);
        List<KeywordGroup> keywordGroups = bookRepository.searchByKeywordGroupVector(embeddingResponse, 5);
        List<String> recommendKeyword = new ArrayList<>();
        if (!keywordGroups.isEmpty()) {
            Cache cache = redisCacheManager.getCache("group");
            if (cache != null) {
                for (KeywordGroup keywordGroup : keywordGroups) {
                    KeywordGroupMapping mapping = toKeywordGroupMapping(cache, keywordGroup.getId());
                    if (mapping != null) {
                        recommendKeyword.add(mapping.getGroupName());
                    }
                }
            }
        }
        return new RecommendKeywordResponse(recommendKeyword);

    }

    private List<Book> resultsBooks(List<Long> ids) {
        return bookRepository.searchByBookIds(ids);
    }

    private List<SearchResponse> responseBooks(List<Book> books) {
        List<SearchResponse> responses = new ArrayList<>();
        for (Book book : books) {
            responses.add(new SearchResponse(book));
        }
        return responses;
    }

    private KeywordGroupMapping toKeywordGroupMapping(Cache groupCache, UUID groupId) {
        ValueWrapper wrapper = groupCache.get(groupId);
        if (wrapper == null) {
            return null;
        }
        return objectMapper.convertValue(wrapper.get(),
                KeywordGroupMapping.class);
    }
}