package shop.chaekmate.search.common;

import java.time.LocalDateTime;

public record ErrorResponse(
        LocalDateTime currentTime,
        int status,
        String code,
        String message

) {
    public static ErrorResponse from(ErrorCode errorCode) {
        return new ErrorResponse(LocalDateTime.now(),
                errorCode.getStatus().value(),
                errorCode.getCode(),
                errorCode.getMessage()
        );
    }
}
