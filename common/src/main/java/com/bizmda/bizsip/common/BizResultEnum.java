package com.bizmda.bizsip.common;

public enum BizResultEnum {
    SUCCESS(0,"成功"),
    // 聚合器错误（1-99）
    // 客户端适配器错误（100-199）
    // 服务端适配器错误（200-299）
    SERVER_ADAPTOR_JAVA_ERROR(200,"服务端适配器Java协议类创建失败"),
    SERVER_ADAPTOR_NO_MESSAGE_PROCESSOR_ERROR(201,"没有设置消息转换处理器"),
    SERVER_ADAPTOR_NO_PROTOCOL_PROCESSOR_ERROR(202,"没有设置协议接入处理器");
    // 其它错误（1000以上）

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
