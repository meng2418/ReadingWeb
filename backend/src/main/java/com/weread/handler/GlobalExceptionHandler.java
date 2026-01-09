package com.weread.handler;

import com.weread.dto.Result;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ResponseStatusException;

/**
 * Global Exception Handler (Centralized exception handling using @ControllerAdvice).
 * Unifies the response format and handles exceptions thrown by Controllers.
 */
@RestControllerAdvice // Applies to all @RestController annotated classes
public class GlobalExceptionHandler {

    /**
     * Handles ResponseStatusException (e.g., thrown by services).
     * When status code is 400, returns empty body as per API design.
     * This handler must be before RuntimeException handler since ResponseStatusException extends RuntimeException.
     */
    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<Void> handleResponseStatusException(ResponseStatusException e) {
        HttpStatus status = HttpStatus.resolve(e.getStatusCode().value());
        if (status == null) {
            status = HttpStatus.INTERNAL_SERVER_ERROR;
        }
        // 根据接口设计，400 状态码时响应不应该有 Body
        if (status == HttpStatus.BAD_REQUEST) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
        // 其他状态码也返回空 body（如 401, 404 等）
        return ResponseEntity.status(status).build();
    }

    /**
     * Handles validation exceptions (e.g., failed @NotNull, @NotEmpty checks).
     * Returns 400 with empty body as per API design.
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Void> handleValidationException(MethodArgumentNotValidException e) {
        // 根据接口设计，400 状态码时响应不应该有 Body
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
    }

    /**
     * Handles business exceptions (e.g., "Book already exists", "Book not found").
     * Returns 400 with empty body as per API design.
     * Note: This handler should not catch ResponseStatusException as it's handled above.
     */
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<Void> handleBusinessException(RuntimeException e) {
        // 根据接口设计，400 状态码时响应不应该有 Body
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
    }

    /**
     * Handles system exceptions (e.g., database connection failure, unhandled exceptions).
     */
    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR) // Returns 500 Internal Server Error
    public Result<Void> handleSystemException(Exception e) {
        // Log the detailed stack trace for debugging
        e.printStackTrace(); 
        return Result.fail("系统繁忙，请稍后重试");
    }
}