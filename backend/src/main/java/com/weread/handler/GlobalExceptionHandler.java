package com.weread.handler;

import com.weread.dto.Result;
import org.springframework.http.HttpStatus;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * Global Exception Handler (Centralized exception handling using @ControllerAdvice).
 * Unifies the response format and handles exceptions thrown by Controllers.
 */
@RestControllerAdvice // Applies to all @RestController annotated classes
public class GlobalExceptionHandler {

    /**
     * Handles business exceptions (e.g., "Book already exists", "Book not found").
     */
    @ExceptionHandler(RuntimeException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST) // Returns 400 Bad Request
    public Result<Void> handleBusinessException(RuntimeException e) {
        return Result.fail(e.getMessage());
    }

    /**
     * Handles validation exceptions (e.g., failed @NotNull, @NotEmpty checks).
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST) // Returns 400 Bad Request
    public Result<Void> handleValidationException(MethodArgumentNotValidException e) {
        // Retrieve the first validation failure field and message
        FieldError fieldError = e.getBindingResult().getFieldError();
        String errorMessage = fieldError != null ? fieldError.getDefaultMessage() : "参数校验失败";
        return Result.fail(errorMessage);
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