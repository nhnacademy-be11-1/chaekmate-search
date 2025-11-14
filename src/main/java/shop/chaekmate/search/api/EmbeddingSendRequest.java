package shop.chaekmate.search.api;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import shop.chaekmate.search.dto.EmbeddingRequest;
import shop.chaekmate.search.dto.EmbeddingResponse;
@FeignClient(name = "embeddingClient", url = "${ollama.api.url}")
public interface EmbeddingSendRequest {
    @PostMapping("/api/embeddings")
    EmbeddingResponse createEmbedding(@RequestBody EmbeddingRequest request);
}
