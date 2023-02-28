package com.youneng.troy.template.common.results;

import lombok.Data;

import java.util.List;

import static com.youneng.troy.template.common.results.BaseStatusEnum.BUSINESS_EXCEPTION;
import static com.youneng.troy.template.common.results.BaseStatusEnum.SUCCESS;


/**
 * @author lishuai17
 * @create 2019-03-22 17:34
 * @desc
 **/
@Data
public class ListObjectResults<T> extends Results {

    List<T> data;

    //----------------------------我是一条漂亮的分割性---------------------------------

    public ListObjectResults() {

    }

    public ListObjectResults(BaseStatusEnum status, String message, List<T> data) {
        this.status = status.getStatus();
        this.message = message;
        this.data = data;
    }

    //----------------------------我是一条漂亮的分割性---------------------------------


    /**
     * 返回成功 并带数据
     *
     * @param data
     * @param <T>
     * @return
     */
    public static <T> ListObjectResults<T> ok(List<T> data) {
        return new ListObjectResults<>(SUCCESS, SUCCESS.getMessage(), data);
    }

    //----------------------------我是一条漂亮的分割性---------------------------------

    /**
     * 返回系统错误 自定义信息
     *
     * @param message
     * @param <T>
     * @return
     */
    public static <T> ListObjectResults<T> createErrorResult(String message) {
        return createErrorResult(null, message);
    }


    /**
     * 返回系统错误 并带数据 自动以message
     *
     * @param data
     * @param message
     * @param <T>
     * @return
     */
    public static <T> ListObjectResults<T> createErrorResult(List<T> data, String message) {
        return new ListObjectResults<>(BaseStatusEnum.ERROR, message, data);
    }

    //----------------------------我是一条漂亮的分割性---------------------------------

    /**
     * 返回业务错误 并带数据 自定义信息
     *
     * @param data
     * @param message
     * @param <T>
     * @return
     */
    public static <T> ListObjectResults<T> createBizExceptionResult(List<T> data, String message) {
        return new ListObjectResults<>(BUSINESS_EXCEPTION, message, data);
    }

    /**
     * 返回业务错误 自定义信息
     *
     * @param message
     * @param <T>
     * @return
     */
    public static <T> ListObjectResults<T> createBizExceptionResult(String message) {
        return createBizExceptionResult(null, message);
    }


    @Override
    public String toString() {
        return "ListObjectResults{" +
                "data=" + data +
                ", status=" + status +
                ", message='" + message + '\'' +
                ", desc='" + desc + '\'' +
                '}';
    }
}
