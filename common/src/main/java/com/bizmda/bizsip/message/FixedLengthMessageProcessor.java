package com.bizmda.bizsip.message;

import cn.hutool.core.text.CharSequenceUtil;
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
 * @author 史正烨
 */
public class FixedLengthMessageProcessor extends AbstractMessageProcessor<String> {
    private Map<java.lang.String, List<FixedLengthConfig>> fixedLengthConfigsMap = new HashMap<>();
    private List<PreUnpackConfig> preUnpackConfigList;

    @Override
    public void init(java.lang.String configPath, Map messageMap) throws BizException {
        super.init(configPath,messageMap);
        List<Map<String,Object>> preUnpackConfigMapList = (List<Map<String,Object>>)messageMap.get("pre-unpack");
        this.preUnpackConfigList = new ArrayList<>();
        for(Map<String,Object> map:preUnpackConfigMapList) {
            PreUnpackConfig preUnpackConfig = new PreUnpackConfig(map);
            this.preUnpackConfigList.add(preUnpackConfig);
        }
    }

    @Override
    protected JSONObject biz2json(JSONObject inMessage) throws BizException {
        return inMessage;
    }

    @Override
    protected String json2adaptor(JSONObject inMessage) throws BizException {
        java.lang.String configFileName = this.matchMessagePredicateRule(this.packRules,inMessage);
        if (configFileName == null) {
            throw new BizException(BizResultEnum.NO_MESSAGE_MATCH_RULE);
        }

        StringBuilder stringBuilder = new StringBuilder();
        List<FixedLengthConfig> fixedLengthConfigList = this.getFixedLengthConfigList(configFileName);
        for(FixedLengthConfig fixedLengthConfig:fixedLengthConfigList) {
            buildFixedLengthField(inMessage, stringBuilder, fixedLengthConfig);
        }

        return stringBuilder.toString();
    }

    private void buildFixedLengthField(JSONObject inMessage, StringBuilder stringBuilder, FixedLengthConfig fixedLengthConfig) throws BizException {
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

        String strValue;
        if (!(jsonFieldValue instanceof String)) {
            strValue = jsonFieldValue.toString();
        }
        else {
            strValue = (String)jsonFieldValue;
        }
        if (strValue.length()>fixedLengthConfig.getLength()) {
            strValue = strValue.substring(0,fixedLengthConfig.getLength());
        }
        stringBuilder.append(jsonFieldValue);
        if (strValue.length()<fixedLengthConfig.getLength()) {
            stringBuilder.append(CharSequenceUtil.repeat(" ",fixedLengthConfig.getLength()-strValue.length()));
        }
    }

    @Override
    protected JSONObject adaptor2json(String inMessage) throws BizException {

        JSONObject jsonObject = new JSONObject();
        int offset = 0;
        for(PreUnpackConfig preUnpackConfig:this.preUnpackConfigList) {
            if (preUnpackConfig.getName() == null || preUnpackConfig.getName().isEmpty()) {
                offset = offset + preUnpackConfig.getLength();
                continue;
            }
            java.lang.String fieldStr = inMessage.substring(offset,offset+preUnpackConfig.getLength());
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
            if (fixedLengthConfig.getName() == null || fixedLengthConfig.getName().isEmpty()) {
                offset = offset + fixedLengthConfig.getLength();
                continue;
            }
            java.lang.String fieldStr = inMessage.substring(offset,offset+fixedLengthConfig.getLength());
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
        List<Map<String,Object>> mapList = null;
        try {
            mapList = (List<Map<String,Object>>)yaml.load(new FileInputStream(new File(this.configPath + "/message/" +fileName)));
        } catch (FileNotFoundException e) {
            throw new BizException(BizResultEnum.NO_MESSAGE_CONFIG_FILE,"消息配置文件找不到:"+fileName);
        }
        fixedLengthConfigList = new ArrayList<>();
        for(Map<String,Object> map:mapList) {
            FixedLengthConfig fixedLengthConfig = new FixedLengthConfig(map);
            fixedLengthConfigList.add(fixedLengthConfig);
        }
        this.fixedLengthConfigsMap.put(fileName,fixedLengthConfigList);
        return fixedLengthConfigList;
    }
}
