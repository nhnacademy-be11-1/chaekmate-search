//package shop.chaekmate.search.controller;
//
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.data.domain.Page;
//import org.springframework.data.domain.PageImpl;
//import org.springframework.data.domain.PageRequest;
//import org.springframework.data.domain.Pageable;
//import org.springframework.http.MediaType;
//import org.springframework.test.context.ActiveProfiles;
//import org.springframework.test.context.bean.override.mockito.MockitoBean;
//import org.springframework.test.web.servlet.MockMvc;
//import shop.chaekmate.search.document.Book;
//import shop.chaekmate.search.dto.SearchResponse;
//import shop.chaekmate.search.service.SearchService;
//
//import java.time.LocalDate;
//import java.util.List;
//
//import static org.mockito.ArgumentMatchers.any;
//import static org.mockito.ArgumentMatchers.anyString;
//import static org.mockito.Mockito.when;
//import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
//
//@SpringBootTest
//@AutoConfigureMockMvc
//@ActiveProfiles("test")
//class SearchControllerTest {
//    @Autowired
//    private MockMvc mockMvc;
//    @MockitoBean
//    private SearchService searchService;
//
//    @Test
//    void 검색() throws Exception {
//        Pageable pageable = PageRequest.of(0, 10);
//
//        Page<SearchResponse> mockResponses = new PageImpl<>(List.of(
//                new SearchResponse(Book.builder()
//                        .id(1L)
//                        .title("zzz")
//                        .author("zzz")
//                        .price(18000)
//                        .description("zzzzz")
//                        .reviewCnt(0)
//                        .categories(List.of("zz", "zzz"))
//                        .tags(List.of("zzz", "zzz"))
//                        .publicationDatetime(LocalDate.now())
//                        .build()),
//                new SearchResponse(Book.builder()
//                        .id(2L)
//                        .title("zzzzz")
//                        .author("zzz")
//                        .price(20000)
//                        .reviewCnt(0)
//                        .description("zzzzz")
//                        .categories(List.of("zz", "zzz"))
//                        .tags(List.of("zzz", "zzz"))
//                        .publicationDatetime(LocalDate.now())
//                        .build())
//        ),pageable,2);
//
//        when(searchService.search(anyString(), any(Pageable.class))).thenReturn(mockResponses);
//
//        mockMvc.perform(get("/search")
//                        .param("prompt", "zzz")
//                        .accept(MediaType.APPLICATION_JSON))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.code").value("SUCCESS-200"))
//                .andExpect(jsonPath("$.data.content[0].title").value("zzz"))
//                .andExpect(jsonPath("$.data.content[1].title").value("zzzzz"))
//                .andExpect(jsonPath("$.data.totalElements").value(2));
//    }
//}
