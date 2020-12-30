package com.bizmda.bizsip.integrator.controller;

import cn.hutool.core.text.StrFormatter;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import com.bizmda.bizsip.common.BizException;
import com.bizmda.bizsip.common.BizMessage;
import com.bizmda.bizsip.common.BizResultEnum;
import com.bizmda.bizsip.common.fieldrule.FieldRule;
import com.bizmda.bizsip.common.fieldrule.FieldRuleConfig;
import com.bizmda.bizsip.common.fieldrule.FieldRuleConfigMapping;
import com.bizmda.bizsip.common.fieldrule.FieldRuleHelper;
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
@RequestMapping("/")
public class IntegratorController {
    @Autowired
    IntegratorServiceMapping integratorServiceMapping;
    @Autowired
    FieldRuleConfigMapping fieldRuleConfigMapping;

    public static ThreadLocal<BizMessage> currentBizMessage = new ThreadLocal<BizMessage>();

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
        IntegratorController.currentBizMessage.set(inMessage);

        JSONArray jsonArray = this.checkFieldRule(serviceId,inJsonObject);
        if (jsonArray != null) {
            throw new BizException(BizResultEnum.FIELD_VALIDATE_ERROR,jsonArray.toString());
        }


        AbstractIntegratorService integratorService = this.integratorServiceMapping.getIntegratorService(serviceId);
        if (integratorService == null) {
            throw new BizException(BizResultEnum.INTEGRATOR_SERVICE_NOT_FOUND,
                    StrFormatter.format("聚合服务不存在:{}",serviceId));
        }

        BizMessage outMessage = integratorService.doBizService(inMessage);

        IntegratorController.currentBizMessage.remove();
        return outMessage;
    }

    private JSONArray checkFieldRule(String serviceId, JSONObject message) throws BizException {
        List<FieldRuleConfig> fieldRuleConfigList = this.fieldRuleConfigMapping.getFieldValidateConfigList(serviceId);
        if (fieldRuleConfigList == null) {
            return null;
        }
        FieldRule fieldRule = FieldRuleHelper.checkFieldRule(message, fieldRuleConfigList);
        if (fieldRule == null) {
            return null;
        }
        JSONArray jsonArray = new JSONArray();
        JSONObject jsonObject = new JSONObject();
        jsonObject.set("field", fieldRule.getField());
        jsonObject.set("desc", fieldRule.getDesc());
        jsonArray.add(jsonObject);
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
