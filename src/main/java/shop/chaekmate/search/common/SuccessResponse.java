package shop.chaekmate.search.common;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.time.LocalDateTime;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record SuccessResponse<T>(
        LocalDateTime timestamp,
        String code,
        T data
) {
    public SuccessResponse(T data) {
        this(LocalDateTime.now(), "SUCCESS-200", data);
    }

    public static <T> SuccessResponse<T> of(T data) {
        return new SuccessResponse<>(data);
    }
}