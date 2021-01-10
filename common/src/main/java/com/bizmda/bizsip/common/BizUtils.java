package com.bizmda.bizsip.common;

import cn.hutool.core.text.StrFormatter;
import cn.hutool.core.thread.ThreadUtil;
import cn.hutool.json.JSONObject;
import lombok.extern.slf4j.Slf4j;
import java.util.concurrent.*;

/**
 * @author 史正烨
 */
@Slf4j
public class BizUtils {
    public static final ThreadLocal<BizMessage<JSONObject>> bizMessageThreadLocal = new ThreadLocal<>();
    public static final ThreadLocal<TmContext> tmContextThreadLocal = new ThreadLocal<>();
    private static final ExecutorService elExecutorService = ThreadUtil.newExecutor(Runtime.getRuntime().availableProcessors());

    private BizUtils() {
    }

    public static String getElStringResult(String express, Object data) throws BizException {
        String result = null;
        Future<Object> future = elExecutorService.submit(new ElThread(express,data,false));
        try {
            result = (String)future.get();
        } catch (InterruptedException e) {
            log.error("EL表达式计算被中断:{}",express,e);
            Thread.currentThread().interrupt();
            return null;
        } catch (ExecutionException e) {
            throw new BizException(BizResultEnum.EL_CALCULATE_ERROR,e,
                    StrFormatter.format("EL表达式计算出错:{}",express));
        }
        return result;
    }

    public static Boolean getElBooleanResult(String express, Object data) throws BizException {
        Boolean result = null;
        Future<Object> future = elExecutorService.submit(new ElThread(express,data,true));
        try {
            result = (Boolean)future.get();
        } catch (InterruptedException e) {
            log.error("EL表达式计算被中断:{}",express,e);
            Thread.currentThread().interrupt();
            return null;
        } catch (ExecutionException e) {
            throw new BizException(BizResultEnum.EL_CALCULATE_ERROR,e,
                    StrFormatter.format("EL表达式计算出错:{}",express));
        }
        return result;
    }


}
