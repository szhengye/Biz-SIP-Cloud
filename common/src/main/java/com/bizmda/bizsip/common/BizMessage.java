package com.bizmda.bizsip.common;

import cn.hutool.core.util.IdUtil;
import cn.hutool.json.JSONObject;
import lombok.Data;

import java.util.Map;

/**
 * @author 史正烨
 */
@Data
public class BizMessage<T> {
//    // 缺省为10000毫秒
//    public static final String FIELD_SAF_DELAY_TIME = "sip_delay_time";
//    public static final String FIELD_SAF_DONE_NUM = "sip_done_num";
//    // error：错误，success：成功
//    public static final String FIELD_SAF_SERVICE_STATUS = "sip_service_status";

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

    public static BizMessage createNewTransaction() {
        BizMessage bizMessage = new BizMessage();
        bizMessage.traceId = IdUtil.fastSimpleUUID();
        bizMessage.timestamp = System.currentTimeMillis();
        return bizMessage;
    }

    public static BizMessage createChildTransaction(BizMessage parentBizMessage) {
        BizMessage bizMessage = new BizMessage();
        bizMessage.traceId = IdUtil.fastSimpleUUID();
        bizMessage.parentTraceId = parentBizMessage.traceId;
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
