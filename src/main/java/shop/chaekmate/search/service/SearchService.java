package shop.chaekmate.search.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.Cache;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
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

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.springframework.cache.Cache.ValueWrapper;

@Service
@RequiredArgsConstructor
public class SearchService {
    private final BookRepository bookRepository;
    private final AiApiClient aiApiClient;
    private final ObjectMapper objectMapper;
    private final RedisCacheManager redisCacheManager;
    private final ApplicationEventPublisher publisher;

    public Page<SearchResponse> search(String keyword, Pageable pageable) throws JsonProcessingException {
        EmbeddingResponse embeddingResponse = aiApiClient.createEmbedding(keyword);
        List<KeywordGroup> keywordGroups = bookRepository.searchByKeywordGroupVector(embeddingResponse, 20);
        if (!keywordGroups.isEmpty()) {
            Cache cache = redisCacheManager.getCache("group");
            if (cache != null) {
                KeywordGroupMapping mapping = toKeywordGroupMapping(cache, keywordGroups.getFirst().id);
                if (mapping != null) {
                    List<Book> books = resultsBooks(mapping.getIds(),pageable);
                    return responseBooks(books,pageable,mapping.getIds().size());
                }
            }
        }
        List<Book> keywordBooks = bookRepository.searchByKeyword(keyword);
        List<Book> vectorBooks = bookRepository.searchByVector(embeddingResponse);

        String keywordJson = objectMapper.writeValueAsString(keywordBooks.stream().map(Book::toJson).toList());
        String vectorJson = objectMapper.writeValueAsString(vectorBooks.stream().map(Book::toJson).toList());

        String searchResults = aiApiClient.createSearch(keyword, keywordJson, vectorJson);
        List<Long> ids = objectMapper.readValue(
                searchResults,
                new TypeReference<>() {
                });
        List<Book> books = resultsBooks(ids,pageable);
        publisher.publishEvent(new CreateGroupEvent(keyword, books, ids, embeddingResponse.getEmbedding()));
        return responseBooks(books,pageable,ids.size());
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

    private List<Book> resultsBooks(List<Long> ids,Pageable pageable) {
        return bookRepository.searchByBookIds(ids,pageable);
    }

    private Page<SearchResponse> responseBooks(List<Book> books,Pageable pageable , int totalCnt) {
        List<SearchResponse> responses = new ArrayList<>();
        for (Book book : books) {
            responses.add(new SearchResponse(book));
        }
        return new PageImpl<>(responses,pageable,totalCnt);
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