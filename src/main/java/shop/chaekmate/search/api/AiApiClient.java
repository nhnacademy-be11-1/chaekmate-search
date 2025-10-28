package shop.chaekmate.search.api;

import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.ai.vertexai.gemini.VertexAiGeminiChatModel;
import org.springframework.stereotype.Component;
import shop.chaekmate.search.dto.EmbeddingRequest;
import shop.chaekmate.search.dto.EmbeddingResponse;
@Component
@RequiredArgsConstructor
public class AiApiClient {
    static String DEFAULT_MODEL  = "bge-m3";
    private final EmbeddingSendRequest embeddingSendRequest;
    private final VertexAiGeminiChatModel vertexAiGeminiChatModel;
    private final PromptTemplate promptTemplate;
    public EmbeddingResponse createEmbedding(String prompt){
        return embeddingSendRequest.createEmbedding(new EmbeddingRequest(DEFAULT_MODEL,prompt));
    }

    public String createSearch(String keyword, String keywordJson, String vectorJson) {
        Prompt prompt = promptTemplate.create(Map.of(
                "input", keyword,
                "keywordSearch", keywordJson,
                "vectorSearch", vectorJson
        ));
        ChatResponse response = vertexAiGeminiChatModel.call(prompt);
        return response.getResult().getOutput().getText();
    }
}
