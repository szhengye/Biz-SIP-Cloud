package com.bizmda.bizsip.clientadaptor.config;

import cn.hutool.json.JSONObject;
import com.bizmda.bizsip.common.BizException;
import com.bizmda.bizsip.common.BizMessage;
import com.bizmda.bizsip.common.BizResultEnum;
import com.bizmda.bizsip.common.BizUtils;
import com.bizmda.bizsip.config.*;
import com.bizmda.bizsip.message.AbstractMessageProcessor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;

/**
 * @author shizhengye
 */
@Slf4j
@Service
@Scope("prototype")
public class ClientAdaptor {
    @Value("${bizsip.config-path}")
    private String configPath;

    @Value("${bizsip.integrator-url}")
    private String integratorUrl;

    @Autowired
    private ClientAdaptorConfigMapping clientAdaptorConfigMapping;

    private AbstractMessageProcessor messageProcessor;
    private List<PredicateRuleConfig> serviceRules;

    public void init(String adaptorId) throws BizException {
        CommonClientAdaptorConfig clientAdaptorConfig = this.clientAdaptorConfigMapping.getClientAdaptorConfig(adaptorId);
        String messageType = (String)clientAdaptorConfig.getMessageMap().get("type");
        Class clazz = AbstractMessageProcessor.MESSAGE_TYPE_MAP.get(messageType);
        if (clazz == null) {
            throw new BizException(BizResultEnum.NO_MESSAGE_PROCESSOR);
        }

        try {
            this.messageProcessor = (AbstractMessageProcessor)clazz.newInstance();
        } catch (InstantiationException e) {
            throw new BizException(BizResultEnum.MESSAGE_CREATE_ERROR,e);
        } catch (IllegalAccessException e) {
            throw new BizException(BizResultEnum.MESSAGE_CREATE_ERROR,e);
        }

        this.messageProcessor.init(this.configPath, clientAdaptorConfig.getMessageMap());

        this.serviceRules = clientAdaptorConfig.getServiceRules();
    }

    public Object process(Object inMessage) throws BizException {
        log.debug("Client-Adaptor传入消息:{}",inMessage);
        JSONObject message = this.messageProcessor.unpack(inMessage);
        log.debug("解包后消息:{}",message);
        message = this.doBizService(message);
        Object outMessage = this.messageProcessor.pack(message);
        log.debug("打包后消息:{}",outMessage);
        return outMessage;
    }

    private JSONObject doBizService(JSONObject inData) throws BizException {
        RestTemplate restTemplate = new RestTemplate();
        BizMessage inMessage = BizMessage.createNewTransaction();
        inMessage.setData(inData);
        String rule = this.matchServicePredicateRule((JSONObject)inData);
        BizMessage outMessage = (BizMessage)restTemplate.postForObject(this.integratorUrl + rule, inMessage, BizMessage.class);
        if (outMessage.getCode() == 0) {
            log.debug("Integrator返回成功:{}",outMessage.getData());
        }
        else {
            log.debug("Integrator返回错误:{}-{}",outMessage.getCode(),outMessage.getMessage());
            throw new BizException(outMessage.getCode(),outMessage.getMessage());
        }
        return outMessage.getData();
    }

    private String matchServicePredicateRule(JSONObject inData) throws BizException {
        for (PredicateRuleConfig predicateRuleConfig:this.serviceRules) {
            if (predicateRuleConfig.getPredicate() == null ||
                    predicateRuleConfig.getPredicate().isEmpty()) {
                return BizUtils.getElStringResult(predicateRuleConfig.getRule(),inData);
            }
            Boolean predicateFlag = BizUtils.getElBooleanResult(predicateRuleConfig.getPredicate(),inData);
            if (predicateFlag) {
                return BizUtils.getElStringResult(predicateRuleConfig.getRule(),inData);
            }
        }
        throw new BizException(BizResultEnum.NO_MATCH_SERVICE_RULE);
    }
}
