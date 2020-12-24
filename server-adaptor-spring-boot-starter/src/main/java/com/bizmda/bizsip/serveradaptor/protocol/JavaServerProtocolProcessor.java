package com.bizmda.bizsip.serveradaptor.protocol;

import com.bizmda.bizsip.common.BizException;
import com.bizmda.bizsip.common.BizResultEnum;
import com.bizmda.bizsip.config.AbstractServerAdaptorConfig;
import com.bizmda.bizsip.serveradaptor.protocol.java.JavaProtocolInterface;

/**
 * @author shizhengye
 */
public class JavaServerProtocolProcessor extends AbstractServerProtocolProcessor {
    private JavaProtocolInterface javaProtocol;

    @Override
    public void init(AbstractServerAdaptorConfig serverAdaptorConfig) throws BizException {
        super.init(serverAdaptorConfig);
//        JavaServerAdaptorProtocolConfig serverAdaptorProtocol = (JavaServerAdaptorProtocolConfig)serverAdaptorConfig.getProtocol();
        String clazzName = (String)serverAdaptorConfig.getProtocolMap().get("class-name");
        try {
            javaProtocol = (JavaProtocolInterface)Class.forName(clazzName).newInstance();
        } catch (InstantiationException e) {
            throw new BizException(BizResultEnum.SERVER_PROTOCOL_JAVA_CREATE_ERROR,e);
        } catch (IllegalAccessException e) {
            throw new BizException(BizResultEnum.SERVER_PROTOCOL_JAVA_CREATE_ERROR,e);
        } catch (ClassNotFoundException e) {
            throw new BizException(BizResultEnum.SERVER_PROTOCOL_JAVA_CREATE_ERROR,e);
        }
    }

    @Override
    public Object process(Object inMessage) throws BizException {
        return javaProtocol.process(inMessage);
    }
}
