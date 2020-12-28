package com.bizmda.bizsip.serveradaptor.listener;

import com.alibaba.nacos.api.config.listener.AbstractListener;
import com.bizmda.bizsip.serveradaptor.ServerAdaptor;

/**
 * @author 史正烨
 */
public class ServerAdaptorListener extends AbstractListener {
    protected ServerAdaptor serverAdaptor;

    public ServerAdaptorListener(ServerAdaptor serverAdaptor) {
        this.serverAdaptor = serverAdaptor;
    }

    @Override
    public void receiveConfigInfo(String s) {
    }
}
