package com.bizmda.bizsip.integrator.tm;

import cn.hutool.core.text.StrFormatter;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.bizmda.bizsip.common.*;
import com.bizmda.bizsip.integrator.config.IntegratorServiceMapping;
import com.bizmda.bizsip.integrator.config.RabbitmqConfig;
import com.bizmda.bizsip.integrator.service.AbstractIntegratorService;
import com.bizmda.bizsip.integrator.service.SipServiceLogService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * RabbitMQ接收服务
 */
@Slf4j
@Service
public class TmDelayQueueReceiverService {
    @Autowired
    private IntegratorServiceMapping integratorServiceMapping;
    @Autowired
    private TmService tmService;
    @Autowired
    private SipServiceLogService sipServiceLogService;

    @RabbitListener(queues = RabbitmqConfig.TM_DELAY_QUEUE, containerFactory = "multiListenerContainer")
    public void TmDelayQueueListener(Map map) {
        log.info("收到:{}",map);
        String serviceId = (String)map.get("serviceId");
        int retryCount = (int)map.get("retryCount");
        JSONObject jsonObject = (JSONObject)JSONUtil.parse(map.get("bizmessage"));
        BizMessage inBizMessage = new BizMessage(jsonObject);
        BizMessage outBizMessage;
        AbstractIntegratorService integratorService = this.integratorServiceMapping.getIntegratorService(serviceId);
        if (integratorService == null) {
            outBizMessage = BizMessage.buildFailMessage(inBizMessage
                    ,new BizException(BizResultEnum.INTEGRATOR_SERVICE_NOT_FOUND
                    ,StrFormatter.format("聚合服务不存在:{}",serviceId)));
            this.sipServiceLogService.saveErrorServiceLog(inBizMessage,outBizMessage);
            return;
        }
        TmContext tmContext = new TmContext();
        tmContext.setRetryCount(retryCount+1);

        BizUtils.tmContextThreadLocal.set(tmContext);
        BizUtils.bizMessageThreadLocal.set(inBizMessage);

        try {
            outBizMessage = integratorService.doBizService(inBizMessage);
        }
        finally {
            tmContext = BizUtils.tmContextThreadLocal.get();
            BizUtils.tmContextThreadLocal.remove();
            BizUtils.bizMessageThreadLocal.remove();
        }
//        JSONObject jsonObject1 = (JSONObject)outBizMessage.getData();

        if (outBizMessage.getCode() != 0) {
            tmContext.setServiceStatus(TmContext.SERVICE_STATUS_ERROR);
        }
        if (tmContext.getServiceStatus() == TmContext.SERVICE_STATUS_ERROR) {
            outBizMessage = BizMessage.buildFailMessage(inBizMessage
                    ,new BizException(BizResultEnum.SCRIPT_RETURN_ERROR));
            this.sipServiceLogService.saveErrorServiceLog(inBizMessage,outBizMessage);
            return;
        } else if (tmContext.getServiceStatus() == TmContext.SERVICE_STATUS_SUCCESS) {
            this.sipServiceLogService.saveSuccessServiceLog(inBizMessage,outBizMessage);
            return;
        }
        this.tmService.sendDelayQueue(serviceId,outBizMessage,tmContext);
    }
}
