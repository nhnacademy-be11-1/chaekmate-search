package shop.chaekmate.search.dto;


import java.time.LocalDateTime;
import java.util.List;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BookInfoRequest {
    long id;
    String title;
    String author;
    int price;
    String description;
    List<String> categories;
    LocalDateTime publicationDatetime;
    List<String> tags;
}
