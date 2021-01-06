package com.bizmda.bizsip.integrator.tm;

import cn.hutool.core.text.StrFormatter;
import cn.hutool.json.JSONObject;
import com.bizmda.bizsip.common.*;
import com.bizmda.bizsip.integrator.config.IntegratorServiceMapping;
import com.bizmda.bizsip.integrator.config.RabbitmqConfig;
import com.bizmda.bizsip.integrator.service.AbstractIntegratorService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.AmqpException;
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

    public BizMessage doSafService(String serviceId, BizMessage bizMessage) {
        AbstractIntegratorService integratorService = this.integratorServiceMapping.getIntegratorService(serviceId);
        if (integratorService == null) {
            bizMessage.fail(new BizException(BizResultEnum.INTEGRATOR_SERVICE_NOT_FOUND,
                    StrFormatter.format("聚合服务不存在:{}",serviceId)));
            return bizMessage;
        }

//        JSONObject inJsonObject = (JSONObject)childBizMessage.getData();
        TmContext tmContext = BizUtils.tmContextThreadLocal.get();
//        int safDelayTime = tmContext.getDelayTime();
//        if (safDelayTime <= 0) {
//            // 保存父交易上下文环境
//            BizMessage<JSONObject> parentBizMessage = BizUtils.bizMessageThreadLocal.get();
//            int retryCount = tmContext.getRetryCount();
//            // 设置子交易上下文环境
//            BizUtils.bizMessageThreadLocal.set(childBizMessage);
//            tmContext.setRetryCount(retryCount+1);
//            BizUtils.tmContextThreadLocal.set(tmContext);
//            BizMessage outMessage = integratorService.doBizService(childBizMessage);
//            if (outMessage.getCode() != 0) {
//                tmContext.setServiceStatus(TmContext.SERVICE_STATUS_ERROR);
//            }
//            this.sendDelayQueue(serviceId,outMessage,tmContext);
////            outMessage.setParentTraceId(bizMessage.getParentTraceId());
//            // 恢复父交易上下文环境
//            tmContext.setRetryCount(retryCount);
//            BizUtils.tmContextThreadLocal.set(tmContext);
//            BizUtils.bizMessageThreadLocal.set(parentBizMessage);
//            return outMessage;
//        }
//        else {
//            this.sendDelayQueue(serviceId,childBizMessage,tmContext);
//            return bizMessage;
//        }
        return this.sendDelayQueue(serviceId,bizMessage,tmContext);
    }

    public BizMessage sendDelayQueue(String serviceId, BizMessage bizMessage, TmContext tmContext) {
        if (tmContext.getServiceStatus() == TmContext.SERVICE_STATUS_ERROR) {
            this.abortTransaction(BizResultEnum.SCRIPT_RETURN_ERROR.getCode()
                    , BizResultEnum.SCRIPT_RETURN_ERROR.getMessage(), bizMessage);
            return bizMessage;
        } else if (tmContext.getServiceStatus() == TmContext.SERVICE_STATUS_SUCCESS) {
            this.finishTransaction(bizMessage);
            return bizMessage;
        }

        BizMessage childBizMessage = BizMessage.createChildTransaction(bizMessage);
        childBizMessage.setData(bizMessage.getData());

        Map map = new HashMap();
        map.put("serviceId",serviceId);
        map.put("bizmessage",childBizMessage);
        map.put("retryCount",tmContext.getRetryCount());
        rabbitTemplate.convertAndSend(RabbitmqConfig.TM_DELAY_EXCHANGE, RabbitmqConfig.TM_DELAY_ROUTING_KEY, map,
                new MessagePostProcessor() {
                    @Override
                    public Message postProcessMessage(Message message) throws AmqpException {
                        //设置消息持久化
                        message.getMessageProperties().setDeliveryMode(MessageDeliveryMode.PERSISTENT);
                        //message.getMessageProperties().setHeader("x-delay", "6000");
                        message.getMessageProperties().setDelay(tmContext.getDelayTime());
                        return message;
                    }
                });
        return childBizMessage;
    }

    public void abortTransaction(int code,String message,BizMessage bizMessage) {
        log.error("交易错误:{}-{}\n{}",code,message,bizMessage);
        // TODO 记录和更新事务日志
    }

    public void finishTransaction(BizMessage bizMessage) {
        log.info("交易结束:{}",bizMessage);
        // TODO 记录和更新事务日志
    }
}
