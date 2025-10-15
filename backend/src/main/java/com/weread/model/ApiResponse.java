package com.weread.model;

import lombok.Data;

@Data
public class ApiResponse<T> {
    private Integer code;
    private String message;
    private T data;
    
    public static <T> ApiResponse<T> success(T data) {
        ApiResponse<T> response = new ApiResponse<>();
        response.setCode(200);
        response.setMessage("成功");
        response.setData(data);
        return response;
    }
    
    public static ApiResponse<?> error(Integer code, String message) {
        ApiResponse<?> response = new ApiResponse<>();
        response.setCode(code);
        response.setMessage(message);
        return response;
    }
}