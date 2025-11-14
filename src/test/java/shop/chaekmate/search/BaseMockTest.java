package shop.chaekmate.search;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import shop.chaekmate.search.repository.BookRepository;
import shop.chaekmate.search.repository.KeywordGroupRepository;

@SpringBootTest
@ActiveProfiles("test")
public abstract class BaseMockTest {

    @TestConfiguration
    static class Mocks {
        @MockitoBean
        BookRepository bookRepository;
        @MockitoBean KeywordGroupRepository keywordGroupRepository;
    }
}