package shop.chaekmate.search.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import shop.chaekmate.search.api.AiApiClient;
import shop.chaekmate.search.document.Book;
import shop.chaekmate.search.dto.SearchResponse;
import shop.chaekmate.search.repository.BookRepository;

@Service
@RequiredArgsConstructor
public class SearchService {
    private final BookRepository bookRepository;
    private final AiApiClient aiApiClient;
    private final ObjectMapper objectMapper;

    public List<SearchResponse> getSearch(String keyword) throws JsonProcessingException {
        List<Book> keywordBooks = bookRepository.searchByKeyword(keyword);
        List<Book> vectorBooks = bookRepository.searchByVector(aiApiClient.createEmbedding(keyword));
        String keywordJson = objectMapper.writeValueAsString(keywordBooks.stream().map(Book::toJson).toList());

        String vectorJson = objectMapper.writeValueAsString(vectorBooks.stream().map(Book::toJson).toList());

        String searchResults = aiApiClient.createSearch(keyword, keywordJson, vectorJson);
        List<Long> ids = objectMapper.readValue(
                searchResults,
                new TypeReference<>() {
                });

        return searchResultsBooks(ids);
    }

    private List<SearchResponse> searchResultsBooks(List<Long> ids) {
        List<Book> books = bookRepository.searchByBookIds(ids);
        List<SearchResponse> responses = new ArrayList<>();
        for (Book book : books) {
            responses.add(new SearchResponse(book));
        }
        return responses;
    }
}
