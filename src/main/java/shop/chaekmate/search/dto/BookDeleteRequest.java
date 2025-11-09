package shop.chaekmate.search.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class BookDeleteRequest implements BaseBookTaskDto {
    long id;
    @Override
    public Long getId() {
        return id;
    }
}
