package com.bizmda.bizsip.integrator.controller;

import cn.hutool.core.text.StrFormatter;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import com.bizmda.bizsip.common.*;
import com.bizmda.bizsip.integrator.checkrule.*;
import com.bizmda.bizsip.integrator.config.IntegratorServiceMapping;
import com.bizmda.bizsip.integrator.executor.AbstractIntegratorExecutor;
import com.bizmda.bizsip.integrator.executor.SipServiceLogService;
import com.bizmda.log.trace.MDCTraceUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Map;

/**
 * @author 史正烨
 */
@Slf4j
@RestController
//@RequestMapping("/")
public class IntegratorController {
    @Autowired
    private IntegratorServiceMapping integratorServiceMapping;
    @Autowired
    private CheckRuleConfigMapping checkRuleConfigMapping;
    @Autowired
    private SipServiceLogService sipServiceLogService;

    @PostMapping(value="/api",consumes = "application/json", produces = "application/json")
    public BizMessage<JSONObject> doApiService(HttpServletRequest request, HttpServletResponse response,
                                   @RequestBody JSONObject inJsonObject,
                                   @PathVariable(required = false) Map<String, Object> pathVariables,
                                   @RequestParam(required = false) Map<String, Object> parameters) throws BizException {
        String serviceId = request.getHeader("Biz-Service-Id");
        log.info("Biz-Service-id:{},{},{}",serviceId,request.getHeaderNames(),request.getHeader(MDCTraceUtils.TRACE_ID_HEADER));
        BizMessage<JSONObject> inMessage = BizMessage.createNewTransaction();
        inMessage.setData(inJsonObject);
        BizUtils.bizMessageThreadLocal.set(inMessage);

        JSONArray jsonArray = this.checkFieldRule(serviceId,inJsonObject);
        if (!jsonArray.isEmpty()) {
            throw new BizException(BizResultEnum.FIELD_CHECK_ERROR,jsonArray.toString());
        }

        jsonArray = this.checkServiceRule(serviceId,inJsonObject);
        if (!jsonArray.isEmpty()) {
            throw new BizException(BizResultEnum.SERVICE_CHECK_ERROR,jsonArray.toString());
        }

        AbstractIntegratorExecutor integratorService = this.integratorServiceMapping.getIntegratorService(serviceId);
        if (integratorService == null) {
            throw new BizException(BizResultEnum.INTEGRATOR_SERVICE_NOT_FOUND,
                    StrFormatter.format("聚合服务不存在:{}",serviceId));
        }

        BizUtils.tmContextThreadLocal.set(new TmContext());
        BizMessage<JSONObject> outMessage = null;
        try {
            outMessage = integratorService.doBizService(inMessage);
        }
        catch (Exception e) {
            outMessage = BizMessage.buildFailMessage(inMessage,e);
        }
        finally {
            BizUtils.tmContextThreadLocal.remove();
            BizUtils.bizMessageThreadLocal.remove();
        }
        if (outMessage.getCode() == 0) {
            this.sipServiceLogService.saveSuccessServiceLog(inMessage,outMessage);
        }
        else {
            this.sipServiceLogService.saveErrorServiceLog(inMessage,outMessage);
        }
        return outMessage;
    }

    private JSONArray checkFieldRule(String serviceId, JSONObject message) throws BizException {
        JSONArray jsonArray = new JSONArray();
        CheckRuleConfig checkRuleConfig = this.checkRuleConfigMapping.getCheckRuleConfig(serviceId);
        if (checkRuleConfig == null) {
            return jsonArray;
        }
        List<FieldCheckRule> fieldCheckRuleList = checkRuleConfig.getFieldCheckRuleList();
        if (fieldCheckRuleList == null) {
            return jsonArray;
        }
        List<FieldChcekRuleResult> fieldChcekRuleResultList = FieldCheckRuleHelper.checkFieldRule(message, fieldCheckRuleList,checkRuleConfig.getFieldCheckMode());

        for(FieldChcekRuleResult fieldChcekRuleResult:fieldChcekRuleResultList) {
            JSONObject jsonObject = new JSONObject();
            jsonObject.set("field", fieldChcekRuleResult.getField());
            jsonObject.set("message", fieldChcekRuleResult.getMessage());
            jsonArray.add(jsonObject);
        }
        return jsonArray;
    }

    private JSONArray checkServiceRule(String serviceId, JSONObject message) throws BizException {
        JSONArray jsonArray = new JSONArray();
        CheckRuleConfig checkRuleConfig = this.checkRuleConfigMapping.getCheckRuleConfig(serviceId);
        if (checkRuleConfig == null) {
            return jsonArray;
        }
        List<ServiceCheckRule> serviceCheckRuleList = checkRuleConfig.getServiceCheckRuleList();
        if (serviceCheckRuleList == null) {
            return jsonArray;
        }
        List<ServiceChcekRuleResult> serviceChcekRuleResultList = ServiceCheckRuleHelper.checkServiceRule(message, serviceCheckRuleList,checkRuleConfig.getFieldCheckMode());

        for(ServiceChcekRuleResult serviceChcekRuleResult:serviceChcekRuleResultList) {
            JSONObject jsonObject = new JSONObject();
            jsonObject.set("message", serviceChcekRuleResult.getResult());
            jsonArray.add(jsonObject);
        }
        return jsonArray;
    }

}
