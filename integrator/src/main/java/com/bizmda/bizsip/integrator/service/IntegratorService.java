package com.bizmda.bizsip.integrator.service;

import com.bizmda.bizsip.common.BizMessage;

/**
 * @author shizhengye
 */
public interface IntegratorService {
    public BizMessage doBizService(String serviceId, BizMessage message);
}
