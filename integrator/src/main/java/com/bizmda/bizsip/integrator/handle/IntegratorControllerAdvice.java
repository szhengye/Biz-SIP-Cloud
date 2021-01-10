package com.bizmda.bizsip.integrator.handle;

import cn.hutool.json.JSONObject;
import com.bizmda.bizsip.common.BizException;
import com.bizmda.bizsip.common.BizMessage;
import com.bizmda.bizsip.common.BizUtils;
import org.springframework.web.bind.annotation.*;


/**
 * @author 史正烨
 */
@RestControllerAdvice
@ResponseBody
public class IntegratorControllerAdvice {
    @ExceptionHandler({ BizException.class })
    public BizMessage<JSONObject> bizException(BizException exception) {
        BizMessage<JSONObject> bizMessage = BizUtils.bizMessageThreadLocal.get();
        BizMessage<JSONObject> outMessage = BizMessage.buildFailMessage(bizMessage,exception);
        BizUtils.bizMessageThreadLocal.remove();
        return outMessage;
    }

}
