package shop.chaekmate.search.dto;


import java.time.LocalDateTime;
import java.util.List;
import lombok.Getter;
import lombok.Setter;
import shop.chaekmate.search.common.EventType;

@Getter
@Setter
public class BookInfoRequest implements BaseBookTaskDto{
    long id;
    String title;
    String author;
    int price;
    String description;
    List<String> categories;
    LocalDateTime publicationDatetime;
    List<String> tags;
}
