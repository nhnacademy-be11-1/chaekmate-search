package shop.chaekmate.search.common.exeception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import shop.chaekmate.search.common.ErrorCode;
import shop.chaekmate.search.common.ErrorResponse;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleUnexpected(Exception e) {
        ErrorCode errorCode = ErrorCode.INTERNAL_SERVER_ERROR;
        return ResponseEntity.status(errorCode.getStatus()).body(ErrorResponse.from(errorCode));
    }
}
