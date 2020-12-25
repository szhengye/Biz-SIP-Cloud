package com.bizmda.bizsip.message.fieldfunction;

import com.bizmda.bizsip.common.BizException;
import com.bizmda.bizsip.common.BizResultEnum;
import lombok.Data;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Data
public class FieldFunction {

    private String name;
    private List<String> args;
    private Method method;

    public FieldFunction(Map map) throws BizException {
        this.name = (String)map.get("name");
        try {
            this.method = FieldFunctionImpl.class.getMethod(name, Object.class,int.class,List.class);
        } catch (NoSuchMethodException e) {
            throw new BizException(BizResultEnum.NO_FIELD_FUNCTION_IMPL,"域处理方法没实现:"+name);
        }

        this.args = (List<String>)map.get("args");
        if (this.args == null) {
            this.args = new ArrayList<String>();
        }
    }

    public String invoke(Object fieldValue,int fieldLen) throws BizException {
        try {
            return (String)this.method.invoke(null,fieldValue,fieldLen,this.args);
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new BizException(BizResultEnum.FIELD_FUNCTION_IMPL_ERROR,e,this.name+"("+this.args.toString()+")");
        }
    }

}
