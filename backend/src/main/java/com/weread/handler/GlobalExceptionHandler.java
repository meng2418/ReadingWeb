package com.weread.handler;

import com.weread.dto.Result;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.server.ResponseStatusException;

/**
 * Global Exception Handler (Centralized exception handling using @ControllerAdvice).
 * Unifies the response format and handles exceptions thrown by Controllers.
 */
@RestControllerAdvice // Applies to all @RestController annotated classes
public class GlobalExceptionHandler {

    /**
     * 根据请求路径判断是否应该返回 JSON 格式的错误响应
     * @return true 表示应该返回 JSON 格式，false 表示返回空 body
     */
    private boolean shouldReturnJsonError() {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attributes == null) {
            return false;
        }
        HttpServletRequest request = attributes.getRequest();
        String requestPath = request.getRequestURI();
        
        // /books/{bookId}/mark-finished 接口需要返回 JSON 格式
        if (requestPath != null && requestPath.matches("/books/\\d+/mark-finished")) {
            return true;
        }
        
        // /user/notes 接口需要返回 JSON 格式（401响应）
        if (requestPath != null && requestPath.equals("/user/notes")) {
            return true;
        }
        
        // /book-reviews 接口返回 JSON 格式（包含错误信息）
        if (requestPath != null && requestPath.startsWith("/book-reviews")) {
            return true;
        }
        
        // /notes 接口返回空 body（400和401响应不应该有Body）
        if (requestPath != null && requestPath.startsWith("/notes")) {
            return false;
        }
        
        // 默认返回 JSON 格式（为了兼容其他接口）
        return true;
    }

    /**
     * Handles ResponseStatusException (e.g., thrown by services).
     * Returns different response formats based on the request path.
     * This handler must be before RuntimeException handler since ResponseStatusException extends RuntimeException.
     */
    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<?> handleResponseStatusException(ResponseStatusException e) {
        HttpStatus status = HttpStatus.resolve(e.getStatusCode().value());
        if (status == null) {
            status = HttpStatus.INTERNAL_SERVER_ERROR;
        }
        
        boolean shouldReturnJson = shouldReturnJsonError();
        
        // 根据接口设计，400 状态码时根据路径返回不同格式
        if (status == HttpStatus.BAD_REQUEST) {
            if (shouldReturnJson) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(new java.util.HashMap<>());
            } else {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
            }
        }
        
        // 404 状态码也根据路径返回不同格式
        if (status == HttpStatus.NOT_FOUND) {
            if (shouldReturnJson) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(new java.util.HashMap<>());
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
            }
        }
        
        // 401 状态码根据路径返回不同格式
        if (status == HttpStatus.UNAUTHORIZED) {
            if (shouldReturnJson) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(new java.util.HashMap<>());
            } else {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            }
        }
        
        // 其他状态码返回空 body
        return ResponseEntity.status(status).build();
    }

    /**
     * Handles validation exceptions (e.g., failed @NotNull, @NotEmpty checks).
     * Returns 400 with different formats based on the request path.
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<?> handleValidationException(MethodArgumentNotValidException e) {
        boolean shouldReturnJson = shouldReturnJsonError();
        
        if (shouldReturnJson) {
            // 提取验证错误信息
            String errorMessage = "请求参数验证失败";
            if (e.getBindingResult() != null && e.getBindingResult().hasFieldErrors()) {
                var fieldError = e.getBindingResult().getFieldError();
                if (fieldError != null && fieldError.getDefaultMessage() != null) {
                    errorMessage = fieldError.getDefaultMessage();
                }
            }
            
            java.util.Map<String, Object> errorResponse = new java.util.HashMap<>();
            errorResponse.put("message", errorMessage);
            errorResponse.put("code", 400);
            
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    /**
     * Handles business exceptions (e.g., "Book already exists", "Book not found").
     * Returns 400 with different formats based on the request path.
     * Note: This handler should not catch ResponseStatusException as it's handled above.
     */
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<?> handleBusinessException(RuntimeException e) {
        boolean shouldReturnJson = shouldReturnJsonError();
        if (shouldReturnJson) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new java.util.HashMap<>());
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
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