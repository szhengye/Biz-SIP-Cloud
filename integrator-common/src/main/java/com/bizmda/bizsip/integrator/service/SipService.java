package com.bizmda.bizsip.integrator.service;

import cn.hutool.extra.spring.SpringUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.bizmda.bizsip.common.BizMessage;
import com.bizmda.bizsip.common.BizUtils;
import com.bizmda.bizsip.common.TmContext;
import com.bizmda.bizsip.config.RestServerAdaptorConfig;
import com.bizmda.bizsip.config.ServerAdaptorConfigMapping;
import com.bizmda.bizsip.integrator.tm.TmService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

/**
 * SIP聚合服务API调用用接口
 */
@Slf4j
@Service
public class SipService {
    @Autowired
    private RestTemplate restTemplate;
    @Autowired
    private ServerAdaptorConfigMapping serverAdaptorConfigMapping;
    @Autowired
    private TmService tmService;

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

}
