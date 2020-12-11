package com.bizmda.bizsip.serveradaptor;

import com.bizmda.bizsip.common.BizException;
import com.bizmda.bizsip.common.BizResultEnum;
import com.bizmda.bizsip.config.AbstractServerAdaptorConfig;
import com.bizmda.bizsip.config.ServerAdaptorConfigMapping;
import com.bizmda.bizsip.message.AbstractMessageProcessor;
import com.bizmda.bizsip.message.SimpleJsonMessageProcessor;
import com.bizmda.bizsip.message.SimpleXmlMessageProcessor;
import com.bizmda.bizsip.serveradaptor.config.ServerAdaptorConfig;
import com.bizmda.bizsip.serveradaptor.protocol.AbstractServerProtocol;
import com.bizmda.bizsip.serveradaptor.protocol.JavaServerProtocol;
import org.springframework.beans.factory.annotation.Autowired;

public class ServerAdaptorProcessor {

    private AbstractMessageProcessor messageProcessor;
    private AbstractServerProtocol protocolProcessor;

    public ServerAdaptorProcessor(AbstractServerAdaptorConfig serverAdaptorConfig) throws BizException {
        String messageType = serverAdaptorConfig.getMessageType();
        if (messageType.equalsIgnoreCase("simple-json")) {
            this.messageProcessor = new SimpleJsonMessageProcessor(serverAdaptorConfig);
        }
        else if (messageType.equalsIgnoreCase("simple-xml")) {
            this.messageProcessor = new SimpleXmlMessageProcessor(serverAdaptorConfig);
        }
        else {
            throw new BizException(BizResultEnum.SERVER_ADAPTOR_NO_MESSAGE_PROCESSOR_ERROR);
        }
        String protocolType = serverAdaptorConfig.getProtocol().getType();
        if (protocolType.equalsIgnoreCase("java")) {
            this.protocolProcessor = new JavaServerProtocol(serverAdaptorConfig);
        }
        else {
            throw new BizException(BizResultEnum.SERVER_ADAPTOR_NO_PROTOCOL_PROCESSOR_ERROR);
        }
    }

    public Object process(Object inMessage) throws BizException {
        Object message = this.messageProcessor.pack(inMessage);
        message = this.protocolProcessor.process(message);
        message = this.messageProcessor.unpack(message);
        return message;
    }

//    public AbstractServerAdaptorConfig getServerAdaptorConfigById(String serverId) {
//        if (this.serverAdaptorConfigMapping == null) {
//            this.serverAdaptorConfigMapping = new ServerAdaptorConfigMapping(this.configPath);
//        }
//        return this.serverAdaptorConfigMapping.getServerAdaptorConfig(serverId);
//    }
}
