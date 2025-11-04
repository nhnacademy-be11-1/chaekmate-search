package shop.chaekmate.search.document;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.WriteTypeHint;

@Document(indexName = "keywordgroups",writeTypeHint = WriteTypeHint.FALSE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class KeywordGroup {
}
