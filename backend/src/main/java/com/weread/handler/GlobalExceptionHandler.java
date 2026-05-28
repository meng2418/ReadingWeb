package com.weread.handler;

import com.weread.dto.Result;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.server.ResponseStatusException;
import lombok.extern.slf4j.Slf4j;

/**
 * Global Exception Handler (Centralized exception handling using @ControllerAdvice).
 * Unifies the response format and handles exceptions thrown by Controllers.
 */
@RestControllerAdvice // Applies to all @RestController annotated classes
@Slf4j
public class GlobalExceptionHandler {

    private boolean isAiChatRequest(String requestPath) {
        return requestPath != null && requestPath.startsWith("/ai/chat");
    }

    private String getCurrentRequestPath() {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attributes == null) {
            return null;
        }
        HttpServletRequest request = attributes.getRequest();
        return request.getRequestURI();
    }

    /**
     * 根据请求路径判断是否应该返回 JSON 格式的错误响应
     * @return true 表示应该返回 JSON 格式，false 表示返回空 body
     */
    private boolean shouldReturnJsonError() {
        String requestPath = getCurrentRequestPath();
        if (requestPath == null) {
            return false;
        }
        
        // /books/{bookId}/mark-finished 接口需要返回 JSON 格式
        if (requestPath != null && requestPath.matches("/books/\\d+/mark-finished")) {
            return true;
        }
        
        // /user/notes 接口需要返回 JSON 格式（401响应）
        if (requestPath != null && requestPath.equals("/user/notes")) {
            return true;
        }

        // /user 下接口统一返回标准 JSON 错误体
        if (requestPath != null && requestPath.startsWith("/user/")) {
            return true;
        }

        // /chat 下接口统一返回标准 JSON 错误体
        if (requestPath != null && requestPath.startsWith("/chat/")) {
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
        String requestPath = getCurrentRequestPath();
        if (isAiChatRequest(requestPath)) {
            return ResponseEntity.status(status).body(Result.fail(e.getReason() != null ? e.getReason() : e.getMessage()));
        }
        
        boolean shouldReturnJson = shouldReturnJsonError();
        
        // 根据接口设计，400 状态码时根据路径返回不同格式
        if (status == HttpStatus.BAD_REQUEST) {
            if (shouldReturnJson && requestPath != null
                    && (requestPath.startsWith("/user/") || requestPath.startsWith("/chat/"))) {
                java.util.Map<String, Object> body = new java.util.HashMap<>();
                body.put("code", status.value());
                body.put("message", e.getReason() != null ? e.getReason() : "请求处理失败");
                body.put("data", null);
                return ResponseEntity.status(status).body(body);
            }
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

    private java.util.Map<String, Object> standardErrorBody(int code, String message) {
        java.util.Map<String, Object> body = new java.util.HashMap<>();
        body.put("code", code);
        body.put("message", message);
        body.put("data", null);
        return body;
    }

    /**
     * Handles validation exceptions (e.g., failed @NotNull, @NotEmpty checks).
     * Returns 400 with different formats based on the request path.
     */
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<?> handleTypeMismatchException(MethodArgumentTypeMismatchException e) {
        String requestPath = getCurrentRequestPath();
        if (requestPath != null && (requestPath.startsWith("/user/") || requestPath.startsWith("/chat/"))) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(standardErrorBody(400, "请求参数格式错误"));
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new java.util.HashMap<>());
    }

    /**
     * Handles validation exceptions (e.g., failed @NotNull, @NotEmpty checks).
     * Returns 400 with different formats based on the request path.
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<?> handleValidationException(MethodArgumentNotValidException e) {
        String requestPath = getCurrentRequestPath();
        if (isAiChatRequest(requestPath)) {
            String errorMessage = "请求参数验证失败";
            if (e.getBindingResult() != null && e.getBindingResult().hasFieldErrors()) {
                var fieldError = e.getBindingResult().getFieldError();
                if (fieldError != null && fieldError.getDefaultMessage() != null) {
                    errorMessage = fieldError.getDefaultMessage();
                }
            }
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Result.fail(errorMessage));
        }

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
        String requestPath = getCurrentRequestPath();
        if (isAiChatRequest(requestPath)) {
            log.error("AI chat request failed. path={}, message={}", requestPath, e.getMessage(), e);
            String message = e.getMessage();
            if (message == null || message.isBlank()) {
                message = "AI chat request failed";
            }
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Result.fail(message));
        }

        boolean shouldReturnJson = shouldReturnJsonError();
        if (shouldReturnJson && requestPath != null
                && (requestPath.startsWith("/user/") || requestPath.startsWith("/chat/"))) {
            java.util.Map<String, Object> body = new java.util.HashMap<>();
            body.put("code", 400);
            body.put("message", e.getMessage() != null ? e.getMessage() : "请求处理失败");
            body.put("data", null);
            log.error("请求失败 path={}", requestPath, e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body);
        }
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