package shop.chaekmate.search.dto;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.PROPERTY,
        property = "dtoType"
)
@JsonSubTypes({
        @JsonSubTypes.Type(value = BookInfoRequest.class, name = "BOOK_INFO"),
        @JsonSubTypes.Type(value = BookDeleteRequest.class, name = "BOOK_DELETE")
})
public interface BaseBookTaskDto {}