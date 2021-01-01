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
public class FieldCheckRuleHelper {
    private static ExecutorService fieldAssertExecutorService = ThreadUtil.newExecutor(Runtime.getRuntime().availableProcessors());

    public static List<FieldChcekRuleResult> checkFieldRule(JSONObject jsonObject, List<FieldCheckRule> fieldCheckRuleList, CheckMode checkMode) throws BizException {
        CompletionService<FieldChcekRuleResult> service = new ExecutorCompletionService<FieldChcekRuleResult>(fieldAssertExecutorService);
        List<Future<FieldChcekRuleResult>> futureList = new ArrayList<Future<FieldChcekRuleResult>>();
        for (FieldCheckRule fieldCheckRule : fieldCheckRuleList) {
            Future<FieldChcekRuleResult> future = service.submit(new FieldCheckRuleThread(jsonObject, fieldCheckRule));
            futureList.add(future);
        }
        List<FieldChcekRuleResult> fieldChcekRuleResultList = new ArrayList<FieldChcekRuleResult>();
        for (int i = 0; i < fieldCheckRuleList.size(); i++) {
            Future<FieldChcekRuleResult> take = null;
            try {
                take = service.take();
            } catch (InterruptedException e) {
                throw new BizException(BizResultEnum.FIELD_CHECK_THREAD_ERROR, e);
            }
            FieldChcekRuleResult fieldChcekRuleResult = null;
            try {
                fieldChcekRuleResult = take.get();
            } catch (InterruptedException | ExecutionException e) {
                throw new BizException(BizResultEnum.FIELD_CHECK_THREAD_ERROR, e);
            }
            if (fieldChcekRuleResult != null) {
                if (checkMode == CheckMode.ONE) {
                    for (Future future : futureList) {
                        future.cancel(true);
                    }
                    fieldChcekRuleResultList.add(fieldChcekRuleResult);
                    return fieldChcekRuleResultList;
                }
                else {
                    fieldChcekRuleResultList.add(fieldChcekRuleResult);
                }
            }
        }
        return fieldChcekRuleResultList;
    }
}
