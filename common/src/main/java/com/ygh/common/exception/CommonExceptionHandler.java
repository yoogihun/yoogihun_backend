package com.ygh.common.exception;


import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class CommonExceptionHandler {

    @ExceptionHandler(BaseException.class)
    public ResponseEntity<BaseErrorResponse> handleBaseException(BaseException e) {
        return ResponseEntity
                .status(e.getStatus())
                .body(new BaseErrorResponse(e.getStatus().value(), e.getCode(), e.getMessage()));
    }

    /**
     * validation 관련 예외 처리
     * @param e
     * @return
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<BaseErrorResponse> handleValidationException(MethodArgumentNotValidException e) {
        StringBuilder errorMessage = new StringBuilder();
        e.getBindingResult().getFieldErrors().forEach(fieldError -> {
            errorMessage.append(fieldError.getField())
                    .append(": ")
                    .append(fieldError.getDefaultMessage())
                    .append("; ");
        });
        BaseErrorResponse response = new BaseErrorResponse(HttpStatus.BAD_REQUEST.value(), "VALIDATION_ERROR", errorMessage.toString());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }
}
