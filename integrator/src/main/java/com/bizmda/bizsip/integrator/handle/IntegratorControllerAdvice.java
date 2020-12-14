package com.bizmda.bizsip.integrator.handle;

import com.bizmda.bizsip.common.BizException;
import com.bizmda.bizsip.common.BizMessage;
import com.bizmda.bizsip.common.BizUtils;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;


@RestControllerAdvice
@ResponseBody
public class IntegratorControllerAdvice {
    @ExceptionHandler({ BizException.class })
    public BizMessage bizException(BizException exception) {
        BizMessage bizMessage = BizUtils.currentBizMessage.get();
        bizMessage.fail(exception);
        BizUtils.currentBizMessage.remove();
        return bizMessage;
    }

}
