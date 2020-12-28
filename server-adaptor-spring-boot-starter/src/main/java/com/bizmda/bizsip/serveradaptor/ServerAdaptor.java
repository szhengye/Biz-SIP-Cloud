package com.bizmda.bizsip.serveradaptor;

import cn.hutool.extra.spring.SpringUtil;
import cn.hutool.json.JSONObject;
import com.alibaba.nacos.api.NacosFactory;
import com.alibaba.nacos.api.config.ConfigService;
import com.alibaba.nacos.api.config.listener.AbstractListener;
import com.alibaba.nacos.api.exception.NacosException;
import com.bizmda.bizsip.common.BizException;
import com.bizmda.bizsip.common.BizResultEnum;
import com.bizmda.bizsip.common.NacosConstants;
import com.bizmda.bizsip.config.AbstractServerAdaptorConfig;
import com.bizmda.bizsip.config.ServerAdaptorConfigMapping;
import com.bizmda.bizsip.message.AbstractMessageProcessor;
import com.bizmda.bizsip.serveradaptor.listener.ServerAdaptorListener;
import com.bizmda.bizsip.serveradaptor.protocol.AbstractServerProtocolProcessor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.stereotype.Service;

import java.util.Properties;

/**
 * @author 史正烨
 */
@Slf4j
@Service
@Scope(value = "prototype", proxyMode = ScopedProxyMode.TARGET_CLASS)
public class ServerAdaptor {
    @Value("${bizsip.config-path}")
    private String configPath;
    @Value("${spring.cloud.nacos.discovery.server-addr}")
    private String serverAddr;

    private String serverAdaptorId;
    private ConfigService configService;

    @Autowired
    private ServerAdaptorConfigMapping serverAdaptorConfigMapping;

    private AbstractMessageProcessor messageProcessor;
    private AbstractServerProtocolProcessor protocolProcessor;

    public void init(String adaptorId) throws BizException {
        this.serverAdaptorId = adaptorId;
        this.load();
        Properties properties = new Properties();
        properties.put("serverAddr", this.serverAddr);
        try {
            this.configService = NacosFactory.createConfigService(properties);
            this.configService.addListener(NacosConstants.REFRESH_SERVER_ADAPTOR_DATA_ID, NacosConstants.NACOS_GROUP, new ServerAdaptorListener(this) {
                @Override
                public void receiveConfigInfo(String config) {
                    log.info("刷新服务端适配器[{}]配置",this.serverAdaptor.serverAdaptorId);
                    try {
                        this.serverAdaptor.serverAdaptorConfigMapping = new ServerAdaptorConfigMapping(this.serverAdaptor.configPath);
                        this.serverAdaptor.load();
                    } catch (BizException e) {
                        e.printStackTrace();
                    }
                }
            });
        } catch (NacosException e) {
            throw new BizException(BizResultEnum.NACOS_ERROR);
        }
//        this.configService.addListener(NacosConstants.REFRESH_CLIENT_ADAPTOR_DATA_ID, NacosConstants.NACOS_GROUP, new ServerAdaptorListener(this) {
//            @Override
//            public void receiveConfigInfo(String config) {
//                log.info("刷新客户端适配器配置");
//            }
//        });
//        this.configService.addListener(NacosConstants.REFRESH_SERVICE_DATA_ID, NacosConstants.NACOS_GROUP, new ServerAdaptorListener(this) {
//            @Override
//            public void receiveConfigInfo(String config) {
//                log.info("刷新服务配置");
//            }
//        });
//        this.configService.addListener(NacosConstants.REFRESH_MESSAGE_DATA_ID, NacosConstants.NACOS_GROUP, new ServerAdaptorListener(this) {
//            @Override
//            public void receiveConfigInfo(String config) {
//                log.info("刷新消息配置");
//            }
//        });
        log.info("配置刷新监控初始化成功!");
    }

    public void load() throws BizException {
        AbstractServerAdaptorConfig serverAdaptorConfig = this.serverAdaptorConfigMapping.getServerAdaptorConfig(this.serverAdaptorId);
        String messageType = (String) serverAdaptorConfig.getMessageMap().get("type");
        Class clazz = AbstractMessageProcessor.MESSAGE_TYPE_MAP.get(messageType);
        if (clazz == null) {
            throw new BizException(BizResultEnum.NO_MESSAGE_PROCESSOR);
        }

        try {
            this.messageProcessor = (AbstractMessageProcessor) clazz.newInstance();
        } catch (InstantiationException e) {
            throw new BizException(BizResultEnum.MESSAGE_CREATE_ERROR, e);
        } catch (IllegalAccessException e) {
            throw new BizException(BizResultEnum.MESSAGE_CREATE_ERROR, e);
        }

        this.messageProcessor.init(configPath, serverAdaptorConfig.getMessageMap());

        String protocolType = (String) serverAdaptorConfig.getProtocolMap().get("type");

        clazz = AbstractServerProtocolProcessor.PROTOCOL_TYPE_MAP.get(protocolType);
        if (clazz == null) {
            throw new BizException(BizResultEnum.SERVER_NO_PROTOCOL_PROCESSOR);
        }

        try {
            this.protocolProcessor = (AbstractServerProtocolProcessor) clazz.newInstance();
        } catch (InstantiationException e) {
            throw new BizException(BizResultEnum.SERVER_PROTOCOL_CREATE_ERROR, e);
        } catch (IllegalAccessException e) {
            throw new BizException(BizResultEnum.SERVER_PROTOCOL_CREATE_ERROR, e);
        }

        this.protocolProcessor.init(serverAdaptorConfig);
    }

    public JSONObject process(JSONObject inMessage) throws BizException {
        log.debug("服务端处理器传入消息:{}", inMessage);
        Object message = this.messageProcessor.pack(inMessage);
        log.debug("打包后消息:{}", message);
        message = this.protocolProcessor.process(message);
        log.debug("应用返回消息:{}", message);
        JSONObject jsonObject = this.messageProcessor.unpack(message);
        log.debug("解包后消息:{}", jsonObject);
        return jsonObject;
    }

}
