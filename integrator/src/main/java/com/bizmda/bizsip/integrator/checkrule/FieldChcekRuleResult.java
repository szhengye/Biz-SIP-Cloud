package com.bizmda.bizsip.integrator.checkrule;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class FieldChcekRuleResult {
    private String field;
    private Object value;
    private String rule;
    private List<Object> args;
    private String message;
}
