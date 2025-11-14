package shop.chaekmate.search.api;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.stereotype.Component;
import shop.chaekmate.search.document.Book;
import shop.chaekmate.search.dto.EmbeddingRequest;
import shop.chaekmate.search.dto.EmbeddingResponse;
import shop.chaekmate.search.dto.SearchRerankItem;
import shop.chaekmate.search.dto.SearchRerankRequest;

@Component
@RequiredArgsConstructor
public class AiApiClient {

    static final String DEFAULT_MODEL = "bge-m3";
    private final EmbeddingSendRequest embeddingSendRequest;
    private final RerankSendRequest rerankSendRequest;
    private final PromptTemplate searchPromptTemplate;
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

    public List<Book> rerank(String keyword, List<Book> vectorBooks) {

        if (vectorBooks == null || vectorBooks.isEmpty()) {
            return Collections.emptyList();
        }

        List<String> texts = vectorBooks.stream()
                .map(b -> Optional.ofNullable(b.getDescription()).orElse(""))
                .toList();

        if (texts.stream().allMatch(String::isBlank)) {
            return vectorBooks;
        }
        List<SearchRerankItem> resp = rerankSendRequest.rerank(
                new SearchRerankRequest(keyword, texts)
        );

        if (resp == null || resp.isEmpty()) {
            return vectorBooks;
        }

        List<SearchRerankItem> sorted = resp.stream()
                .sorted(Comparator.comparingDouble(SearchRerankItem::score).reversed())
                .toList();

        int cutoff = Math.max(1, (int) Math.ceil(sorted.size() * 0.8));
        List<SearchRerankItem> topItems = sorted.subList(0, cutoff);

        List<Book> result = new ArrayList<>();
        for (SearchRerankItem item : topItems) {
            int idx = item.index();
            if (idx >= 0 && idx < vectorBooks.size()) {
                result.add(vectorBooks.get(idx));
            }
        }

        return result;
    }
}
