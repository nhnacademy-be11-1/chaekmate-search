package shop.chaekmate.search.document;

import java.util.List;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor

public class KeywordGroupMapping {
    private UUID id;
    private List<Long> ids;
    private String groupName;
    private long hitCnt;
}
