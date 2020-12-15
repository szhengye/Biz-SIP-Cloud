package com.bizmda.bizsip.serveradaptor;

import cn.hutool.json.JSONObject;
import com.bizmda.bizsip.common.BizException;
import com.bizmda.bizsip.common.BizResultEnum;
import com.bizmda.bizsip.config.AbstractServerAdaptorConfig;
import com.bizmda.bizsip.config.ServerAdaptorConfigMapping;
import com.bizmda.bizsip.message.AbstractMessageProcessor;
import com.bizmda.bizsip.serveradaptor.protocol.AbstractServerProtocolProcessor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

/**
 * @author shizhengye
 */
@Slf4j
@Service
@Scope("prototype")
public class ServerAdaptor {
    @Value("${bizsip.config-path}")
    private String configPath;

    @Autowired
    private ServerAdaptorConfigMapping serverAdaptorConfigMapping;

    private AbstractMessageProcessor messageProcessor;
    private AbstractServerProtocolProcessor protocolProcessor;

    public void init(String adaptorId) throws BizException {
        AbstractServerAdaptorConfig serverAdaptorConfig = this.serverAdaptorConfigMapping.getServerAdaptorConfig(adaptorId);
        String messageType = (String) serverAdaptorConfig.getMessageMap().get("type");
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

        this.messageProcessor.init(configPath,serverAdaptorConfig.getMessageMap());

        String protocolType = serverAdaptorConfig.getProtocol().getType();

        clazz = AbstractServerProtocolProcessor.protocolTypeMap.get(protocolType);
        if (clazz == null) {
            throw new BizException(BizResultEnum.SERVER_NO_PROTOCOL_PROCESSOR);
        }

        try {
            this.protocolProcessor = (AbstractServerProtocolProcessor)clazz.newInstance();
        } catch (InstantiationException e) {
            throw new BizException(BizResultEnum.SERVER_PROTOCOL_CREATE_ERROR,e);
        } catch (IllegalAccessException e) {
            throw new BizException(BizResultEnum.SERVER_PROTOCOL_CREATE_ERROR,e);
        }

        this.protocolProcessor.init(serverAdaptorConfig);
    }

    public JSONObject process(JSONObject inMessage) throws BizException {
        log.debug("服务端处理器传入消息:{}",inMessage);
        Object message = this.messageProcessor.pack(inMessage);
        log.debug("打包后消息:{}",message);
        message = this.protocolProcessor.process(message);
        log.debug("应用返回消息:{}",message);
        JSONObject jsonObject = this.messageProcessor.unpack(message);
        log.debug("解包后消息:{}",jsonObject);
        return jsonObject;
    }

}
