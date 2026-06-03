package com.property.common;

/**
 * 业务异常
 */
public class BusinessException extends RuntimeException {

    /** 业务错误码 */
    private final int errorCode;

    public BusinessException(String message) {
        super(message);
        this.errorCode = 1;
    }

    public BusinessException(int errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
    }

    public BusinessException(String message, Throwable cause) {
        super(message, cause);
        this.errorCode = 1;
    }

    public int getErrorCode() {
        return errorCode;
    }
}
