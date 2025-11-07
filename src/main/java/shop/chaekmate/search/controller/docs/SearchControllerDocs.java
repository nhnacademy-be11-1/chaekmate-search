package shop.chaekmate.search.controller.docs;

import com.fasterxml.jackson.core.JsonProcessingException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import shop.chaekmate.search.dto.RecommendKeywordResponse;
import shop.chaekmate.search.dto.SearchResponse;

@Tag(name = "검색 API", description = "검색 API")
public interface SearchControllerDocs {
    @Operation(summary = "검색", description = "검색")
    @ApiResponse(responseCode = "200", description = "검색 성공")
    @GetMapping("/search")
    ResponseEntity<List<SearchResponse> > search(@RequestParam String prompt ) throws JsonProcessingException;

    @Operation(summary = "키워드추천", description = "키워드추천")
    @ApiResponse(responseCode = "200", description = "키워드추천")
    @GetMapping("/search/recommendKeyword")
    ResponseEntity<RecommendKeywordResponse> recommendKeyword(@RequestParam String prompt ) throws JsonProcessingException;

}
