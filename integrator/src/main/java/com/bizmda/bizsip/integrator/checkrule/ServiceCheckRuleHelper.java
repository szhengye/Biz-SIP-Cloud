package com.bizmda.bizsip.integrator.checkrule;

import cn.hutool.core.thread.ThreadUtil;
import cn.hutool.json.JSONObject;
import com.bizmda.bizsip.common.BizException;
import com.bizmda.bizsip.common.BizResultEnum;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

@Slf4j
public class ServiceCheckRuleHelper {
    private static ExecutorService serviceCheckExecutorService = ThreadUtil.newExecutor(Runtime.getRuntime().availableProcessors());

    private ServiceCheckRuleHelper() {

    }

    public static List<ServiceChcekRuleResult> checkServiceRule(JSONObject jsonObject, List<ServiceCheckRule> serviceCheckRuleList, CheckMode checkMode) throws BizException {
        CompletionService<ServiceChcekRuleResult> service = new ExecutorCompletionService<>(serviceCheckExecutorService);
        List<Future<ServiceChcekRuleResult>> futureList = new ArrayList<>();
        for (ServiceCheckRule serviceCheckRule : serviceCheckRuleList) {
            Future<ServiceChcekRuleResult> future = service.submit(new ServiceCheckRuleThread(jsonObject, serviceCheckRule));
            futureList.add(future);
        }
        List<ServiceChcekRuleResult> serviceChcekRuleResultList = new ArrayList<>();
        for (int i = 0; i < serviceCheckRuleList.size(); i++) {
            Future<ServiceChcekRuleResult> take = null;
            try {
                take = service.take();
            } catch (InterruptedException e) {
                log.error("服务校验计算被中断:",e);
                Thread.currentThread().interrupt();
                throw new BizException(BizResultEnum.SERVICE_CHECK_THREAD_ERROR, e);
            }
            ServiceChcekRuleResult serviceChcekRuleResult = null;
            try {
                serviceChcekRuleResult = take.get();
            } catch (InterruptedException e) {
                log.error("服务校验计算被中断:",e);
                Thread.currentThread().interrupt();
                throw new BizException(BizResultEnum.SERVICE_CHECK_THREAD_ERROR, e);
            } catch (ExecutionException e) {
                throw new BizException(BizResultEnum.SERVICE_CHECK_THREAD_ERROR, e);
            }
            if (serviceChcekRuleResult.getResult() != null) {
                if (checkMode == CheckMode.ONE) {
                    for (Future<ServiceChcekRuleResult> future : futureList) {
                        future.cancel(true);
                    }
                    serviceChcekRuleResultList.add(serviceChcekRuleResult);
                    return serviceChcekRuleResultList;
                }
                else {
                    serviceChcekRuleResultList.add(serviceChcekRuleResult);
                }
            }
        }
        return serviceChcekRuleResultList;
    }
}
