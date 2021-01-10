package com.bizmda.bizsip.common;

import lombok.Getter;

/**
 * @author 史正烨
 */
@Getter
public class BizException extends Exception {
    private final int code;
    private final String extMessage;

    public BizException(BizResultEnum bizResultEnum) {
        super((bizResultEnum.getMessage()));
        this.code = bizResultEnum.getCode();
        this.extMessage = null;
    }

    public BizException(BizResultEnum bizResultEnum, Throwable e) {
        super(bizResultEnum.getMessage(),e);
        this.code = bizResultEnum.getCode();
        this.extMessage = null;
    }

    public BizException(int code,String message) {
        super(message);
        this.code = code;
        this.extMessage = null;
    }

    public BizException(BizResultEnum bizResultEnum, String extMessage) {
        super((bizResultEnum.getMessage()));
        this.code = bizResultEnum.getCode();
        this.extMessage = extMessage;
    }

    public BizException(BizResultEnum bizResultEnum, Throwable e, String extMessage) {
        super(bizResultEnum.getMessage(),e);
        this.code = bizResultEnum.getCode();
        this.extMessage = extMessage;
    }

    public BizException(int code,String message,String extMessage) {
        super(message);
        this.code = code;
        this.extMessage = extMessage;
    }

    public BizException(BizMessage<Object> bizMessage) {
        super(bizMessage.getMessage());
        this.code = bizMessage.getCode();
        this.extMessage = bizMessage.getExtMessage();
    }
}
