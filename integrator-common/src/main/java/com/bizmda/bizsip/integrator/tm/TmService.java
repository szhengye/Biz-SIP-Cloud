package com.bizmda.bizsip.integrator.tm;

import cn.hutool.core.text.StrFormatter;
import cn.hutool.json.JSONObject;
import com.bizmda.bizsip.common.*;
import com.bizmda.bizsip.integrator.config.IntegratorServiceMapping;
import com.bizmda.bizsip.integrator.config.RabbitmqConfig;
import com.bizmda.bizsip.integrator.executor.AbstractIntegratorExecutor;
import com.bizmda.bizsip.integrator.executor.SipServiceLogService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageDeliveryMode;
import org.springframework.amqp.core.MessagePostProcessor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
public class TmService {
    @Autowired
    private RabbitTemplate rabbitTemplate;
    @Autowired
    IntegratorServiceMapping integratorServiceMapping;
    @Autowired
    private SipServiceLogService sipServiceLogService;

    public BizMessage<JSONObject> doSafService(String serviceId, BizMessage<JSONObject> bizMessage) {
        AbstractIntegratorExecutor integratorService = this.integratorServiceMapping.getIntegratorService(serviceId);
        if (integratorService == null) {
            return BizMessage.buildFailMessage(bizMessage,
                    new BizException(BizResultEnum.INTEGRATOR_SERVICE_NOT_FOUND,
                    StrFormatter.format("聚合服务不存在:{}",serviceId)));
        }
        BizMessage<JSONObject> childBizMessage = BizMessage.createChildTransaction(bizMessage);

        TmContext tmContext = BizUtils.tmContextThreadLocal.get();

        this.sendDelayQueue(serviceId,childBizMessage,tmContext);
        return childBizMessage;
    }

    public void sendDelayQueue(String serviceId, BizMessage<JSONObject> bizMessage, TmContext tmContext) {
        this.sipServiceLogService.saveProcessingServiceLog(bizMessage);
        Map<String,Object> map = new HashMap<>();
        map.put("serviceId",serviceId);
        map.put("bizmessage",bizMessage);
        map.put("retryCount",tmContext.getRetryCount());
        rabbitTemplate.convertAndSend(RabbitmqConfig.TM_DELAY_EXCHANGE, RabbitmqConfig.TM_DELAY_ROUTING_KEY, map,
                new MessagePostProcessor() {
                    @Override
                    public Message postProcessMessage(Message message) {
                        //设置消息持久化
                        message.getMessageProperties().setDeliveryMode(MessageDeliveryMode.PERSISTENT);
                        message.getMessageProperties().setDelay(tmContext.getDelayTime());
                        return message;
                    }
                });
    }

}
