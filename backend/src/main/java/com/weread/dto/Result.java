package com.weread.dto;

import lombok.Data;

/**
 * Global unified response class (Result DTO).
 * All interface return results are encapsulated through this.
 *
 * @param <T> Type of the response data (data returned on success)
 */
@Data
public class Result<T> {
    // Status code: 200-Success, 400-Business Error, 500-System Error
    private int code;
    // Display message: Success/Error details
    private String message;
    // Response data: Concrete data returned on success (e.g., list of books, user info)
    private T data;

    /**
     * Success response with data.
     */
    public static <T> Result<T> success(T data) {
        Result<T> result = new Result<>();
        result.setCode(200);
        result.setMessage("操作成功"); // Use clean Chinese string
        result.setData(data);
        return result;
    }

    /**
     * Success response without data.
     */
    public static <T> Result<T> success() {
        return success(null);
    }

    /**
     * Failure response with an error message.
     */
    public static <T> Result<T> fail(String message) {
        Result<T> result = new Result<>();
        result.setCode(400);
        result.setMessage(message);
        result.setData(null);
        return result;
    }
}