package shop.chaekmate.search.controller;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import shop.chaekmate.search.dto.SearchResponse;
import shop.chaekmate.search.service.SearchService;

@RestController
@RequestMapping("/search")
@RequiredArgsConstructor
public class SearchController {
    private final SearchService searchService;

    @GetMapping
    ResponseEntity<List<SearchResponse>> getSearch(@RequestParam String prompt){
        return ResponseEntity.ok(searchService.getSearch(prompt));
    }


}
