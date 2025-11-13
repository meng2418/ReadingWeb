package com.weread.dto;

import lombok.Data;

/**
 * 全局统一响应类（放在dto层）
 * 所有接口的返回结果都通过该类封装
 *
 * @param <T> 响应数据的类型（成功时返回的数据）
 */
@Data
public class Result<T> {
    // 状态码：200-成功，400-业务错误，500-系统错误
    private int code;
    // 提示信息：成功/错误描述
    private String message;
    // 响应数据：成功时返回的具体数据（如书籍列表、用户信息等）
    private T data;

    /**
     * 成功响应（带数据）
     */
    public static <T> Result<T> success(T data) {
        Result<T> result = new Result<>();
        result.setCode(200);
        result.setMessage("操作成功");
        result.setData(data);
        return result;
    }

    /**
     * 成功响应（无数据）
     */
    public static <T> Result<T> success() {
        return success(null);
    }

    /**
     * 失败响应（带错误信息）
     */
    public static <T> Result<T> fail(String message) {
        Result<T> result = new Result<>();
        result.setCode(400);
        result.setMessage(message);
        result.setData(null);
        return result;
    }
}