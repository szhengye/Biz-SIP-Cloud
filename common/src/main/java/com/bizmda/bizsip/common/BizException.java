package com.bizmda.bizsip.common;

import lombok.Getter;

@Getter
public class BizException extends Exception {
    private BizResultEnum bizResultEnum;

    public BizException(BizResultEnum bizResultEnum) {
        this.bizResultEnum = bizResultEnum;
    }

    public BizException(BizResultEnum bizResultEnum, Throwable e) {
        super(e);
        this.bizResultEnum = bizResultEnum;
    }
}
