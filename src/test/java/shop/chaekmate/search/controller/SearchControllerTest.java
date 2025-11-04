package shop.chaekmate.search.controller;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import shop.chaekmate.search.document.Book;
import shop.chaekmate.search.dto.SearchResponse;
import shop.chaekmate.search.service.SearchService;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")

public class SearchControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @MockitoBean
    private SearchService searchService;

    @Test
    void 검색() throws Exception {
        List<SearchResponse> mockResponses = List.of(
                new SearchResponse(Book.builder()
                        .id(1L)
                        .title("zzz")
                        .author("zzz")
                        .price(18000)
                        .description("zzzzz")
                        .categories(List.of("zz", "zzz"))
                        .tags(List.of("zzz", "zzz"))
                        .publicationDatetime(LocalDateTime.now())
                        .embedding(new Float[]{0.1f, 0.2f, 0.3f})
                        .build()),
                new SearchResponse(Book.builder()
                        .id(2L)
                        .title("zzzzz")
                        .author("zzz")
                        .price(20000)
                        .description("zzzzz")
                        .categories(List.of("zz", "zzz"))
                        .tags(List.of("zzz", "zzz"))
                        .publicationDatetime(LocalDateTime.now())
                        .embedding(new Float[]{0.1f, 0.2f, 0.3f})
                        .build())
        );

        when(searchService.search("zzz")).thenReturn(mockResponses);

        mockMvc.perform(get("/search")
                        .param("prompt", "zzz")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].title").value("zzz"))
                .andExpect(jsonPath("$[0].author").value("zzz"))
                .andExpect(jsonPath("$[1].title").value("zzzzz"))
                .andExpect(jsonPath("$[1].price").value(20000));
    }
}