package com.bizmda.bizsip.integrator.controller;

import cn.hutool.core.text.StrFormatter;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import com.bizmda.bizsip.common.*;
import com.bizmda.bizsip.integrator.checkrule.*;
import com.bizmda.bizsip.integrator.config.IntegratorServiceMapping;
import com.bizmda.bizsip.integrator.service.AbstractIntegratorService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.ssssssss.magicapi.provider.ResultProvider;
import org.ssssssss.magicapi.provider.impl.DefaultResultProvider;

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
    IntegratorServiceMapping integratorServiceMapping;
    @Autowired
    CheckRuleConfigMapping checkRuleConfigMapping;

    private ResultProvider resultProvider = new DefaultResultProvider();
    private boolean throwException = false;

    @PostMapping(value="/api",consumes = "application/json", produces = "application/json")
    public BizMessage<JSONObject> doApiService(HttpServletRequest request, HttpServletResponse response,
                                   @RequestBody JSONObject inJsonObject,
                                   @PathVariable(required = false) Map<String, Object> pathVariables,
                                   @RequestParam(required = false) Map<String, Object> parameters) throws BizException {

        String serviceId = request.getHeader("Biz-Service-Id");
        if (!serviceId.startsWith("/")) {
            serviceId = "/" + serviceId;
        }

        BizMessage<JSONObject> inMessage = BizMessage.createNewTransaction();
        inMessage.setData(inJsonObject);
        BizUtils.bizMessageThreadLocal.set(inMessage);

        JSONArray jsonArray = this.checkFieldRule(serviceId,inJsonObject);
        if (jsonArray.size() > 0) {
            throw new BizException(BizResultEnum.FIELD_CHECK_ERROR,jsonArray.toString());
        }

        jsonArray = this.checkServiceRule(serviceId,inJsonObject);
        if (jsonArray.size() > 0) {
            throw new BizException(BizResultEnum.SERVICE_CHECK_ERROR,jsonArray.toString());
        }

        AbstractIntegratorService integratorService = this.integratorServiceMapping.getIntegratorService(serviceId);
        if (integratorService == null) {
            throw new BizException(BizResultEnum.INTEGRATOR_SERVICE_NOT_FOUND,
                    StrFormatter.format("聚合服务不存在:{}",serviceId));
        }

        BizUtils.tmContextThreadLocal.set(new TmContext());
        BizMessage outMessage;
        try {
            outMessage = integratorService.doBizService(inMessage);
        }
        finally {
            BizUtils.tmContextThreadLocal.remove();
            BizUtils.bizMessageThreadLocal.remove();
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
//    private void setupMagicModules() {
//        // 设置脚本import时 class加载策略
//        MagicModuleLoader.setClassLoader((className) -> {
//            try {
//                return springContext.getBean(className);
//            } catch (Exception e) {
//                Class<?> clazz = null;
//                try {
//                    clazz = Class.forName(className);
//                    return springContext.getBean(clazz);
//                } catch (Exception ex) {
//                    return clazz;
//                }
//            }
//        });
//        log.info("注册模块:{} -> {}", "log", Logger.class);
//        MagicModuleLoader.addModule("log", LoggerFactory.getLogger(MagicScript.class));
//        log.info("注册模块:{} -> {}", "request", RequestFunctions.class);
//        MagicModuleLoader.addModule("request", new RequestFunctions());
//        log.info("注册模块:{} -> {}", "assert", AssertFunctions.class);
//        MagicModuleLoader.addModule("assert", AssertFunctions.class);
//        log.info("注册模块:{} -> {}", "server", ServerService.class);
//        MagicModuleLoader.addModule("server", ServerService.class);
//    }
}
