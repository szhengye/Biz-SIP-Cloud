package com.bizmda.bizsip.common;

import java.util.HashMap;
import java.util.Map;

public enum BizResultEnum {

    // 聚合器错误（1-99）
    // 客户端适配器错误（100-199）
    // 服务端适配器错误（200-299）
    SERVER_PROTOCOL_CREATE_ERROR(200,"服务端适配器协议处理Java类创建失败"),
    SERVER_NO_PROTOCOL_PROCESSOR_ERROR(202,"没有设置协议接入处理器"),
    SERVER_PROTOCOL_JAVA_CREATE_ERROR(204,"服务端适配器Java协议处理创建失败"),
    // 其它错误（1000以上）
    NO_MESSAGE_PROCESSOR_ERROR(1001,"没有设置消息转换处理器"),
    MESSAGE_CREATE_ERROR(1002,"服务端适配器消息处理Java类创建失败"),

    SUCCESS(0,"成功");

    private int code;
    private String message;

    BizResultEnum(int code, String message) {
        this.code = code;
        this.message = message;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
