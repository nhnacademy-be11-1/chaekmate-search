package shop.chaekmate.search.api;

import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.stereotype.Component;
import shop.chaekmate.search.dto.EmbeddingRequest;
import shop.chaekmate.search.dto.EmbeddingResponse;

@Component
@RequiredArgsConstructor
public class AiApiClient {

    static final String DEFAULT_MODEL = "bge-m3";
    private final EmbeddingSendRequest embeddingSendRequest;
    private final PromptTemplate searchPromptTemplate;
    private final PromptTemplate groupPromptTemplate;
    private final ChatModel geminiChatModel;

    public EmbeddingResponse createEmbedding(String prompt) {
        return embeddingSendRequest.createEmbedding(new EmbeddingRequest(DEFAULT_MODEL, prompt));
    }

    public String createSearch(String keyword, String keywordJson, String vectorJson) {
        Prompt prompt = searchPromptTemplate.create(Map.of(
                "input", keyword,
                "keywordSearch", keywordJson,
                "vectorSearch", vectorJson
        ));

        ChatResponse response = geminiChatModel.call(prompt);
        return response.getResult().getOutput().getText();
    }
    public String groupName(String keyword , String results ,String keywordVector){
        Prompt prompt = groupPromptTemplate.create(Map.of(
                "keyword", keyword,
                "results", results,
                "keywordVector",keywordVector
        ));
        ChatResponse response = geminiChatModel.call(prompt);
        return response.getResult().getOutput().getText();
    }
}
