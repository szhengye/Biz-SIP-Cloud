package com.bizmda.bizsip.common;

import lombok.Data;

import java.util.Map;

@Data
public class BizMessage<T> {
    private int code;
    private String message;
    private T data;

    public static BizMessage success(Object data) {
        BizMessage bizMessage = new BizMessage();
        bizMessage.setCode(0);
        bizMessage.setMessage("success");
        bizMessage.setData(data);
        return bizMessage;
    }

    public static BizMessage fail(BizException e) {
        BizMessage bizMessage = new BizMessage();
        bizMessage.setCode(e.getBizResultEnum().getCode());
        bizMessage.setMessage(e.getBizResultEnum().getMessage());
        return bizMessage;
    }
}
