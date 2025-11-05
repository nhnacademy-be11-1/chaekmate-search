package shop.chaekmate.search.event;

import java.util.List;

public record CreateGroupEvent(String keyword,
                               List<shop.chaekmate.search.document.Book> books, List<Long> ids,Float[] vector) {
}
