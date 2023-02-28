package com.youneng.troy.template.common.results;

import lombok.Data;

import static com.youneng.troy.template.common.results.BaseStatusEnum.*;

/**
 * @author lishuai17
 * @create 2019-03-22 17:34
 * @desc
 **/
@Data
public class ObjectResults<T> extends Results {

    /**
     * 返回的实体数据
     */
    T data;

    //----------------------------我是一条漂亮的分割性---------------------------------

    public ObjectResults() {

    }

    public ObjectResults(BaseStatusEnum statusEnum, T data) {
        this.status = statusEnum.getStatus();
        this.message = statusEnum.getMessage();
        this.data = data;
    }

    public ObjectResults(BaseStatusEnum statusEnum, String message, T data) {
        this.status = statusEnum.getStatus();
        this.message = message;
        this.data = data;
    }

    //----------------------------我是一条漂亮的分割性---------------------------------

    /**
     * 创建返回成功的ObjectResults 无数据
     */
    public static ObjectResults ok() {
        return new ObjectResults<>(SUCCESS, SUCCESS.getMessage(), null);
    }

    /**
     * 创建返回成功的ObjectResults 有数据
     *
     * @param data
     * @param <T>
     * @return
     */
    public static <T> ObjectResults<T> ok(T data) {
        return new ObjectResults<>(SUCCESS, SUCCESS.getMessage(), data);
    }


    //----------------------------我是一条漂亮的分割性---------------------------------

    /**
     * 创建返回系统异常的ObjectResults 无数据
     */
    public static ObjectResults createErrorResult() {
        return createErrorResult(ERROR.getMessage(), null);
    }


    /**
     * 创建返回系统异常的ObjectResults 有数据 并自定义message
     */
    public static <T> ObjectResults<T> createErrorResult(String message, T data) {
        return new ObjectResults<>(ERROR, message, data);
    }


    //----------------------------我是一条漂亮的分割性---------------------------------

    /**
     * 创建返回业务异常的ObjectResults
     */
    public static ObjectResults createBizExceptionResult(String message) {
        return createBusinessExceptionResult(BUSINESS_EXCEPTION.getStatus(), message);
    }

    /**
     * 创建返回业务异常的ObjectResults
     */
    public static ObjectResults createBusinessExceptionResult(Integer errorStatus, String message) {
        ObjectResults<Object> result = createBusinessExceptionResult(errorStatus, message);
        return result;
    }

    /**
     * 创建返回业务异常的ObjectResults 有数据 并自定义异常message
     */
    public static <T> ObjectResults<T> createBusinessExceptionResult(T data, Integer errorStatus, String message) {
        ObjectResults<T> results = new ObjectResults<>(BUSINESS_EXCEPTION, message, data);
        results.setErrorStatus(errorStatus);
        return results;
    }


    @Override
    public String toString() {
        return "ObjectResults{" +
                "data=" + data +
                ", status=" + status +
                ", message='" + message + '\'' +
                ", desc='" + desc + '\'' +
                '}';
    }
}
