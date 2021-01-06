package com.bizmda.bizsip.integrator.tm;

import cn.hutool.core.text.StrFormatter;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.bizmda.bizsip.common.BizMessage;
import com.bizmda.bizsip.common.BizResultEnum;
import com.bizmda.bizsip.common.BizUtils;
import com.bizmda.bizsip.common.TmContext;
import com.bizmda.bizsip.integrator.config.IntegratorServiceMapping;
import com.bizmda.bizsip.integrator.config.RabbitmqConfig;
import com.bizmda.bizsip.integrator.service.AbstractIntegratorService;
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

    @RabbitListener(queues = RabbitmqConfig.TM_DELAY_QUEUE, containerFactory = "multiListenerContainer")
    public void TmDelayQueueListener(Map map) {
        log.info("收到:{}",map);
        String serviceId = (String)map.get("serviceId");
        int retryCount = (int)map.get("retryCount");
        JSONObject jsonObject = (JSONObject)JSONUtil.parse(map.get("bizmessage"));
        BizMessage bizMessage = new BizMessage(jsonObject);
        AbstractIntegratorService integratorService = this.integratorServiceMapping.getIntegratorService(serviceId);
        if (integratorService == null) {
            this.tmService.abortTransaction(BizResultEnum.INTEGRATOR_SERVICE_NOT_FOUND.getCode(),
                    StrFormatter.format("聚合服务不存在:{}",serviceId),bizMessage);
        }
        TmContext tmContext = new TmContext();
        tmContext.setRetryCount(retryCount+1);

        BizUtils.tmContextThreadLocal.set(tmContext);
        BizUtils.bizMessageThreadLocal.set(bizMessage);
        BizMessage outMessage;
        try {
            outMessage = integratorService.doBizService(bizMessage);
        }
        finally {
            tmContext = BizUtils.tmContextThreadLocal.get();
            BizUtils.tmContextThreadLocal.remove();
            BizUtils.bizMessageThreadLocal.remove();
        }
//        JSONObject jsonObject1 = (JSONObject)outMessage.getData();

        if (outMessage.getCode() != 0) {
            tmContext.setServiceStatus(TmContext.SERVICE_STATUS_ERROR);
        }
        this.tmService.sendDelayQueue(serviceId,outMessage,tmContext);
    }


}
