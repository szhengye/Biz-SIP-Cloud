package com.bizmda.bizsip.clientadaptor.config;

import com.bizmda.bizsip.common.BizException;
import com.bizmda.bizsip.common.BizMessage;
import com.bizmda.bizsip.common.BizResultEnum;
import com.bizmda.bizsip.config.*;
import com.bizmda.bizsip.message.AbstractMessageProcessor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Slf4j
@Service
@Scope("prototype")
public class ClientAdaptorProcessor {
    @Autowired
    private ClientAdaptorConfigMapping clientAdaptorConfigMapping;

    private AbstractMessageProcessor messageProcessor;
    private List<PredicateRuleConfig> serviceRules;
    private String bizServiceUrl;

    public void init(String adaptorId) throws BizException {
        CommonClientAdaptorConfig clientAdaptorConfig = this.clientAdaptorConfigMapping.getClientAdaptorConfig(adaptorId);
        String messageType = (String)clientAdaptorConfig.getMessageMap().get("type");
        String clazzName = BizSipConfig.messageTypeMap.get(messageType);
        if (clazzName == null) {
            throw new BizException(BizResultEnum.NO_MESSAGE_PROCESSOR_ERROR);
        }

        try {
            this.messageProcessor = (AbstractMessageProcessor)Class.forName(clazzName).newInstance();
        } catch (InstantiationException e) {
            throw new BizException(BizResultEnum.MESSAGE_CREATE_ERROR,e);
        } catch (IllegalAccessException e) {
            throw new BizException(BizResultEnum.MESSAGE_CREATE_ERROR,e);
        } catch (ClassNotFoundException e) {
            throw new BizException(BizResultEnum.MESSAGE_CREATE_ERROR,e);
        }

        this.messageProcessor.init(clientAdaptorConfig.getMessageMap());

    }

    public Object process(Object inMessage) throws BizException {
        log.debug("客户端处理器传入消息:{}",inMessage);
        Object message = this.messageProcessor.unpack(inMessage);
        log.debug("解包后消息:{}",message);
        message = this.doBizService(message);
        log.debug("整合器返回消息:{}",message);
        message = this.messageProcessor.pack(message);
        log.debug("打包后消息:{}",message);
        return message;
    }

    private Object doBizService(Object inData) throws BizException {
        RestTemplate restTemplate = new RestTemplate();
        BizMessage inMessage = new BizMessage();
        inMessage.setData(inData);
        BizMessage outMessage = (BizMessage)restTemplate.postForObject(this.bizServiceUrl, inData, BizMessage.class);
        if (outMessage.getCode() != 0) {
            throw new BizException(outMessage.getCode(),outMessage.getMessage());
        }
        return outMessage;
    }

}
