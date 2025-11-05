package shop.chaekmate.search.dto;


import java.time.LocalDateTime;
import java.util.List;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BookInfoRequest implements BaseBookTaskDto{
    long id;
    String title;
    String author;
    Integer price;
    String description;
    List<String> bookImages;
    List<String> categories;
    LocalDateTime publicationDatetime;
    List<String> tags;
}
