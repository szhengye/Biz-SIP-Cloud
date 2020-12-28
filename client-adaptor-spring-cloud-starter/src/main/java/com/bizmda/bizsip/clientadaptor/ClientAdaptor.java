package com.bizmda.bizsip.clientadaptor;

import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.alibaba.nacos.api.NacosFactory;
import com.alibaba.nacos.api.config.ConfigService;
import com.alibaba.nacos.api.exception.NacosException;
import com.bizmda.bizsip.clientadaptor.listener.ClientAdaptorListener;
import com.bizmda.bizsip.common.*;
import com.bizmda.bizsip.config.*;
import com.bizmda.bizsip.message.AbstractMessageProcessor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Scope;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.util.RouteMatcher;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Properties;

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
    @Value("${spring.cloud.nacos.discovery.server-addr}")
    private String serverAddr;

    @Autowired
    private ClientAdaptorConfigMapping clientAdaptorConfigMapping;

    @Autowired
    private RestTemplate restTemplate;

    private String clientAdaptorId;
    private ConfigService configService;

    private AbstractMessageProcessor messageProcessor;
    private List<PredicateRuleConfig> serviceRules;

    /**
     * 客户端适配器初始化，主要包括：
     * 1.初始化消息处理器，装载消息转换配置
     * 2.装载聚合服务定位断言规则
     * @param clientAdaptorId 客户端适配器Id，在client-adaptor.yml文件中定义
     * @throws BizException
     */
    public void init(String clientAdaptorId) throws BizException {
        this.clientAdaptorId = clientAdaptorId;
        this.load();
        Properties properties = new Properties();
        properties.put("serverAddr", this.serverAddr);
        try {
            this.configService = NacosFactory.createConfigService(properties);
//        this.configService.addListener(NacosConstants.REFRESH_SERVER_ADAPTOR_DATA_ID, NacosConstants.NACOS_GROUP, new ClientAdaptorListener(this) {
//            @Override
//            public void receiveConfigInfo(String config) {
//                log.info("刷新服务端适配器配置");
//            }
//        });
            this.configService.addListener(NacosConstants.REFRESH_CLIENT_ADAPTOR_DATA_ID, NacosConstants.NACOS_GROUP, new ClientAdaptorListener(this) {
                @Override
                public void receiveConfigInfo(String config) {
                    log.info("刷新客户端适配器[{}]配置",this.clientAdaptor.clientAdaptorId);
                    this.clientAdaptor.clientAdaptorConfigMapping = new ClientAdaptorConfigMapping(this.clientAdaptor.configPath);
                    try {
                        this.clientAdaptor.load();
                    } catch (BizException e) {
                        e.printStackTrace();
                    }
                }
            });
        } catch (NacosException e) {
            throw new BizException(BizResultEnum.NACOS_ERROR,e);
        }
//        this.configService.addListener(NacosConstants.REFRESH_SERVICE_DATA_ID, NacosConstants.NACOS_GROUP, new ClientAdaptorListener(this) {
//            @Override
//            public void receiveConfigInfo(String config) {
//                log.info("刷新服务配置");
//            }
//        });
//        this.configService.addListener(NacosConstants.REFRESH_MESSAGE_DATA_ID, NacosConstants.NACOS_GROUP, new ClientAdaptorListener(this) {
//            @Override
//            public void receiveConfigInfo(String config) {
//                log.info("刷新消息配置");
//            }
//        });
        log.info("配置刷新监控初始化成功!");

    }

    public void load() throws BizException {
        CommonClientAdaptorConfig clientAdaptorConfig = this.clientAdaptorConfigMapping.getClientAdaptorConfig(this.clientAdaptorId);
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

    /**
     * 客户端适配器在收到消息后，执行的处理动作，依次为：
     * 1.消息解包
     * 2.根据解包消息执行聚合服务定位断言规则，定位聚合服务id
     * 3.发送消息给聚合整合器处理
     * 4.消息打包
     * @param inMessage
     * @return
     * @throws BizException
     */
    public BizMessage process(Object inMessage) throws BizException {
        log.debug("Client-Adaptor传入消息:{}",inMessage);
        JSONObject message = this.messageProcessor.unpack(inMessage);
        log.debug("解包后消息:{}",message);
        BizMessage<Object> bizMessage = this.doBizService(message);
        if (!(bizMessage.getData() instanceof JSONObject)) {
            JSONObject jsonObject = JSONUtil.parseObj(bizMessage.getData());
            bizMessage.setData(jsonObject);
        }
        Object outMessage = this.messageProcessor.pack((JSONObject)bizMessage.getData());
        bizMessage.setData(outMessage);
        log.debug("打包后消息:{}",bizMessage);
        return bizMessage;
    }

    private BizMessage doBizService(JSONObject inData) throws BizException {
//        RestTemplate restTemplate = new RestTemplate();
        String rule = this.matchServicePredicateRule((JSONObject)inData);
        if (this.integratorUrl.endsWith("/")) {
            this.integratorUrl = this.integratorUrl.substring(0,integratorUrl.length()-1);
        }
        HttpHeaders header = new HttpHeaders();
        header.add("Biz-Service-Id",rule);
        HttpEntity<JSONObject> httpEntity = new HttpEntity<>(inData, header);

        BizMessage outMessage = (BizMessage)this.restTemplate.postForObject(this.integratorUrl, httpEntity, BizMessage.class);
        if (outMessage.getCode() == 0) {
            log.debug("Integrator返回成功:{}",outMessage.getData());
        }
        else {
            log.debug("Integrator返回错误:{}-{}",outMessage.getCode(),outMessage.getMessage());
            throw new BizException(outMessage.getCode(),outMessage.getMessage());
        }
        return outMessage;
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
