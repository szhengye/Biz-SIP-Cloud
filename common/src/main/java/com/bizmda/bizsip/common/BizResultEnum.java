package com.bizmda.bizsip.common;

import java.util.HashMap;
import java.util.Map;

/**
 * @author 史正烨
 */

public enum BizResultEnum {

    // 聚合器错误（1-99）
    INTEGRATOR_SERVICE_NOT_FOUND(1,"聚合服务不存在"),
    INTEGRATOR_SERVICE_CLASS_LOAD_ERROR(1,"聚合服务实现类装载失败"),
    // 客户端适配器错误（100-199）
    // 服务端适配器错误（200-299）
    SERVER_PROTOCOL_CREATE_ERROR(200,"服务端适配器协议处理Java类创建失败"),
    SERVER_NO_PROTOCOL_PROCESSOR(202,"没有设置协议接入处理器"),
    SERVER_PROTOCOL_JAVA_CREATE_ERROR(204,"服务端适配器Java协议处理创建失败"),
    SERVER_ADATPOR_FILE_NOTFOUND(205,"server-adaptor.yml文件不存在"),
    // 消息转换错误（300-399
    NO_MESSAGE_PROCESSOR(301,"没有设置消息转换处理器"),
    NO_MESSAGE_MATCH_RULE(302,"没有找到匹配的消息断言规则"),
    NO_MESSAGE_CONFIG_FILE(303,"对应的消息转换配置文件不存在"),
    MESSAGE_CREATE_ERROR(304,"消息处理器Java类创建失败"),
    NO_FIELD_FUNCTION_IMPL(305,"域处理函数没有找到实现方法"),
    FIELD_FUNCTION_IMPL_ERROR(306,"域处理函数执行出错"),
    FIELD_CHECK_ERROR(307,"域校验出错"),
    // 其它错误（1000以上）
    EL_CALCULATE_ERROR(1004,"EL表达式计算出错"),
    NO_MATCH_SERVICE_RULE(1005,"没有找到匹配的断言规则"),
    NETTY_CONNECT_ERROR(1006,"连接Netty服务端失败"),
    NACOS_ERROR(1007,"Nacos服务出错"),
    FIELD_CHECK_FUNCTION_NOTFOUND(1008,"域校验函数不存在"),
    FIELD_CHECK_THREAD_ERROR(1009,"域校验函数线程执行错误"),
    SERVICE_CHECK_THREAD_ERROR(1010,"服务校验线程执行错误"),
    CHECK_RULE_FILE_NOTFOUND(1011,"检验规划文件不存在"),
    SERVICE_CHECK_ERROR(1012,"服务规则校验出错"),

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
