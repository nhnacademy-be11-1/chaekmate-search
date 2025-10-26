package shop.chaekmate.search.document;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.*;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;

@Document(indexName = "books")
@NoArgsConstructor
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
    private int price;

    @Field(type = FieldType.Text, analyzer = "korean_english_analyzer")
    private String description;

    @MultiField(
            mainField = @Field(type = FieldType.Text, analyzer = "korean_english_analyzer"),
            otherFields = {
                    @InnerField(suffix = "keyword", type = FieldType.Keyword)
            }
    )
    private List<String> categories;

    @Field(type = FieldType.Date, format = DateFormat.date_optional_time)
    private Instant publicationDatetime;

    @MultiField(
            mainField = @Field(type = FieldType.Text, analyzer = "korean_english_analyzer"),
            otherFields = {
                    @InnerField(suffix = "keyword", type = FieldType.Keyword)
            }
    )
    private List<String> tags;

    @Field(type = FieldType.Dense_Vector, dims = 1024)
    private float[] embedding;

    @Field(type = FieldType.Date, format = DateFormat.date_optional_time)
    private Instant createdAt;

    Book(long id, String title, String author, int price, String description, List<String> categories, LocalDateTime publicationDatetime, List<String> tags, float[] embedding) {
        this.id = id;
        this.title = title;
        this.author = author;
        this.price = price;
        this.description = description;
        this.categories = categories == null ? new ArrayList<>() : categories;
        this.publicationDatetime = publicationDatetime.toInstant(ZoneOffset.UTC);
        this.tags = tags == null ? new ArrayList<>() : tags;
        this.embedding = embedding;
        this.createdAt = LocalDateTime.now().toInstant(ZoneOffset.UTC);
    }

    static Book create(long id, String title, String author, int price, String description, List<String> categories, LocalDateTime publicationDatetime, List<String> tags, float[] embedding) {
        return new Book(id, title, author, price, description, categories, publicationDatetime, tags, embedding);
    }
}

