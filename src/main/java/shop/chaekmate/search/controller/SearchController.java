package shop.chaekmate.search.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import shop.chaekmate.search.controller.docs.SearchControllerDocs;
import shop.chaekmate.search.dto.RecommendKeywordResponse;
import shop.chaekmate.search.dto.SearchResponse;
import shop.chaekmate.search.service.SearchService;

@RestController
@RequestMapping("/search")
@RequiredArgsConstructor
public class SearchController implements SearchControllerDocs {
    private final SearchService searchService;

    @GetMapping
    public ResponseEntity<List<SearchResponse>> search(@RequestParam String prompt) throws JsonProcessingException {
        return ResponseEntity.ok(searchService.search(prompt));
    }

    @GetMapping("/recommendKeyword")
    public ResponseEntity<RecommendKeywordResponse> recommendKeyword(@RequestParam String prompt) {
        return ResponseEntity.ok(searchService.recommendKeyword(prompt));
    }

}
