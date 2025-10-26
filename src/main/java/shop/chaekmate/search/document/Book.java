package shop.chaekmate.search.document;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.*;
import shop.chaekmate.search.dto.BookInfoRequest;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;

@Document(indexName = "books")
@Getter
@Setter
@Setting(settingPath = "elasticsearch/settings/korean-analyzer.json")
@Mapping(mappingPath = "elasticsearch/mappings/books-mapping.json")
public class Book {
    @Id
    private Long id;

    @Field(type = FieldType.Text, analyzer = "korean_english_analyzer", searchAnalyzer = "korean_english_analyzer")
    private String title;

    @Field(type = FieldType.Text, analyzer = "korean_english_analyzer")
    private String author;

    @Field(type = FieldType.Integer)
    private Integer price;

    @Field(type = FieldType.Text, analyzer = "korean_english_analyzer")
    private String description;

    @MultiField(mainField = @Field(type = FieldType.Text, analyzer = "korean_english_analyzer"), otherFields = {@InnerField(suffix = "keyword", type = FieldType.Keyword)})
    private List<String> categories;

    @Field(type = FieldType.Date, format = DateFormat.date_optional_time)
    private Instant publicationDatetime;

    @MultiField(mainField = @Field(type = FieldType.Text, analyzer = "korean_english_analyzer"), otherFields = {@InnerField(suffix = "keyword", type = FieldType.Keyword)})
    private List<String> tags;

    @Field(type = FieldType.Dense_Vector, dims = 1024)
    private float[] embedding;

    @Field(type = FieldType.Date, format = DateFormat.date_optional_time)
    private Instant createdAt;
    @Field(type = FieldType.Date, format = DateFormat.date_optional_time)
    private Instant updateAt;

    @Builder
    Book(long id, String title, String author, int price, String description, List<String> categories, LocalDateTime publicationDatetime, List<String> tags, float[] embedding) {
        this.id = id;
        this.title = title;
        this.author = author;
        this.price = price;
        this.description = description;
        this.categories = categories == null ? new ArrayList<>() : categories;
        this.publicationDatetime = publicationDatetime != null ? publicationDatetime.toInstant(ZoneOffset.UTC) : Instant.now();
        this.tags = tags == null ? new ArrayList<>() : tags;
        this.embedding = embedding;
        this.createdAt = LocalDateTime.now().toInstant(ZoneOffset.UTC);
    }

    public void update(BookInfoRequest bookInfoRequest, float[] embedding) {
        this.title = bookInfoRequest.getTitle() == null ? title : bookInfoRequest.getTitle();
        this.author = bookInfoRequest.getAuthor() == null ? author : bookInfoRequest.getAuthor();
        this.price = bookInfoRequest.getPrice() == null ? price : bookInfoRequest.getPrice();
        this.description = bookInfoRequest.getDescription() == null ? description : bookInfoRequest.getDescription();
        this.categories = bookInfoRequest.getCategories() == null ? categories : bookInfoRequest.getCategories();
        this.tags = bookInfoRequest.getTags() == null ? tags : bookInfoRequest.getTags();
        this.embedding = embedding != null && embedding.length > 0 ? embedding : this.embedding;
        this.updateAt = LocalDateTime.now().toInstant(ZoneOffset.UTC);

    }
}

