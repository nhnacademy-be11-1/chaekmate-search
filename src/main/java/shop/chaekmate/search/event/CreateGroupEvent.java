package shop.chaekmate.search.event;

import java.util.List;
import shop.chaekmate.search.document.Book;

public record CreateGroupEvent(
        List<Book> books, List<Long> ids, Float[] vector) {
}
