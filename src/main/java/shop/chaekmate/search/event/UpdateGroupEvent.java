package shop.chaekmate.search.event;

import shop.chaekmate.search.document.Book;
import shop.chaekmate.search.dto.EmbeddingResponse;

public record UpdateGroupEvent(EmbeddingResponse embeddingResponse, Book book ) {
}
