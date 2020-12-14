package com.bizmda.bizsip.serveradaptor;

import cn.hutool.extra.ssh.JschRuntimeException;
import cn.hutool.json.JSONObject;
import com.bizmda.bizsip.common.BizException;
import com.bizmda.bizsip.common.BizMessage;
import com.bizmda.bizsip.common.BizResultEnum;
import com.bizmda.bizsip.config.AbstractServerAdaptorConfig;
import com.bizmda.bizsip.config.BizSipConfig;
import com.bizmda.bizsip.config.ServerAdaptorConfigMapping;
import com.bizmda.bizsip.message.AbstractMessageProcessor;
import com.bizmda.bizsip.serveradaptor.config.ServerAdaptorConfiguration;
import com.bizmda.bizsip.serveradaptor.protocol.AbstractServerProtocolProcessor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@Scope("prototype")
public class ServerAdaptor {
    @Autowired
    private ServerAdaptorConfigMapping serverAdaptorConfigMapping;

    private AbstractMessageProcessor messageProcessor;
    private AbstractServerProtocolProcessor protocolProcessor;

    public void init(String adaptorId) throws BizException {
        AbstractServerAdaptorConfig serverAdaptorConfig = this.serverAdaptorConfigMapping.getServerAdaptorConfig(adaptorId);
        String messageType = (String) serverAdaptorConfig.getMessageMap().get("type");
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

        this.messageProcessor.init(serverAdaptorConfig.getMessageMap());

        String protocolType = serverAdaptorConfig.getProtocol().getType();

        clazzName = BizSipConfig.protocolTypeMap.get(protocolType);
        if (clazzName == null) {
            throw new BizException(BizResultEnum.SERVER_NO_PROTOCOL_PROCESSOR_ERROR);
        }

        try {
            this.protocolProcessor = (AbstractServerProtocolProcessor)Class.forName(clazzName).newInstance();
        } catch (InstantiationException e) {
            throw new BizException(BizResultEnum.SERVER_PROTOCOL_CREATE_ERROR,e);
        } catch (IllegalAccessException e) {
            throw new BizException(BizResultEnum.SERVER_PROTOCOL_CREATE_ERROR,e);
        } catch (ClassNotFoundException e) {
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
