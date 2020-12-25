package com.bizmda.bizsip.message.fieldfunction;

import cn.hutool.core.util.NumberUtil;
import cn.hutool.core.util.StrUtil;

import java.util.List;

public class FieldFunctionImpl {
    public static String fill(Object fieldValue, int fieldLen, List<String> args) {
        if (fieldLen == 0) {
            if (args.size() >= 3) {
                fieldLen = Integer.parseInt(args.get(2));
            }
            else {
                return fieldValue.toString();
            }
        }
        if (args.size() < 2) {
            return (String)fieldValue;
        }
        int fillContentLen = args.get(1).length();
        if (fillContentLen == 0) {
            return (String)fieldValue;
        }
        String fieldValueStr = (String)fieldValue;
        String fillStr = StrUtil.repeat(args.get(1),fieldLen - fieldValueStr.length());
        if (args.get(0).equalsIgnoreCase("left")) {
            fieldValueStr = fillStr + fieldValueStr;
            return fieldValueStr.substring(fieldValueStr.length() - fieldLen,fieldValueStr.length());
        }
        if (args.get(0).equalsIgnoreCase("right")) {
            fieldValueStr = fieldValueStr + fillStr;
            return fieldValueStr.substring(0,fieldLen);
        }
        return fieldValueStr;
    }

    public static String trim(Object fieldValue, int fieldLen, List<String> args) {
        if (args.size() == 0) {
            return fieldValue.toString().trim();
        }
        if (args.get(0).equalsIgnoreCase("left")) {
            return StrUtil.trimStart(fieldValue.toString());
        }
        if (args.get(0).equalsIgnoreCase("right")) {
            return StrUtil.trimEnd(fieldValue.toString());
        }

        return fieldValue.toString().trim();
    }

    public static String decimalFormat(Object fieldValue, int fieldLen, List<String> args) {
        if (args.size() == 0) {
            return fieldValue.toString().trim();
        }

        return NumberUtil.decimalFormat(args.get(0),fieldValue);
    }
}
