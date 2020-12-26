package com.bizmda.bizsip.message;

import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONObject;
import com.bizmda.bizsip.common.BizException;
import com.bizmda.bizsip.common.BizResultEnum;
import com.bizmda.bizsip.message.fieldfunction.FieldFunction;
import com.bizmda.bizsip.message.fixedlength.FixedLengthConfig;
import com.bizmda.bizsip.message.fixedlength.PreUnpackConfig;
import org.yaml.snakeyaml.Yaml;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author shizhengye
 */
public class FixedLengthMessageProcessor<String> extends AbstractMessageProcessor {
    private Map<java.lang.String, List<FixedLengthConfig>> fixedLengthConfigsMap = new HashMap<java.lang.String,List<FixedLengthConfig>>();
    private List<PreUnpackConfig> preUnpackConfigList;

    @Override
    public void init(java.lang.String configPath, Map messageMap) throws BizException {
        super.init(configPath,messageMap);
        List<Map> preUnpackConfigMapList = (List<Map>)messageMap.get("pre-unpack");
        this.preUnpackConfigList = new ArrayList<PreUnpackConfig>();
        for(Map map:preUnpackConfigMapList) {
            PreUnpackConfig preUnpackConfig = new PreUnpackConfig(map);
            this.preUnpackConfigList.add(preUnpackConfig);
        }
    }

    @Override
    protected JSONObject biz2json(JSONObject inMessage) throws BizException {
        return inMessage;
    }

    @Override
    protected Object json2adaptor(JSONObject inMessage) throws BizException {
        java.lang.String configFileName = this.matchMessagePredicateRule(this.packRules,inMessage);
        if (configFileName == null) {
            throw new BizException(BizResultEnum.NO_MESSAGE_MATCH_RULE);
        }

        StringBuffer stringBuffer = new StringBuffer();
        List<FixedLengthConfig> fixedLengthConfigList = this.getFixedLengthConfigList(configFileName);
        for(FixedLengthConfig fixedLengthConfig:fixedLengthConfigList) {
            Object jsonFieldValue = null;
            if (!(fixedLengthConfig.getName() == null || fixedLengthConfig.getName().isEmpty())) {
                jsonFieldValue = inMessage.get(fixedLengthConfig.getName());
            }

            for(FieldFunction fieldFunction:fixedLengthConfig.getPackFunctions()) {
                jsonFieldValue = fieldFunction.invoke(jsonFieldValue,fixedLengthConfig.getLength());
            }
            if (jsonFieldValue == null) {
                jsonFieldValue = "";
            }

            java.lang.String strValue;
            if (!(jsonFieldValue instanceof java.lang.String)) {
                strValue = jsonFieldValue.toString();
            }
            else {
                strValue = (java.lang.String)jsonFieldValue;
            }
            if (strValue.length()>fixedLengthConfig.getLength()) {
                strValue = strValue.substring(0,fixedLengthConfig.getLength());
            }
            stringBuffer.append(jsonFieldValue);
            if (strValue.length()<fixedLengthConfig.getLength()) {
                stringBuffer.append(StrUtil.repeat(" ",fixedLengthConfig.getLength()-strValue.length()));
            }
        }

        return stringBuffer.toString();
    }

    @Override
    protected JSONObject adaptor2json(Object inMessage) throws BizException {
        java.lang.String inStr = (java.lang.String)inMessage;

        JSONObject jsonObject = new JSONObject();
        int offset = 0;
        for(PreUnpackConfig preUnpackConfig:this.preUnpackConfigList) {
            if (preUnpackConfig.getName() == null || preUnpackConfig.getName().isEmpty()) {
                offset = offset + preUnpackConfig.getLength();
                continue;
            }
            java.lang.String fieldStr = inStr.substring(offset,offset+preUnpackConfig.getLength());
            for(FieldFunction fieldFunction:preUnpackConfig.getFunctions()) {
                fieldStr = fieldFunction.invoke(fieldStr,preUnpackConfig.getLength());
            }
            jsonObject.set(preUnpackConfig.getName(),fieldStr);
            offset = offset + preUnpackConfig.getLength();
        }
        java.lang.String configFileName = this.matchMessagePredicateRule(this.unpackRules,jsonObject);
        if (configFileName == null) {
            throw new BizException(BizResultEnum.NO_MESSAGE_MATCH_RULE);
        }

        List<FixedLengthConfig> fixedLengthConfigList = this.getFixedLengthConfigList(configFileName);
        offset = 0;
        for(FixedLengthConfig fixedLengthConfig:fixedLengthConfigList) {
            Object jsonFieldValue = null;
            if (fixedLengthConfig.getName() == null || fixedLengthConfig.getName().isEmpty()) {
                offset = offset + fixedLengthConfig.getLength();
                continue;
            }
            java.lang.String fieldStr = inStr.substring(offset,offset+fixedLengthConfig.getLength());
            for(FieldFunction fieldFunction:fixedLengthConfig.getUnpackFunctions()) {
                fieldStr = fieldFunction.invoke(fieldStr,fixedLengthConfig.getLength());
            }
            jsonObject.set(fixedLengthConfig.getName(),fieldStr);
            offset = offset + fixedLengthConfig.getLength();
        }

        return jsonObject;
    }

    @Override
    protected JSONObject json2biz(JSONObject inMessage) throws BizException {
        return inMessage;
    }

    private List<FixedLengthConfig> getFixedLengthConfigList(java.lang.String fileName) throws BizException {
        List<FixedLengthConfig> fixedLengthConfigList = this.fixedLengthConfigsMap.get(fileName);
        if (fixedLengthConfigList != null) {
            return fixedLengthConfigList;
        }
        Yaml yaml = new Yaml();
        List<Map> mapList = null;
        try {
            mapList = (List<Map>)yaml.load(new FileInputStream(new File(this.configPath + "/message/" +fileName)));
        } catch (FileNotFoundException e) {
            throw new BizException(BizResultEnum.NO_MESSAGE_CONFIG_FILE,"消息配置文件找不到:"+fileName);
        }
        fixedLengthConfigList = new ArrayList<FixedLengthConfig>();
        for(Map map:mapList) {
            FixedLengthConfig fixedLengthConfig = new FixedLengthConfig(map);
            fixedLengthConfigList.add(fixedLengthConfig);
        }
        this.fixedLengthConfigsMap.put(fileName,fixedLengthConfigList);
        return fixedLengthConfigList;
    }
}
