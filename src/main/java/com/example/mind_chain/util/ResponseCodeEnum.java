package com.example.mind_chain.util;


import lombok.Getter;

@Getter
public enum ResponseCodeEnum {
    // 基础
    SUCCESS(0, "请求成功"),
    FAIL(1, "请求失败"),

    ACCOUNT_EXIST(2,"账号已存在"),
    ERROR(3, "程序执行出错"),
    PARAM_ERROR(4, "参数校验异常"),
    DATA_NOT_EXIST(5,"暂无数据");


    // 编码
    private int code;

    // 消息
    private String message;

    // 构造方法
    ResponseCodeEnum(int code, String msg) {
        this.code = code;
        this.message = msg;
    }
}
