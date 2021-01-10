package com.bizmda.bizsip.message;

import cn.hutool.core.text.StrSpliter;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import com.bizmda.bizsip.common.BizException;
import com.bizmda.bizsip.common.BizResultEnum;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.runtime.RuntimeConstants;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;


/**
 * @author 史正烨
 */
public class VelocitySplitMessageProcessor extends AbstractMessageProcessor<String> {
    private List<String> separators;
    @Override
    public void init(String configPath,Map messageMap) throws BizException {
        super.init(configPath,messageMap);
        this.separators = (List<String>)messageMap.get("separators");
        Properties properties = new Properties();
        properties.setProperty(RuntimeConstants.FILE_RESOURCE_LOADER_PATH, this.configPath + "/message");
        properties.setProperty(RuntimeConstants.ENCODING_DEFAULT, "UTF-8");
        properties.setProperty(RuntimeConstants.OUTPUT_ENCODING, "UTF-8");
        Velocity.init(properties);
        VelocityEngine velocityEngine = new VelocityEngine();
        velocityEngine.init();
    }

    @Override
    protected JSONObject biz2json(JSONObject inMessage) throws BizException {
        return inMessage;
    }

    @Override
    protected String json2adaptor(JSONObject inMessage) throws BizException {
        Map<String,Object> map = new HashMap<>();
        map.put("data",inMessage);
        VelocityContext velocityContext = new VelocityContext(map);
        StringWriter stringWriter = new StringWriter();
        String templateFileName = this.matchMessagePredicateRule(this.packRules,inMessage);
        if (templateFileName == null) {
            throw new BizException(BizResultEnum.NO_MESSAGE_MATCH_RULE);
        }
        Template template = Velocity.getTemplate(templateFileName, "UTF-8" );
        template.merge(velocityContext, stringWriter);
        return stringWriter.toString();
    }

    @Override
    protected JSONObject adaptor2json(String inMessage) throws BizException {
        String message = inMessage;
        JSONObject jsonObject = new JSONObject();
        JSONArray jsonArray = this.createArray(0,message);
        if (jsonArray == null) {
            jsonObject.set("data",inMessage);
        }
        else {
            jsonObject.set("data", jsonArray);
        }
        return jsonObject;
    }

    @Override
    protected JSONObject json2biz(JSONObject inMessage) throws BizException {
        return inMessage;
    }

    private JSONArray createArray(int level,String str) {
        if (this.separators.size()-level <= 0) {
            return null;
        }
        List<String> splitList = StrSpliter.split(str,this.separators.get(level),false,false);
        if (splitList.size() <= 1) {
            return null;
        }
        JSONArray jsonArray = new JSONArray();
        for(String subStr:splitList) {
            JSONArray jsonArray1 = this.createArray(level+1,subStr);
            if (jsonArray1 == null) {
                jsonArray.add(subStr);
            }
            else {
                jsonArray.add(jsonArray1);
            }
        }
        return jsonArray;
    }
}
