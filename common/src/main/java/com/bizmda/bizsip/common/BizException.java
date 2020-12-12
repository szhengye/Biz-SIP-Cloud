package com.bizmda.bizsip.common;

import lombok.Getter;

@Getter
public class BizException extends Exception {
    private int code;
//    private BizResultEnum bizResultEnum;

    public BizException(BizResultEnum bizResultEnum) {
        super((bizResultEnum.getMessage()));
        this.code = code;
    }

    public BizException(BizResultEnum bizResultEnum, Throwable e) {
        super(bizResultEnum.getMessage(),e);
        this.code = code;
    }

    public BizException(int code,String message) {
        super(message);
        this.code = code;
    }
}
