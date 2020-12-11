package com.bizmda.bizsip.serveradaptor.protocol;

import com.bizmda.bizsip.common.BizException;
import com.bizmda.bizsip.common.BizResultEnum;
import com.bizmda.bizsip.config.AbstractServerAdaptorConfig;
import com.bizmda.bizsip.config.JavaServerAdaptorProtocolConfig;

public class JavaServerProtocol extends AbstractServerProtocol {
    private JavaProtocolInterface javaProtocol;
    public JavaServerProtocol(AbstractServerAdaptorConfig serverAdaptor) throws BizException {
        super(serverAdaptor);
        JavaServerAdaptorProtocolConfig serverAdaptorProtocol = (JavaServerAdaptorProtocolConfig)serverAdaptor.getProtocol();
        String clazzName = serverAdaptorProtocol.getClazzName();
        try {
            javaProtocol = (JavaProtocolInterface)Class.forName(clazzName).newInstance();
        } catch (InstantiationException e) {
            throw new BizException(BizResultEnum.SERVER_ADAPTOR_JAVA_ERROR,e);
        } catch (IllegalAccessException e) {
            throw new BizException(BizResultEnum.SERVER_ADAPTOR_JAVA_ERROR,e);
        } catch (ClassNotFoundException e) {
            throw new BizException(BizResultEnum.SERVER_ADAPTOR_JAVA_ERROR,e);
        }
    }

    @Override
    public Object process(Object inMessage) throws BizException {
        return javaProtocol.process(inMessage);
    }
}
