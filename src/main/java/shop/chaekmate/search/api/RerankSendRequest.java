package shop.chaekmate.search.api;

import java.util.List;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import shop.chaekmate.search.dto.SearchRerankItem;
import shop.chaekmate.search.dto.SearchRerankRequest;

@FeignClient(name = "rerankClient", url = "${rerank.api.url}")

public interface RerankSendRequest {
    @PostMapping("/rerank")
    List<SearchRerankItem> rerank(@RequestBody SearchRerankRequest request);

}
