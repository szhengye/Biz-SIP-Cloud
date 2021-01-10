package com.bizmda.bizsip.common;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.json.JSONObject;
import lombok.Data;

/**
 * @author 史正烨
 */
@Data
public class BizMessage<T> {
    private int code;
    private String message;
    private String extMessage;
    private String traceId;
    private String parentTraceId;
    private long timestamp;
    private T data;

    public BizMessage() {

    }

    public BizMessage(JSONObject jsonObject) {
        this.code = (int) jsonObject.get("code");
        this.message = (String)jsonObject.get("message");
        this.extMessage = (String)jsonObject.get("extMessage");
        this.traceId = (String)jsonObject.get("traceId");
        this.parentTraceId = (String)jsonObject.get("parentTraceId");
        this.timestamp = (long)jsonObject.get("timestamp");
        this.data = (T)jsonObject.get("data");
    }

    public static BizMessage<JSONObject> createNewTransaction() {
        BizMessage<JSONObject> bizMessage = new BizMessage<>();
        bizMessage.traceId = IdUtil.fastSimpleUUID();
        bizMessage.timestamp = System.currentTimeMillis();
        return bizMessage;
    }

    public static BizMessage<JSONObject> createChildTransaction(BizMessage<JSONObject> parentBizMessage) {
        BizMessage<JSONObject> bizMessage = new BizMessage<>();
        BeanUtil.copyProperties(parentBizMessage,bizMessage);
        bizMessage.traceId = IdUtil.fastSimpleUUID();
        bizMessage.parentTraceId = parentBizMessage.traceId;
        bizMessage.timestamp = System.currentTimeMillis();
        return bizMessage;
    }

    public static BizMessage buildSuccessMessage(BizMessage inBizMessage,Object data) {
        BizMessage<Object> bizMessage = new BizMessage<>();

        BeanUtil.copyProperties(inBizMessage,bizMessage);

        bizMessage.setCode(0);
        bizMessage.setMessage("success");
        bizMessage.setExtMessage(null);
        bizMessage.setData(data);
        return bizMessage;
    }

    public static BizMessage buildFailMessage(BizMessage inBizMessage,Exception e) {
        BizMessage<Object> bizMessage = new BizMessage<>();

        BeanUtil.copyProperties(inBizMessage,bizMessage);
        if (e instanceof BizException) {
            BizException bizException = (BizException)e;
            bizMessage.setCode(bizException.getCode());
            bizMessage.setMessage(bizException.getMessage());
            bizMessage.setExtMessage(bizException.getExtMessage());
        }
        else {
            bizMessage.setCode(BizResultEnum.OTHER_ERROR.getCode());
            bizMessage.setMessage(e.getMessage());
        }
        bizMessage.data = null;
        return bizMessage;
    }
}
