package com.bizmda.bizsip.common;

import cn.hutool.core.util.IdUtil;
import cn.hutool.json.JSONObject;
import lombok.Data;

import java.util.Map;

/**
 * @author shizhengye
 */
@Data
public class BizMessage<T> {
    private int code;
    private String message;
    private String extMessage;
    private String traceId;
    private long timestamp;
    private T data;

    public BizMessage() {

    }

    public BizMessage(JSONObject jsonObject) {
        this.code = (int) jsonObject.get("code");
        this.message = (String)jsonObject.get("message");
        this.extMessage = (String)jsonObject.get("extMessage");
        this.traceId = (String)jsonObject.get("traceId");
        this.timestamp = (long)jsonObject.get("timestamp");
        this.data = (T)jsonObject.get("data");
    }

    public static BizMessage createNewTransaction() {
        BizMessage bizMessage = new BizMessage();
        bizMessage.traceId = IdUtil.fastSimpleUUID();
        bizMessage.timestamp = System.currentTimeMillis();
        return bizMessage;
    }

    public void success(T data) {
        this.setCode(0);
        this.setMessage("success");
        this.setExtMessage(null);
        this.setData(data);
    }

    public void fail(BizException e) {
        this.setCode(e.getCode());
        this.setMessage(e.getMessage());
        this.setExtMessage(e.getExtMessage());
        this.data = null;
    }
}
