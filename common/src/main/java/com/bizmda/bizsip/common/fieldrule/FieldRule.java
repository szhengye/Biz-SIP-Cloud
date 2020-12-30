package com.bizmda.bizsip.common.fieldrule;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class FieldRule {
    private String field;
    private Object value;
    private String rule;
    private List<Object> args;
    private String desc;
}
