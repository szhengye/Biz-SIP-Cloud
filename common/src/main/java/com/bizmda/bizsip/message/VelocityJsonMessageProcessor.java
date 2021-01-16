package com.bizmda.bizsip.message;

import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.bizmda.bizsip.common.BizConstant;
import com.bizmda.bizsip.common.BizException;
import com.bizmda.bizsip.common.BizResultEnum;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.runtime.RuntimeConstants;

import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;


/**
 * @author 史正烨
 */
public class VelocityJsonMessageProcessor extends AbstractMessageProcessor<String> {
    @Override
    public void init(String configPath,Map messageMap) throws BizException {
        super.init(configPath,messageMap);
        Properties properties = new Properties();
        properties.setProperty(RuntimeConstants.FILE_RESOURCE_LOADER_PATH, this.configPath + "/message");
        properties.setProperty(RuntimeConstants.ENCODING_DEFAULT, BizConstant.DEFAULT_CHARSET_NAME);
        properties.setProperty(RuntimeConstants.OUTPUT_ENCODING, BizConstant.DEFAULT_CHARSET_NAME);
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
        return JSONUtil.parseObj(inMessage);
    }

    @Override
    protected JSONObject json2biz(JSONObject inMessage) throws BizException {
        return inMessage;
    }
}
