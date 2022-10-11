package com.youneng.troy.template.common.exception;

/**
 * @author : sunjianzhi
 * @version V1.0
 * @Project: ProjectTemplate
 * @Package com.youneng.troy.template.common.exception
 * @Description:
 * @date Date : 2022年10月10日 17:14
 */
public class ProjectTemplateException extends RuntimeException{

    /**
     * 业务code
     */
    private Integer code;

    /**
     * 业务message
     */
    private String message;

    public ProjectTemplateException(String message) {
        super(message);
        this.message = message;
    }

    public ProjectTemplateException() {}

    public ProjectTemplateException(Integer code, String message) {
        super(message);
        this.code = code;
        this.message = message;
    }

    public ProjectTemplateException(Throwable cause, String message, Integer code) {
        super(message, cause);
        this.code = code;
        this.message = message;
    }

    public static ProjectTemplateException of(String message) {
        return new ProjectTemplateException(message);
    }

}
