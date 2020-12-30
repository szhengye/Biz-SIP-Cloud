package com.bizmda.bizsip.common.fieldrule;

import cn.hutool.core.thread.ThreadUtil;
import cn.hutool.json.JSONObject;
import com.bizmda.bizsip.common.BizException;
import com.bizmda.bizsip.common.BizResultEnum;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

@Slf4j
public class FieldRuleHelper {
    private static ExecutorService fieldAssertExecutorService = ThreadUtil.newExecutor(Runtime.getRuntime().availableProcessors());

    public static FieldRule checkFieldRule(JSONObject jsonObject, List<FieldRuleConfig> fieldRuleConfigList) throws BizException {
        CompletionService<FieldRule> service = new ExecutorCompletionService<FieldRule>(fieldAssertExecutorService);
        List<Future<FieldRule>> futureList = new ArrayList<Future<FieldRule>>();
        for(FieldRuleConfig fieldRuleConfig : fieldRuleConfigList) {
            Future<FieldRule> future = service.submit(new FieldRuleThread(jsonObject, fieldRuleConfig));
            futureList.add(future);
        }
        for(int i = 0; i< fieldRuleConfigList.size(); i++) {
            Future<FieldRule> take = null;
            try {
                take = service.take();
            } catch (InterruptedException e) {
                throw new BizException(BizResultEnum.FIELD_VALIDATE_THREAD_ERROR,e);
            }
            FieldRule fieldRule = null;
            try {
                fieldRule = take.get();
            } catch (InterruptedException | ExecutionException e) {
                throw new BizException(BizResultEnum.FIELD_VALIDATE_THREAD_ERROR,e);
            }
            if(fieldRule != null) {
                for(Future future:futureList) {
                    future.cancel(true);
                }
                return fieldRule;
            }
        }
        return null;
    }
}
