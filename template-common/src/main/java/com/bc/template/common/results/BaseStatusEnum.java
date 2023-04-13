package com.bc.template.common.results;

/**
 * 通用的返回状态枚举
 */
public enum BaseStatusEnum {

    ERROR(0,"服务器打了个盹，还请重试下"),

    SUCCESS(1,"OK"),

    BUSINESS_EXCEPTION(2,"业务异常"),
    ;


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
