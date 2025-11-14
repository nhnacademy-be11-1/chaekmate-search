package shop.chaekmate.search.document;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.*;
import shop.chaekmate.search.dto.BookInfoRequest;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Document(indexName = "books", writeTypeHint = WriteTypeHint.FALSE)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
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

    @Field(type = FieldType.Text)
    private String isbn;

    @Field(type = FieldType.Text, analyzer = "korean_english_analyzer")
    private String publisher;

    @Field(type = FieldType.Text)
    private String bookImages;

    @MultiField(mainField = @Field(type = FieldType.Text, analyzer = "korean_english_analyzer"),
            otherFields = @InnerField(suffix = "keyword", type = FieldType.Keyword))
    private List<String> categories;

    @Field(type = FieldType.Date, format = DateFormat.date)
    private LocalDate publicationDatetime;

    @MultiField(mainField = @Field(type = FieldType.Text, analyzer = "korean_english_analyzer"),
            otherFields = @InnerField(suffix = "keyword", type = FieldType.Keyword))
    private List<String> tags;

    @Field(type = FieldType.Text, analyzer = "korean_english_analyzer")
    private String reviewSummary;

    @Field(type = FieldType.Integer)
    private Integer reviewCnt;

    @Field(type = FieldType.Double)
    private Double rating;

    @Field(type = FieldType.Dense_Vector, dims = 1024)
    private Float[] embedding;

    @Field(type = FieldType.Date, format = DateFormat.date_hour_minute_second)
    private LocalDateTime createdAt;

    @Field(type = FieldType.Date, format = DateFormat.date_hour_minute_second)
    private LocalDateTime updatedAt;

    @Builder
    public Book(long id, String title, String author, Integer price, String description,
                String isbn, String publisher, String bookImages,
                List<String> categories, LocalDate publicationDatetime,
                List<String> tags, String reviewSummary, Double rating, Float[] embedding, Integer reviewCnt) {
        this.id = id;
        this.title = title;
        this.author = author;
        this.price = price;
        this.description = description;
        this.isbn = isbn;
        this.publisher = publisher;
        this.bookImages = bookImages;
        this.categories = categories != null ? categories : new ArrayList<>();
        this.publicationDatetime = publicationDatetime;
        this.tags = tags != null ? tags : new ArrayList<>();
        this.reviewSummary = reviewSummary == null || reviewSummary.isEmpty() ? "" : reviewSummary;
        this.rating = rating != null ? rating : 0.0;
        this.embedding = embedding;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        this.reviewCnt = reviewCnt == null ? 0 : reviewCnt;
    }

    public void update(BookInfoRequest bookInfoRequest, Float[] embedding) {
        this.title = bookInfoRequest.getTitle() != null ? bookInfoRequest.getTitle() : this.title;
        this.author = bookInfoRequest.getAuthor() != null ? bookInfoRequest.getAuthor() : this.author;
        this.price = bookInfoRequest.getPrice() != null ? bookInfoRequest.getPrice() : this.price;
        this.description =
                bookInfoRequest.getDescription() != null ? bookInfoRequest.getDescription() : this.description;
        this.isbn = bookInfoRequest.getIsbn() != null ? bookInfoRequest.getIsbn() : this.isbn;
        this.publisher = bookInfoRequest.getPublisher() != null ? bookInfoRequest.getPublisher() : this.publisher;
        this.bookImages = bookInfoRequest.getBookImages() != null ? bookInfoRequest.getBookImages() : this.bookImages;
        this.categories = bookInfoRequest.getCategories() != null ? bookInfoRequest.getCategories() : this.categories;
        this.publicationDatetime =
                bookInfoRequest.getPublicationDatetime() != null ? bookInfoRequest.getPublicationDatetime()
                        : this.publicationDatetime;
        this.tags = bookInfoRequest.getTags() != null ? bookInfoRequest.getTags() : this.tags;
        this.reviewSummary =
                bookInfoRequest.getReviewSummary() != null ? bookInfoRequest.getReviewSummary() : this.reviewSummary;
        this.reviewCnt = bookInfoRequest.getReviewCnt() != null ? bookInfoRequest.getReviewCnt() : this.reviewCnt;
        this.rating = (bookInfoRequest.getRating() != null && bookInfoRequest.getRating() > 0.0)
                ? bookInfoRequest.getRating()
                : this.rating;
        this.embedding = (embedding != null && embedding.length > 0) ? embedding : this.embedding;
        this.updatedAt = LocalDateTime.now();
    }

    public Map<String, Object> toJson() {
        return Map.of(
                "id", id,
                "title", Optional.ofNullable(title).orElse(""),
                "author", Optional.ofNullable(author).orElse(""),
                "price", Optional.ofNullable(price).orElse(0),
                "categories", Optional.ofNullable(categories).orElse(List.of()),
                "tags", Optional.ofNullable(tags).orElse(List.of()),
                "rating", Optional.ofNullable(rating).orElse(0.0),
                "reviewSummary", Optional.ofNullable(reviewSummary).orElse(""),
                "reviewCnt", Optional.ofNullable(reviewCnt).orElse(0)
        );
    }
}

