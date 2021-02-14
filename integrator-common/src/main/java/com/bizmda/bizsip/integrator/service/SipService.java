package com.bizmda.bizsip.integrator.service;

import cn.hutool.extra.spring.SpringUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.bizmda.bizsip.common.*;
import com.bizmda.bizsip.config.RestServerAdaptorConfig;
import com.bizmda.bizsip.config.ServerAdaptorConfigMapping;
import com.bizmda.bizsip.integrator.tm.TmService;
//import com.open.capacity.redis.util.RedisUtil;
import com.open.capacity.redis.util.RedisUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

/**
 * SIP聚合服务API调用用接口
 */
@Slf4j
@Service
public class SipService {
    public static final String PREFIX_SIP_ASYNCLOG = "sip:asynclog:";
    @Autowired
    private RestTemplate restTemplate;
    @Autowired
    private ServerAdaptorConfigMapping serverAdaptorConfigMapping;
    @Autowired
    private TmService tmService;
    @Autowired
    private RedisUtil redisUtil ;

    /**
     * 执行服务适配器服务的调用
     * @param adaptorId 服务适配器ID
     * @param inData 传入数据
     * @return 返回数据，为BizMessage格式
     */
    public BizMessage<JSONObject> doServerService( String adaptorId, Object inData) {
        JSONObject jsonObject = JSONUtil.parseObj(inData);

        if (serverAdaptorConfigMapping == null) {
            serverAdaptorConfigMapping = SpringUtil.getBean("serverAdaptorConfigMapping");
        }

        BizMessage<JSONObject> inMessage = BizUtils.bizMessageThreadLocal.get();
        inMessage.setData(jsonObject);

        RestServerAdaptorConfig serverAdaptorConfig = (RestServerAdaptorConfig) serverAdaptorConfigMapping.getServerAdaptorConfig(adaptorId);
        log.debug("doService()请求:\n{}",inMessage);
        BizMessage<JSONObject> outMessage = restTemplate.postForObject(serverAdaptorConfig.getUrl(), inMessage, BizMessage.class);
        log.debug("doService()响应:\n{}",outMessage);
        return outMessage;
    }

    /**
     * 调用存储转发（SAF）服务
     * @param serviceId 调用的服务ID
     * @param inData 传入数据
     * @return 返回数据，为BizMessage格式
     */
    public BizMessage<JSONObject> doSafService(String serviceId,Object inData) {
        JSONObject jsonObject = JSONUtil.parseObj(inData);

        BizMessage<JSONObject> inMessage = BizUtils.bizMessageThreadLocal.get();
        inMessage.setData(jsonObject);
        if (tmService == null) {
            tmService = SpringUtil.getBean("tmService");
        }

        log.debug("doSafService()请求:\n{}",inMessage);
        BizMessage<JSONObject> outMessage = tmService.doSafService(serviceId,inMessage);
        log.debug("doSafService()响应:\n{}",outMessage);
        return outMessage;
    }

    /**
     * 设置SAF服务的延迟执行时间
     * @param delayTime 延迟执行时间，单位为ms
     */
    public void setTmDelayTime(int delayTime) {
        TmContext tmContext = BizUtils.tmContextThreadLocal.get();
        tmContext.setDelayTime(delayTime);
        BizUtils.tmContextThreadLocal.set(tmContext);
    }

    /**
     * 获取设置的SAF服务延迟执行时间，单位为ms
     * @return
     */
    public int getTmDelayTime() {
        TmContext tmContext = BizUtils.tmContextThreadLocal.get();
        return tmContext.getDelayTime();
    }

    /**
     * 获取SAF服务的当前重试次数
     * @return 重试次数
     */
    public int getTmRetryCount() {
        TmContext tmContext = BizUtils.tmContextThreadLocal.get();
        return tmContext.getRetryCount();
    }

    /**
     * 设置当前SAF服务的运行状态
     * @param status 设置的SAF服务状态，有"success","error"，"retry"三种
     */
    public void setTmServiceStatus(String status) {
        TmContext tmContext = BizUtils.tmContextThreadLocal.get();
        if (tmContext == null) {
            return;
        }
        if ("success".equalsIgnoreCase(status)) {
            tmContext.setServiceStatus(TmContext.SERVICE_STATUS_SUCCESS);
        }
        else if("error".equalsIgnoreCase(status)) {
            tmContext.setServiceStatus(TmContext.SERVICE_STATUS_ERROR);
        }
        else if("retry".equalsIgnoreCase(status)) {
            tmContext.setServiceStatus(TmContext.SERVICE_STATUS_RETRY);
        }
        BizUtils.tmContextThreadLocal.set(tmContext);
    }

    /**
     * 保存异步服务上下文
     * @param transactionKey 异步回调的全局唯一交易索引键
     * @param context 注入回调聚合服务的上下文变量
     * @param timeout 异步服务超时时间，单位（秒）
     */
    public void saveAsyncContext(String transactionKey,Object context,long timeout) {
        BizMessage bizMessage = BizUtils.bizMessageThreadLocal.get();
        Map<String,Object> map = new HashMap<>();
        map.put("traceId",bizMessage.getTraceId());
        map.put("context",context);
        this.redisUtil.set(PREFIX_SIP_ASYNCLOG +transactionKey, context, timeout);
    }

    /**
     * 恢复异步服务上下文
     * @param transactionKey 异步回调的全局唯一交易索引键
     * @return 异步服务上下文
     */
    public Object loadAsyncContext(String transactionKey) throws BizException {
        Map<String, Object> map = (Map<String, Object>) this.redisUtil.get(PREFIX_SIP_ASYNCLOG + transactionKey);
        if (map == null) {
            throw new BizException(BizResultEnum.ASYNC_SERVICE_CONTEXT_NOT_FOUND);
        }
        String traceId = (String) map.get("traceId");
        Object context = map.get("context");
        BizMessage bizMessage = BizUtils.bizMessageThreadLocal.get();
        if (bizMessage.getParentTraceId() == null) {
            bizMessage.setParentTraceId(traceId);
            BizUtils.bizMessageThreadLocal.set(bizMessage);
            return context;
        } else if (bizMessage.getParentTraceId() == traceId) {
            return context;
        } else {
            throw new BizException(BizResultEnum.ASYNC_SERVICE_PARENT_TRANCTION_BINDDING_EOORO);
        }
    }
}
