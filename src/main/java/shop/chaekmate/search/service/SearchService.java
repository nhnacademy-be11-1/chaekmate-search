package shop.chaekmate.search.service;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import shop.chaekmate.search.dto.SearchResponse;
import shop.chaekmate.search.repository.BookRepository;

@Service
@RequiredArgsConstructor
public class SearchService {
    private final BookRepository bookRepository;
    public List<SearchResponse> getSearch(String keyword) {
        return null;
    }
}
