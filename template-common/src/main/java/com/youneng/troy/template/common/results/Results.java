package com.youneng.troy.template.common.results;

import lombok.Data;

import java.io.Serializable;

@Data
public abstract class Results implements Serializable {

    /**
     * 状态码
     */
    protected Integer status;

    /**
     * 错误码
     */
    protected Integer errorStatus;

    /**
     * 信息
     */
    protected String message;

    /**
     * 详情
     */
    protected String desc;


    public Results() {
    }

}
