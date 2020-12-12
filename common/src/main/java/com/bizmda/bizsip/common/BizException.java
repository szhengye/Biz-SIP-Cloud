package com.bizmda.bizsip.common;

import lombok.Getter;

@Getter
public class BizException extends Exception {
    private int code;
    private String extMessage;

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

    public BizException(BizResultEnum bizResultEnum, String extMessage) {
        super((bizResultEnum.getMessage()));
        this.code = code;
        this.extMessage = extMessage;
    }

    public BizException(BizResultEnum bizResultEnum, Throwable e, String extMessage) {
        super(bizResultEnum.getMessage(),e);
        this.code = code;
        this.extMessage = extMessage;
    }

    public BizException(int code,String message,String extMessage) {
        super(message);
        this.code = code;
        this.extMessage = extMessage;
    }

    public BizException(BizMessage bizMessage) {
        super(bizMessage.getMessage());
        this.code = bizMessage.getCode();
        this.extMessage = bizMessage.getExtMessage();
    }
}
