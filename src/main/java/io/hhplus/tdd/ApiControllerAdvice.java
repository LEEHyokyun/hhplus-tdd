package io.hhplus.tdd;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@RestControllerAdvice
class ApiControllerAdvice extends ResponseEntityExceptionHandler {
    @ExceptionHandler(value = InterruptedException.class)
    public ResponseEntity<ErrorResponse> InterruptedException(Exception e) {
        return ResponseEntity.status(500).body(new ErrorResponse("500", "InterruptedException 에러가 발생했습니다."));
    }
    
    @ExceptionHandler(value = java.util.concurrent.ExecutionException.class)
    public ResponseEntity<ErrorResponse> ExecutionException(Exception e) {
        return ResponseEntity.status(500).body(new ErrorResponse("500", "ExecutionException 에러가 발생했습니다."));
    }
    
    @ExceptionHandler(value = Exception.class)
    public ResponseEntity<ErrorResponse> handleException(Exception e) {
        return ResponseEntity.status(500).body(new ErrorResponse("500", "에러가 발생했습니다."));
    }
}
