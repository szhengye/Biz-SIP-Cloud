package com.bizmda.bizsip.common;

public class BizConstant {
    public static final String SERVICE_STATUS_SUCCESS = "0";
    public static final String SERVICE_STATUS_ERROR = "1";
    public static final String SERVICE_STATUS_PROCESSING = "2";

    public static final String NACOS_GROUP="DEFAULT_GROUP";
    public static final String REFRESH_SERVER_ADAPTOR_DATA_ID="bizsip.refresh.server-adaptor";
    public static final String REFRESH_CLIENT_ADAPTOR_DATA_ID="bizsip.refresh.client-adaptor";
    public static final String REFRESH_SERVICE_DATA_ID="bizsip.refresh.service";
    public static final String REFRESH_MESSAGE_DATA_ID="bizsip.refresh.message";

    public static final String DEFAULT_CHARSET_NAME="UTF-8";

    /**
     * 日志链路追踪id信息头
     */
    public static final String TRACE_ID_HEADER = "x-traceId-header";
    /**
     * 日志链路追踪id日志标志
     */
    public static final String LOG_TRACE_ID = "traceId";

    private BizConstant() {
    }
}
