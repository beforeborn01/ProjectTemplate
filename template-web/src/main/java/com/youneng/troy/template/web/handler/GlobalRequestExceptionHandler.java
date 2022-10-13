package com.youneng.troy.template.web.handler;

import com.youneng.troy.template.web.util.ProjectTemplateContextEnv;
import java.util.Optional;

import javax.servlet.http.HttpServletRequest;
import javax.validation.ValidationException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.ServletRequestBindingException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.HandlerMapping;

import com.xdf.pscommon.log4j2.core.LogManager;
import com.xdf.pscommon.log4j2.interfaces.Logger;
import com.youneng.seal.api.resp.ObjectResults;
import com.youneng.seal.api.resp.Results;
import com.youneng.troy.template.common.exception.ProjectTemplateException;
import com.youneng.troy.template.service.util.DingTalkAlertUtil;

/**
 * @author sunjianzhi
 */
@ControllerAdvice
public class GlobalRequestExceptionHandler {

    public static final Logger LOGGER = LogManager.getLogger(GlobalRequestExceptionHandler.class);

    private static final String SYSTEM_ERROR_MSG = "系统异常 请稍后重试";
    private static final String PARAM_INVALID = "参数校验异常";

    @Autowired
    private DingTalkAlertUtil dingTalkAlertUtil;

    /**
     * 参数异常
     * //codeRules 注意这里的参数异常不应作为给用户提示的目的
     * 
     * @return
     */
    @ExceptionHandler(value = {ValidationException.class, ServletRequestBindingException.class, MethodArgumentTypeMismatchException.class,
        MethodArgumentNotValidException.class, HttpMessageNotReadableException.class})
    @ResponseBody
    public Results paramsException(Exception e) {
        Results results = ObjectResults.createErrorResult(null);
        results.setMessage(PARAM_INVALID);

        if (e instanceof MethodArgumentNotValidException) {
            BindingResult bindingResult = ((MethodArgumentNotValidException)e).getBindingResult();
            FieldError fieldError = bindingResult.getFieldError();
            String desc = Optional.ofNullable(fieldError).map(f -> f.getField() + f.getDefaultMessage()).orElse("");
            results.setDesc(desc);
        } else {
            results.setDesc(e.getMessage());
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
        Object urlObject = req.getAttribute(HandlerMapping.BEST_MATCHING_PATTERN_ATTRIBUTE);
        String customMessage = "URL : " + (urlObject == null ? req.getRequestURL() : urlObject);
        dingTalkAlertUtil.alert(e, ProjectTemplateContextEnv.getUserEmail(), customMessage);
        LOGGER.error("systemException", e);
        return results;
    }

}
