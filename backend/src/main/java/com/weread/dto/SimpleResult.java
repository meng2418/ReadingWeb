package com.weread.dto;

import lombok.Data;

@Data
public class SimpleResult {
    private int code;
    private String message;
    
    public static SimpleResult success() {
        SimpleResult result = new SimpleResult();
        result.setCode(200);
        result.setMessage("success");
        return result;
    }
    
    public static SimpleResult fail(String message) {
        SimpleResult result = new SimpleResult();
        result.setCode(400);
        result.setMessage(message);
        return result;
    }
}