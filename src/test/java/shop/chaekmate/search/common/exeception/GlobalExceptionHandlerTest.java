package shop.chaekmate.search.common.exeception;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;
import shop.chaekmate.search.common.ErrorCode;
import shop.chaekmate.search.common.ErrorResponse;
class GlobalExceptionHandlerTest {
    GlobalExceptionHandler handler = new GlobalExceptionHandler();

    @Test
    @DisplayName("예상치 못한 Exception 발생 시 500 + ErrorResponse 반환")
    void handleUnexpected() {

        Exception e = new Exception("test");

        ResponseEntity<ErrorResponse> response = handler.handleUnexpected(e);
        assertThat(response.getStatusCode().value())
                .isEqualTo(ErrorCode.INTERNAL_SERVER_ERROR.getStatus().value());

        assertThat(response.getBody().code())
                .isEqualTo(ErrorCode.INTERNAL_SERVER_ERROR.getCode());

        assertThat(response.getBody().message())
                .isEqualTo(ErrorCode.INTERNAL_SERVER_ERROR.getMessage());
    }
}