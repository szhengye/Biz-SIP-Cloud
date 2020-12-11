package com.bizmda.bizsip.serveradaptor;

import com.bizmda.bizsip.common.BizException;
import com.bizmda.bizsip.common.BizResultEnum;
import com.bizmda.bizsip.config.AbstractServerAdaptorConfig;
import com.bizmda.bizsip.config.ServerAdaptorMapping;
import com.bizmda.bizsip.message.AbstractMessageProcessor;
import com.bizmda.bizsip.message.SimpleJsonMessageProcessor;
import com.bizmda.bizsip.message.SimpleXmlMessageProcessor;
import com.bizmda.bizsip.serveradaptor.protocol.AbstractServerProtocol;
import com.bizmda.bizsip.serveradaptor.protocol.JavaServerProtocol;
import org.springframework.beans.factory.annotation.Autowired;

public class ServerAdaptorProcessor {
    @Autowired
    ServerAdaptorMapping serverAdaptorMapping;

    AbstractMessageProcessor messageProcessor;
    AbstractServerProtocol protocolProcessor;

    public void init(String serverId) throws BizException {
        AbstractServerAdaptorConfig serverAdaptor = serverAdaptorMapping.getServerAdaptor(serverId);
        String messageType = serverAdaptor.getMessageType();
        if (messageType.equalsIgnoreCase("simple_json")) {
            this.messageProcessor = new SimpleJsonMessageProcessor(serverAdaptor);
        }
        else if (messageType.equalsIgnoreCase("simple_xml")) {
            this.messageProcessor = new SimpleXmlMessageProcessor(serverAdaptor);
        }
        else {
            throw new BizException(BizResultEnum.SERVER_ADAPTOR_NO_MESSAGE_PROCESSOR_ERROR);
        }
        String protocolType = serverAdaptor.getProtocol().getType();
        if (protocolType.equalsIgnoreCase("java")) {
            this.protocolProcessor = new JavaServerProtocol(serverAdaptor);
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

}
