package shop.chaekmate.search.dto;


import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
public class BookInfoRequest implements BaseBookTaskDto{
    long id;
    String title;
    String author;
    Integer price;
    String description;
    String isbn;
    String publisher;
    List<String> bookImages;
    List<String> categories;
    LocalDate publicationDatetime;
    List<String> tags;
    String reviewSummary;
    Double rating;
    Integer reviewCnt;
    @Override
    public Long getId() {
        return id;
    }

}
