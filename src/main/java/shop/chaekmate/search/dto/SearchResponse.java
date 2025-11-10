package shop.chaekmate.search.dto;

import lombok.Getter;
import lombok.Setter;
import shop.chaekmate.search.document.Book;

import java.time.LocalDate;
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
    LocalDate publicationDatetime;
    List<String> tags;
    String reviewSummary;
    int reviewCnt;
    double rating;
    String publisher;
    public SearchResponse(Book book){
        this.id= book.getId();
        this.title = book.getTitle();
        this.author = book.getAuthor();
        this.price = book.getPrice();
        this.description = book.getDescription();
        this.bookImages = book.getBookImages();
        this.categories = book.getCategories();
        this.publicationDatetime =  book.getPublicationDatetime();
        this.reviewSummary = book.getReviewSummary();
        this.reviewCnt = book.getReviewCnt();
        this.rating = book.getRating();
        this.publisher =  book.getPublisher();
        this.tags = book.getTags();
    }

}
