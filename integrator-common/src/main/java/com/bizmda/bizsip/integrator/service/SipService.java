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

@Slf4j
@Service
public class SipService {
    @Autowired
    private RestTemplate restTemplate;
    @Autowired
    private ServerAdaptorConfigMapping serverAdaptorConfigMapping;
    @Autowired
    private TmService tmService;

    public BizMessage<JSONObject> doServerService( String adaptorId, Object inData) {
        JSONObject jsonObject = JSONUtil.parseObj(inData);

        if (serverAdaptorConfigMapping == null) {
            serverAdaptorConfigMapping = SpringUtil.getBean("serverAdaptorConfigMapping");
        }

        BizMessage<JSONObject> inMessage = BizUtils.bizMessageThreadLocal.get();
        inMessage.setData(jsonObject);

        RestServerAdaptorConfig serverAdaptorConfig = (RestServerAdaptorConfig) serverAdaptorConfigMapping.getServerAdaptorConfig(adaptorId);
        log.debug("doService()请求:\n{}",inMessage);
        BizMessage<JSONObject> outMessage = (BizMessage)restTemplate.postForObject(serverAdaptorConfig.getUrl(), inMessage, BizMessage.class);
        log.debug("doService()响应:\n{}",outMessage);
        return outMessage;
    }

    public BizMessage<JSONObject> doSafService(String serviceId,Object inData) {
        JSONObject jsonObject = JSONUtil.parseObj(inData);

        BizMessage<JSONObject> inMessage = BizUtils.bizMessageThreadLocal.get();
        inMessage.setData(jsonObject);
        if (tmService == null) {
            tmService = SpringUtil.getBean("tmService");
        }

        log.debug("doSafService()请求:\n{}",inMessage);
        BizMessage outMessage = tmService.doSafService(serviceId,inMessage);
        log.debug("doSafService()响应:\n{}",outMessage);
        return outMessage;
    }

    public void setTmDelayTime(int delayTime) {
        TmContext tmContext = BizUtils.tmContextThreadLocal.get();
        tmContext.setDelayTime(delayTime);
        BizUtils.tmContextThreadLocal.set(tmContext);
    }

    public int getTmDelayTime() {
        TmContext tmContext = BizUtils.tmContextThreadLocal.get();
        return tmContext.getDelayTime();
    }

    public int getTmRetryCount() {
        TmContext tmContext = BizUtils.tmContextThreadLocal.get();
        return tmContext.getRetryCount();
    }

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
