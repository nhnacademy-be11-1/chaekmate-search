package shop.chaekmate.search.api;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.Mockito.*;

import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.model.Generation;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.ai.vertexai.gemini.VertexAiGeminiChatModel;
import shop.chaekmate.search.dto.EmbeddingResponse;

@ExtendWith(MockitoExtension.class)
class AiApiClientTest {
    @Mock
    EmbeddingSendRequest embeddingSendRequest;
    @Mock
    VertexAiGeminiChatModel vertexAiGeminiChatModel;

    @Mock
    PromptTemplate promptTemplate;

    @InjectMocks
    AiApiClient aiApiClient;
    @Test
    void 임베딩요청성공(){
        EmbeddingResponse embeddingResponse = new EmbeddingResponse();
        embeddingResponse.setEmbedding(new Float[]{0.1F,0.2F});
        when(embeddingSendRequest.createEmbedding(any())).thenReturn(embeddingResponse);
        EmbeddingResponse result = embeddingSendRequest.createEmbedding(any());
        Assertions.assertEquals(2, result.getEmbedding().length);
    }
    @Test
    void llm요청성공() {
        Prompt mockPrompt = new Prompt("검색어:test,keywordSearch: test1, vectorSearch:test2", null);
        when(promptTemplate.create(anyMap())).thenReturn(mockPrompt);

        AssistantMessage realMessage = new AssistantMessage("[1,2,3]");
        Generation generation = new Generation(realMessage);
        ChatResponse fakeResponse = new ChatResponse(List.of(generation), null);

        when(vertexAiGeminiChatModel.call(any(Prompt.class))).thenReturn(fakeResponse);
        String result = aiApiClient.createSearch("test","test1","test2");
        verify(vertexAiGeminiChatModel, times(1)).call(any(Prompt.class));
        Assertions.assertEquals("[1,2,3]",result);
    }
}
