package shop.chaekmate.search.service;

import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import shop.chaekmate.search.document.Book;
import shop.chaekmate.search.dto.SearchResponse;
import shop.chaekmate.search.repository.BookRepository;

@Service
@RequiredArgsConstructor
public class SearchService {
    private final BookRepository bookRepository;
    public List<SearchResponse> getSearch(String keyword) {
        List<Book> books = bookRepository.searchByKeyword(keyword);
        List<SearchResponse> searchResponses = new ArrayList<>();
        for (Book book : books) {
            searchResponses.add(new SearchResponse(book));
        }
        return searchResponses;
    }
}
