package com.bizmda.bizsip.common;

import cn.hutool.json.JSONObject;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.Expression;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.common.TemplateParserContext;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;

import java.util.concurrent.Callable;

public class ELThread implements Callable<BizMessage> {
    private String express;
    private JSONObject data;
    private boolean isBooleanResult;
    private ExpressionParser parser;
    private EvaluationContext context;

    public ELThread(String express, JSONObject data, boolean isBooleanResult) {
        this.express = express;
        this.data = data;
        this.isBooleanResult = isBooleanResult;
        this.parser = new SpelExpressionParser();
        this.context = new StandardEvaluationContext();
        this.context.setVariable("data",data);
    }

    @Override
    public BizMessage call() throws Exception {
        Expression expression = this.parser.parseExpression(this.express, new TemplateParserContext());
        if (this.isBooleanResult) {
            Boolean result = expression.getValue(this.context, Boolean.class);
            return BizMessage.success(result);
        }
        else {
            String result = expression.getValue(this.context, String.class);
            return BizMessage.success(result);
        }
    }
}
