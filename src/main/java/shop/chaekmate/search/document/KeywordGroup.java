package shop.chaekmate.search.document;

import java.util.UUID;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;
import org.springframework.data.elasticsearch.annotations.WriteTypeHint;

@Document(indexName = "keywordgroups",writeTypeHint = WriteTypeHint.FALSE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class KeywordGroup {
    @Id
    public UUID id;
    @Field(type = FieldType.Dense_Vector, dims = 1024)
    private Float[] embedding;

    @Builder
    KeywordGroup(UUID id , Float[] embedding){
        this.id = id;
        this.embedding = embedding;
    }
}
