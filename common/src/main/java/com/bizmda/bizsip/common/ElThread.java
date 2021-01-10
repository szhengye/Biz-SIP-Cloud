package com.bizmda.bizsip.common;

import org.springframework.expression.EvaluationContext;
import org.springframework.expression.Expression;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.common.TemplateParserContext;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;

import java.util.concurrent.Callable;

/**
 * @author 史正烨
 */
public class ElThread implements Callable<Object> {
    private String express;
    private boolean isBooleanResult;
    private ExpressionParser parser;
    private EvaluationContext context;

    public ElThread(String express, Object data, boolean isBooleanResult) {
        this.express = express;
        this.isBooleanResult = isBooleanResult;
        this.parser = new SpelExpressionParser();
        this.context = new StandardEvaluationContext();
        this.context.setVariable("data",data);
    }

    @Override
    public Object call() throws Exception {
        Expression expression = this.parser.parseExpression(this.express, new TemplateParserContext());
        if (this.isBooleanResult) {
            return expression.getValue(this.context, Boolean.class);
        }
        else {
            return expression.getValue(this.context, String.class);
        }
    }
}
