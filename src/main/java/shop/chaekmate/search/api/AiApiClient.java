package shop.chaekmate.search.api;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import shop.chaekmate.search.dto.EmbeddingRequest;
import shop.chaekmate.search.dto.EmbeddingResponse;
@Component
@RequiredArgsConstructor
public class AiApiClient {
    static String DEFAULT_MODEL  = "bge-m3";
    private final EmbeddingSendRequest embeddingSendRequest;
    public EmbeddingResponse createEmbedding(String prompt){
        return embeddingSendRequest.createEmbedding(new EmbeddingRequest(DEFAULT_MODEL,prompt));
    }

}
