package com.bizmda.bizsip.message.fixedlength;

import com.bizmda.bizsip.common.BizException;
import com.bizmda.bizsip.message.fieldfunction.FieldFunction;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author 史正烨
 */
@Data
public class FixedLengthConfig {
    private String name;
    private int length;
    private List<FieldFunction> packFunctions;
    private List<FieldFunction> unpackFunctions;

    public FixedLengthConfig(Map<String,Object> map) throws BizException {
        this.name = (String)map.get("name");
        this.length = (int)map.get("length");
        List<Map<String,Object>> mapList = (List<Map<String,Object>>)map.get("pack-functions");
        this.packFunctions = new ArrayList<>();
        if (mapList != null) {
            for(Map<String,Object> functionMap:mapList) {
                FieldFunction fieldFunction = new FieldFunction(functionMap);
                this.packFunctions.add(fieldFunction);
            }
        }
        mapList = (List<Map<String,Object>>)map.get("unpack-functions");
        this.unpackFunctions = new ArrayList<>();
        if (mapList != null) {
            for(Map<String,Object> functionMap:mapList) {
                FieldFunction fieldFunction = new FieldFunction(functionMap);
                this.unpackFunctions.add(fieldFunction);
            }
        }
    }
}
