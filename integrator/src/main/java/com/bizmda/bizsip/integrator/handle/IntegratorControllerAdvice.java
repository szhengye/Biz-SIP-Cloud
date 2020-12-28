package com.bizmda.bizsip.integrator.handle;

import com.bizmda.bizsip.common.BizException;
import com.bizmda.bizsip.common.BizMessage;
import com.bizmda.bizsip.common.BizUtils;
import com.bizmda.bizsip.integrator.controller.IntegratorController;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;


/**
 * @author 史正烨
 */
@RestControllerAdvice
@ResponseBody
public class IntegratorControllerAdvice {
    @ExceptionHandler({ BizException.class })
    public BizMessage bizException(BizException exception) {
        BizMessage bizMessage = IntegratorController.currentBizMessage.get();
        bizMessage.fail(exception);
        IntegratorController.currentBizMessage.remove();
        return bizMessage;
    }

}
