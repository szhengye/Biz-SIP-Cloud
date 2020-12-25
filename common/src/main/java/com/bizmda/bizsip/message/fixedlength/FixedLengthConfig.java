package com.bizmda.bizsip.message.fixedlength;

import com.bizmda.bizsip.common.BizException;
import com.bizmda.bizsip.message.fieldfunction.FieldFunction;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Data
public class FixedLengthConfig {
    private String name;
    private int length;
    private List<FieldFunction> packFunctions;
    private List<FieldFunction> unpackFunctions;

    public FixedLengthConfig(Map map) throws BizException {
        this.name = (String)map.get("name");
        this.length = (int)map.get("length");
        List<Map> mapList = (List<Map>)map.get("pack-functions");
        this.packFunctions = new ArrayList<FieldFunction>();
        if (mapList != null) {
            for(Map functionMap:mapList) {
                FieldFunction fieldFunction = new FieldFunction(functionMap);
                this.packFunctions.add(fieldFunction);
            }
        }
        mapList = (List<Map>)map.get("unpack-functions");
        this.unpackFunctions = new ArrayList<FieldFunction>();
        if (mapList != null) {
            for(Map functionMap:mapList) {
                FieldFunction fieldFunction = new FieldFunction(functionMap);
                this.unpackFunctions.add(fieldFunction);
            }
        }
    }
}
