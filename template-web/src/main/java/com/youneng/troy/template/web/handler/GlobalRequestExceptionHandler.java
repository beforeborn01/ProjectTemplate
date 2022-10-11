package com.youneng.troy.template.web.handler;

import com.xdf.pscommon.log4j2.core.LogManager;
import com.xdf.pscommon.log4j2.interfaces.Logger;
import com.xdf.seal.openfeign.exception.SealOpenFeignResponseException;
import com.youneng.seal.api.BaseStatusEnum;
import com.youneng.seal.api.resp.ObjectResults;
import com.youneng.seal.api.resp.Results;
import com.youneng.troy.template.common.exception.ProjectTemplateException;
import com.youneng.troy.template.web.util.DingTalkAlertUtils;
import javax.servlet.http.HttpServletRequest;
import javax.validation.ValidationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.ServletRequestBindingException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

/**
 * @author sunjianzhi
 */
@ControllerAdvice
public class GlobalRequestExceptionHandler {

    public static final Logger LOGGER = LogManager.getLogger(GlobalRequestExceptionHandler.class);

    private static final String SYSTEM_ERROR_MSG = "系统异常 请稍后重试";

    @Autowired
    private DingTalkAlertUtils dingtalkAlertUtils;

    /**
     * openFeign e
     */
    @ExceptionHandler(value = SealOpenFeignResponseException.class)
    @ResponseBody
    public Results sealOpenFeignResponseException(SealOpenFeignResponseException e) {
        return e.getResponseObj();
    }
    /**
     * 参数异常
     *
     * @param e
     * @return
     */
    @ExceptionHandler(value = {ValidationException.class, ServletRequestBindingException.class, MethodArgumentTypeMismatchException.class, MethodArgumentNotValidException.class})
    @ResponseBody
    public Results paramsException(Exception e) {
        Results results = ObjectResults.createErrorResult(null);
        results.setMessage(SYSTEM_ERROR_MSG);

        if (e instanceof MethodArgumentNotValidException) {
            ObjectError objectError = ((MethodArgumentNotValidException)e).getBindingResult().getAllErrors().get(0);
            String desc = objectError.getDefaultMessage();
            results.setStatus(BaseStatusEnum.ERROR.getStatus());
            results.setMessage(desc);
        }
        if (e instanceof ValidationException) {
            ValidationException ve = ((ValidationException)e);
        }
        if (e instanceof MethodArgumentTypeMismatchException) {
            MethodArgumentTypeMismatchException mte = ((MethodArgumentTypeMismatchException)e);
        }
        if (e instanceof ServletRequestBindingException) {
            ServletRequestBindingException srbe = ((ServletRequestBindingException)e);
        }
        LOGGER.warn("paramsException", e);
        return results;
    }


    /**
     * 业务异常
     */
    @ExceptionHandler(value = ProjectTemplateException.class)
    @ResponseBody
    public Results templateException(ProjectTemplateException e) {
        Results results = ObjectResults.createErrorResult(e.getMessage());
        LOGGER.warn("templateException", e);
        return results;
    }

    /**
     * 其他异常处理
     */
    @ExceptionHandler(value = Exception.class)
    @ResponseBody
    public Results allException(HttpServletRequest req, Exception e) {

        Results results = ObjectResults.createErrorResult(SYSTEM_ERROR_MSG);

        dingtalkAlertUtils.dingTalkAlert(req, e);

        LOGGER.error("ExceptionHandler : ", e);

        return results;
    }

}
