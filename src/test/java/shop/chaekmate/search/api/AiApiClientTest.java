package shop.chaekmate.search.api;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.model.Generation;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.ai.vertexai.gemini.VertexAiGeminiChatModel;
import shop.chaekmate.search.document.Book;
import shop.chaekmate.search.dto.EmbeddingResponse;
import shop.chaekmate.search.dto.SearchRerankItem;
import shop.chaekmate.search.dto.SearchRerankRequest;

@ExtendWith(MockitoExtension.class)
class AiApiClientTest {
    @Mock
    EmbeddingSendRequest embeddingSendRequest;
    @Mock
    VertexAiGeminiChatModel vertexAiGeminiChatModel;
    @Mock
    RerankSendRequest rerankSendRequest;
    @Mock
    PromptTemplate promptTemplate;

    @InjectMocks
    AiApiClient aiApiClient;

    @Test
    void 임베딩요청성공() {
        EmbeddingResponse embeddingResponse = new EmbeddingResponse();
        embeddingResponse.setEmbedding(new Float[]{0.1F, 0.2F});
        when(embeddingSendRequest.createEmbedding(any())).thenReturn(embeddingResponse);
        EmbeddingResponse result = embeddingSendRequest.createEmbedding(any());
        Assertions.assertEquals(2, result.getEmbedding().length);
    }

    @Test
    void 검색llm요청성공() {
        Prompt mockPrompt = new Prompt("검색어:test,keywordSearch: test1, vectorSearch:test2", null);
        when(promptTemplate.create(anyMap())).thenReturn(mockPrompt);

        AssistantMessage realMessage = new AssistantMessage("[1,2,3]");
        Generation generation = new Generation(realMessage);
        ChatResponse fakeResponse = new ChatResponse(List.of(generation), null);

        when(vertexAiGeminiChatModel.call(any(Prompt.class))).thenReturn(fakeResponse);
        String result = aiApiClient.createSearch("test", "test1", "test2");
        verify(vertexAiGeminiChatModel, times(1)).call(any(Prompt.class));
        Assertions.assertEquals("[1,2,3]", result);
    }

    @Test
    void 리랭크요청성공() {
        List<Book> books = List.of(
                Book.builder().id(0L).description("TEST0").build(),
                Book.builder().id(1L).description("TEST1").build(),
                Book.builder().id(2L).description("TEST2").build(),
                Book.builder().id(3L).description("TEST3").build(),
                Book.builder().id(4L).description("TEST4").build()
        );
        List<SearchRerankItem> mockResponse = List.of(
                new SearchRerankItem(3, 0.95),
                new SearchRerankItem(1, 0.90),
                new SearchRerankItem(4, 0.85),
                new SearchRerankItem(0, 0.70),
                new SearchRerankItem(2, 0.60)
        );

        when(rerankSendRequest.rerank(any(SearchRerankRequest.class))).thenReturn(mockResponse);

        List<Book> result = aiApiClient.rerank("test",books);

        assertThat(result).hasSize(4);

        assertThat(result.get(0).getDescription()).isEqualTo("TEST3");
        assertThat(result.get(1).getDescription()).isEqualTo("TEST1");
        assertThat(result.get(2).getDescription()).isEqualTo("TEST4");
        assertThat(result.get(3).getDescription()).isEqualTo("TEST0");
    }
}
