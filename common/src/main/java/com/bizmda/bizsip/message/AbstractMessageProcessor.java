package com.bizmda.bizsip.message;

import cn.hutool.json.JSON;
import cn.hutool.json.JSONObject;
import com.bizmda.bizsip.common.BizException;
import com.bizmda.bizsip.config.AbstractServerAdaptorConfig;
import com.bizmda.bizsip.config.PredicateRuleConfig;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author shizhengye
 */
@Slf4j
public abstract class AbstractMessageProcessor<T> {
    protected String type;
    protected String configPath;
    protected List<PredicateRuleConfig> packRules;
    protected List<PredicateRuleConfig> unpackRules;

    public final static Map<String,Class> MESSAGE_TYPE_MAP = new HashMap<String,Class>(){{
        put("simple-json",SimpleJsonMessageProcessor.class);
        put("simple-xml",SimpleXmlMessageProcessor.class);
        put("velocity-json",VelocityJsonMessageProcessor.class);
    }};

    /**
     * 根据传入JSONObject打包断言规则选择规则，根据规则打包成JSONObject对象。
     * @param inMessage
     * @return
     * @throws BizException
     */
    protected abstract JSONObject biz2json(JSONObject inMessage) throws BizException;

    /**
     * 根据适配端口消息类型进行消息打包。
     * @param inMessage
     * @return
     * @throws BizException
     */
    protected abstract T json2adaptor(JSONObject inMessage) throws BizException;

    /**
     * 根据适配端口消息类型进行消息预解包
     * @param inMessage
     * @return
     * @throws BizException
     */
    protected abstract JSONObject adaptor2json(Object inMessage) throws BizException;

    /**
     * 根据传入预解包JSONObject解包断言规则选择规则，根据规则打包成JSONObject对象
     * @param inMessage
     * @return
     * @throws BizException
     */
    protected abstract JSONObject json2biz(JSONObject inMessage) throws BizException;

    public T pack(JSONObject inMessage) throws BizException {
        JSONObject message = this.biz2json(inMessage);
        return this.json2adaptor(message);
    }
    public JSONObject unpack(T inMessage) throws BizException {
        JSONObject message = this.adaptor2json(inMessage);
        return this.json2biz(message);
    }

    public void init(String configPath,Map messageMap) throws BizException {
        this.type = (String)messageMap.get("type");
        this.configPath = configPath;
        List<Map> packRules = (List<Map>)messageMap.get("pack-rules");
        if (packRules == null ) {
            packRules = new ArrayList<Map>();
        }
        this.packRules = new ArrayList<PredicateRuleConfig>();
        for(Map ruleMap:packRules) {
            PredicateRuleConfig predicateRuleConfig = new PredicateRuleConfig(ruleMap);
            this.packRules.add(predicateRuleConfig);
        }
        List<Map> unpackRules = (List<Map>)messageMap.get("unpack-rules");
        if (unpackRules == null) {
            unpackRules = new ArrayList<Map>();
        }
        this.unpackRules = new ArrayList<PredicateRuleConfig>();
        for(Map ruleMap:unpackRules) {
            PredicateRuleConfig predicateRuleConfig = new PredicateRuleConfig(ruleMap);
            this.unpackRules.add(predicateRuleConfig);
        }
    }

    public String matchMessagePredicateRule(List<PredicateRuleConfig> predicateRuleConfigs, JSONObject message) {
        String rule;
        for (PredicateRuleConfig predicateRuleConfig:predicateRuleConfigs) {
            try {
                rule = predicateRuleConfig.getMatchRule(message);
            } catch (BizException e) {
                log.error("匹配规则出错:{}-{}",predicateRuleConfig.getPredicate(),predicateRuleConfig.getRule(),e);
                continue;
            }
            if (rule != null) {
                return rule;
            }
        }
        return null;
    }
}
