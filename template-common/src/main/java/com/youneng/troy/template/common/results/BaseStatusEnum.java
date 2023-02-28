package com.youneng.troy.template.common.results;

/**
 * 通用的返回状态枚举
 */
public enum BaseStatusEnum {

    ERROR(0,"服务器打了个盹，还请重试下"),

    SUCCESS(1,"OK"),

    BUSINESS_EXCEPTION(2,"业务异常"),

    NO_SIGN_IN(3,"您还没登录"),

    NO_PERMISSION(4,"未授权");

    private Integer status;

    private String message;

    BaseStatusEnum(Integer status, String message) {
        this.status = status;
        this.message = message;
    }

    public Integer getStatus() {
        return status;
    }

    public String getMessage() {
        return message;
    }
}
