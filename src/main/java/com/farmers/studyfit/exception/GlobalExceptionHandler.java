package com.farmers.studyfit.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {

    //DuplicateLoginIdException → 409 CONFLICT
    @ExceptionHandler(DuplicateLoginIdException.class)
    public ResponseEntity<ErrorResponse> handleDuplicateEmail(DuplicateLoginIdException ex) {
        ErrorResponse body = new ErrorResponse("DUPLICATE_EMAIL", ex.getMessage());
        return ResponseEntity.status(HttpStatus.CONFLICT).body(body);
    }

    // 인증 에러 401
    @ExceptionHandler(UnAuthorizedException.class)
    public ResponseEntity<ErrorResponse> handleUnAuthorized(UnAuthorizedException ex){
        ErrorResponse body = new ErrorResponse("UNAUTHORIZED", ex.getMessage());
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(body);
    }

    // 유저 찾을 수 없음 404
    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleUserNotFound(UserNotFoundException ex){
        ErrorResponse body = new ErrorResponse("USER_NOT_FOUND", ex.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(body);
    }

    // 리소스를 찾지 못했을 때: ResourceNotFoundException → 404 NOT_FOUND
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleNotFound(ResourceNotFoundException ex) {
        ErrorResponse body = new ErrorResponse("RESOURCE_NOT_FOUND", ex.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(body);
    }

    // @Valid 검증 실패 처리: MethodArgumentNotValidException → 400 BAD_REQUEST
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidation(MethodArgumentNotValidException ex) {
        // 필드 오류 메시지를 “field: message” 형태로 합친다
        String detail = ex.getFieldErrors().stream()
                .map(fe -> fe.getField() + ": " + fe.getDefaultMessage())
                .collect(Collectors.joining(", "));
        ErrorResponse body = new ErrorResponse("VALIDATION_FAILED", detail);
        return ResponseEntity.badRequest().body(body);
    }
    // 연결(Connection)을 찾지 못했을 때: ConnectionNotFoundException → 404 NOT_FOUND
    @ExceptionHandler(ConnectionNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleConnectionNotFound(ConnectionNotFoundException ex) {
        ErrorResponse body = new ErrorResponse("CONNECTION_NOT_FOUND", ex.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(body);
    }
    // 그 외 모든 예외 처리: Exception → 500 INTERNAL_SERVER_ERROR
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGeneric(Exception ex) {
        // 로그로 남기고, 클라이언트에는 일반 메시지를 노출
        ex.printStackTrace();
        ErrorResponse body = new ErrorResponse("INTERNAL_ERROR", "서버 오류가 발생했습니다.");
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(body);
    }

    // 공통 에러 응답 DTO
    public static class ErrorResponse {
        private final String code;
        private final String message;
        public ErrorResponse(String code, String message) {
            this.code = code;
            this.message = message;
        }
        public String getCode() { return code; }
        public String getMessage() { return message; }
    }
}
