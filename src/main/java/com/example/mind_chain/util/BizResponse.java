package com.example.mind_chain.util;

import lombok.Data;

@Data
public class BizResponse<T> {
    private int code;

    private String message;

    private T data;

    public static <T> BizResponse<T> success(T data) {
        BizResponse<T> response = new BizResponse<>();
        response.setCode(ResponseCodeEnum.SUCCESS.getCode());
        response.setMessage(ResponseCodeEnum.SUCCESS.getMessage());
        response.setData(data);
        return response;
    }

    public static <T> BizResponse<T> success() {
        return success(null);
    }

    public static <T> BizResponse<T> fail(ResponseCodeEnum responseCodeEnum) {
        return fail(responseCodeEnum.getCode(), responseCodeEnum.getMessage());
    }

    public static <T> BizResponse<T> fail(int errorCode, String message) {
        BizResponse<T> response = new BizResponse<>();
        response.setCode(errorCode);
        response.setMessage(message);
        return response;
    }
}
