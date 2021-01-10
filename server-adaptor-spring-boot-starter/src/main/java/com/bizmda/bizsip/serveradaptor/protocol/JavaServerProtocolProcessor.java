package com.bizmda.bizsip.serveradaptor.protocol;

import com.bizmda.bizsip.common.BizException;
import com.bizmda.bizsip.common.BizResultEnum;
import com.bizmda.bizsip.config.AbstractServerAdaptorConfig;
import com.bizmda.bizsip.serveradaptor.protocol.java.JavaProtocolInterface;

import java.lang.reflect.InvocationTargetException;

/**
 * @author 史正烨
 */
public class JavaServerProtocolProcessor extends AbstractServerProtocolProcessor {
    private JavaProtocolInterface javaProtocol;

    @Override
    public void init(AbstractServerAdaptorConfig serverAdaptorConfig) throws BizException {
        super.init(serverAdaptorConfig);
        String clazzName = (String)serverAdaptorConfig.getProtocolMap().get("class-name");
        try {
            javaProtocol = (JavaProtocolInterface)Class.forName(clazzName).getDeclaredConstructor().newInstance();
        } catch (InstantiationException | IllegalAccessException | ClassNotFoundException
                | NoSuchMethodException | InvocationTargetException e) {
            throw new BizException(BizResultEnum.SERVER_PROTOCOL_JAVA_CREATE_ERROR,e);
        }
    }

    @Override
    public Object process(Object inMessage) throws BizException {
        return javaProtocol.process(inMessage);
    }
}
