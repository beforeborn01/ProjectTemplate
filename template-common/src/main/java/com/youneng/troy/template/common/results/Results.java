package com.youneng.troy.template.common.results;

import lombok.Data;

import java.io.Serializable;

/**
 * @author lishuai17
 * @create 2019-03-22 17:26
 * @desc
 **/
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
