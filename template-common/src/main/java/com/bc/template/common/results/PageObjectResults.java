package com.bc.template.common.results;

import lombok.Data;

import java.util.List;

import static com.bc.template.common.results.BaseStatusEnum.*;

@Data
public class PageObjectResults<T> extends Results {

    /**
     * 返回的实体数据
     */
    private PageInfo<T> data;

    public PageObjectResults() {
    }

    public PageObjectResults(BaseStatusEnum statusEnum, PageInfo<T> data) {
        this.data = data;
        this.status = statusEnum.getStatus();
        this.message = statusEnum.getMessage();
    }

    public PageObjectResults(BaseStatusEnum statusEnum,String message, PageInfo<T> data) {
        this.data = data;
        this.status = statusEnum.getStatus();
        this.message = message;
    }


    /**
     * 创建返回成功的PageObjectResults 有数据
     *
     * @param data
     * @param <T>
     * @return
     */
    public static <T> PageObjectResults<T> createSuccessResult(PageInfo<T> data) {
        return new PageObjectResults<>(SUCCESS, data);
    }

    /**
     * 创建返回成功的PageObjectResults 有数据
     *
     * @param <T>
     * @return
     */
    public static <T> PageObjectResults<T> createSuccessResult(List<T> rows, Integer pageNum, Integer pageSize, Integer total) {
        return new PageObjectResults<>(SUCCESS, new PageInfo<T>(rows, pageNum, pageSize, total));
    }

    /**
     * 创建返回系统异常的PageObjectResults 有message
     * @return
     */
    public static PageObjectResults createErrorResult(String message) {
        return createErrorResult(null, message);
    }

    /**
     * 创建返回系统异常的PageObjectResults 有数据 并自定义message
     *
     * @param data
     * @param message
     * @param <T>
     * @return
     */
    public static <T> PageObjectResults<T> createErrorResult(PageInfo<T> data, String message) {
        return new PageObjectResults<>(ERROR, message, data);
    }

    /**
     * 创建返回业务异常
     *
     * @return
     */
    public static PageObjectResults createBizExceptionResult(String message) {
        return createBizExceptionResult(null, message);
    }

    /**
     * 创建返回业务异常的 有数据 并自定义异常message
     */
    public static <T> PageObjectResults<T> createBizExceptionResult(PageInfo<T> data, String message) {
        return new PageObjectResults<>(BUSINESS_EXCEPTION, message, data);
    }
}
