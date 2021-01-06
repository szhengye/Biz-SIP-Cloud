package com.bizmda.bizsip.common;

import lombok.Data;

@Data
public class TmContext {
    public static final char SERVICE_STATUS_RETRY = '0';
    public static final char SERVICE_STATUS_SUCCESS = '1';
    public static final char SERVICE_STATUS_ERROR = '2';

    private int delayTime = 10000;
    private int retryCount = 0;
    private char serviceStatus = SERVICE_STATUS_RETRY;
}
