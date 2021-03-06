package com.bizmda.bizsip.integrator.executor.script;

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
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.ssssssss.magicapi.config.MagicModule;
import org.ssssssss.script.annotation.Comment;

/**
 * @author 史正烨
 */
@Slf4j
@Service
public class SipFunctions implements MagicModule {

    private static ServerAdaptorConfigMapping serverAdaptorConfigMapping = null;
    private static RestTemplate restTemplate = null;
    private static TmService tmService = null;

    @Comment("执行适配器服务调用")
    public static BizMessage<JSONObject> doService(@Comment("服务ID") String adaptorId, @Comment("调用输入参数") Object inData) {
        JSONObject jsonObject = JSONUtil.parseObj(inData);
        if (restTemplate == null) {
            restTemplate = SpringUtil.getBean("restTemplate");
        }
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

    @Comment("执行SAF服务调用")
    public static BizMessage<JSONObject> doSafService(@Comment("服务ID") String serviceId, @Comment("调用输入参数") Object inData) {
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

    @Comment("设置SAF服务的延迟执行时间")
    public static void setTmDelayTime(@Comment("延迟执行时间") int delayTime) {
        TmContext tmContext = BizUtils.tmContextThreadLocal.get();
        tmContext.setDelayTime(delayTime);
        BizUtils.tmContextThreadLocal.set(tmContext);
    }

    @Comment("获取SAF服务的延迟执行时间")
    public static int getTmDelayTime() {
        TmContext tmContext = BizUtils.tmContextThreadLocal.get();
        return tmContext.getDelayTime();
    }

    @Comment("获取SAF服务的当前重试次数")
    public static int getTmRetryCount() {
        TmContext tmContext = BizUtils.tmContextThreadLocal.get();
        return tmContext.getRetryCount();
    }

    @Comment("设置当前SAF服务的运行状态")
    public static void setTmServiceStatus(@Comment("SAF服务运行状态") String status) {
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

    @Override
    public String getModuleName() {
        return "sip";
    }
}
