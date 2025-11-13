package com.weread.handler;

import com.weread.dto.Result;
import org.springframework.http.HttpStatus;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * 全局异常处理器（放在handler层）
 * 统一捕获并处理Controller层抛出的所有异常
 */
@RestControllerAdvice // 作用于所有@RestController注解的类
public class GlobalExceptionHandler {

    /**
     * 处理业务异常（如"书籍已在书架中"、"书籍不存在"等自定义异常）
     */
    @ExceptionHandler(RuntimeException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST) // 返回400状态码
    public Result<Void> handleBusinessException(RuntimeException e) {
        return Result.fail(e.getMessage());
    }

    /**
     * 处理参数校验异常（如@NotNull、@NotEmpty等注解校验失败）
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST) // 返回400状态码
    public Result<Void> handleValidationException(MethodArgumentNotValidException e) {
        // 提取校验失败的字段和错误信息（例如"bookId不能为空"）
        FieldError fieldError = e.getBindingResult().getFieldError();
        String errorMessage = fieldError != null ? fieldError.getDefaultMessage() : "参数校验失败";
        return Result.fail(errorMessage);
    }

    /**
     * 处理系统异常（如数据库连接失败、空指针等未捕获的异常）
     */
    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR) // 返回500状态码
    public Result<Void> handleSystemException(Exception e) {
        // 生产环境建议记录日志（e.printStackTrace()仅用于开发调试）
        e.printStackTrace();
        return Result.fail("系统繁忙，请稍后再试");
    }
}