package shop.chaekmate.search.common;

import shop.chaekmate.search.document.Book;

import java.util.Optional;

public class Valid {

    public static Book isBook(Optional<Book> byId) {
        return byId.orElseThrow(IllegalArgumentException::new);
    }

    public static void existBook(Optional<Book> byId) {
        if(byId.isPresent()){
            throw new IllegalArgumentException();
        }
    }
}
