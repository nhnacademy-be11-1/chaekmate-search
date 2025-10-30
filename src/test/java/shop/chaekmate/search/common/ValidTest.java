package shop.chaekmate.search.common;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Optional;
import org.junit.jupiter.api.Test;
import shop.chaekmate.search.document.Book;

class ValidTest {

    @Test
    void isBook() {
        assertThrows(IllegalArgumentException.class, () -> Valid.isBook(Optional.empty()));
    }

    @Test
    void existBook() {
        assertThrows(IllegalArgumentException.class, () -> Valid.existBook(Optional.of(Book.builder().build())));

    }
}