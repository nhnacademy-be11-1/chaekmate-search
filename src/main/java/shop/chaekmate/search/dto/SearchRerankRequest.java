package shop.chaekmate.search.dto;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class SearchRerankRequest {
    String query;
    List<String> texts;
}
