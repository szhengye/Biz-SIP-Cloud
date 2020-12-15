package com.bizmda.bizsip.common;

import java.util.HashMap;
import java.util.Map;

/**
 * @author shizhengye
 */

public enum BizResultEnum {

    // 聚合器错误（1-99）
    INTEGRATOR_SERVICE_NOT_FOUND(1,"聚合服务不存在"),
    // 客户端适配器错误（100-199）
    // 服务端适配器错误（200-299）
    SERVER_PROTOCOL_CREATE_ERROR(200,"服务端适配器协议处理Java类创建失败"),
    SERVER_NO_PROTOCOL_PROCESSOR(202,"没有设置协议接入处理器"),
    SERVER_PROTOCOL_JAVA_CREATE_ERROR(204,"服务端适配器Java协议处理创建失败"),
    // 消息转换错误（300-399
    NO_MESSAGE_PROCESSOR(301,"没有设置消息转换处理器"),
    NO_MESSAGE_MATCH_RULE(302,"没有找到匹配的消息断言规则"),
    MESSAGE_CREATE_ERROR(303,"消息处理器Java类创建失败"),
    // 其它错误（1000以上）
    EL_CALCULATE_ERROR(1004,"EL表达式计算出错"),
    NO_MATCH_SERVICE_RULE(1005,"没有找到匹配的断言规则"),

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
