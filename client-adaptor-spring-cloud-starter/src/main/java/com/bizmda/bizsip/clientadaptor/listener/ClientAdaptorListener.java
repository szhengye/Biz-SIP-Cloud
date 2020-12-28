package com.bizmda.bizsip.clientadaptor.listener;

import com.alibaba.nacos.api.config.listener.AbstractListener;
import com.bizmda.bizsip.clientadaptor.ClientAdaptor;

/**
 * @author 史正烨
 */
public class ClientAdaptorListener extends AbstractListener {
    protected ClientAdaptor clientAdaptor;

    public ClientAdaptorListener(ClientAdaptor clientAdaptor) {
        this.clientAdaptor = clientAdaptor;
    }

    @Override
    public void receiveConfigInfo(String s) {
    }
}
