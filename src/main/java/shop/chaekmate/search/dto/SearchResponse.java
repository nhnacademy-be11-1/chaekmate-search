package shop.chaekmate.search.dto;

import lombok.Getter;
import lombok.Setter;
import shop.chaekmate.search.document.Book;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;

@Getter
@Setter
public class SearchResponse {
    long id;
    String title;
    String author;
    Integer price;
    String description;
    List<String> bookImages;
    List<String> categories;
    LocalDateTime publicationDatetime;
    List<String> tags;

    public SearchResponse(Book book){
        this.id= book.getId();
        this.title = book.getTitle();
        this.author = book.getAuthor();
        this.price = book.getPrice();
        this.description = book.getDescription();
        this.bookImages = book.getBookImages();
        this.categories = book.getCategories();
        this.publicationDatetime =  LocalDateTime.ofInstant(book.getPublicationDatetime(), ZoneId.of("Asia/Seoul"));
        this.tags = book.getTags();
    }

}
