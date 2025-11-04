package shop.chaekmate.search.config;

import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.vertexai.gemini.VertexAiGeminiChatModel;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AiConfig {

    @Bean
    public ChatModel geminiChatModel(VertexAiGeminiChatModel vertexAiGeminiChatModel) {
        return vertexAiGeminiChatModel;
    }
}
