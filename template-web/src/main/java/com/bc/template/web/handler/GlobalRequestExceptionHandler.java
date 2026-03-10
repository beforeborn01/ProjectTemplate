package com.bc.template.web.handler;

import com.bc.template.common.exception.ProjectTemplateException;
import com.bc.template.common.results.ObjectResults;
import com.bc.template.common.results.Results;
import com.bc.template.service.util.FeishuAlertUtil;
import com.bc.template.web.util.ContextEnv;
import lombok.extern.slf4j.Slf4j;
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

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ValidationException;
import java.util.Optional;

/**
 * @author sunjianzhi
 */
@ControllerAdvice
@Slf4j
public class GlobalRequestExceptionHandler {

    private static final int BIZ_EXCEPTION_CODE = 5001;

    private static final String SYSTEM_ERROR_MSG = "系统异常 请稍后重试";

    private static final String PARAM_INVALID = "参数校验异常";

    @Autowired
    private FeishuAlertUtil feishuAlertUtil;

    /**
     * 参数异常
     * //codeRules 注意这里的参数异常不应作为给用户提示的目的
     *
     * @return
     */
    @ExceptionHandler(value = {ValidationException.class, ServletRequestBindingException.class, MethodArgumentTypeMismatchException.class, MethodArgumentNotValidException.class, HttpMessageNotReadableException.class})
    @ResponseBody
    public ObjectResults<String> paramsException(Exception e) {
        //返回400，表示错误的请求
        ObjectResults<String> result = ObjectResults.createErrorResult(PARAM_INVALID);
        if (e instanceof MethodArgumentNotValidException) {
            BindingResult bindingResult = ((MethodArgumentNotValidException) e).getBindingResult();
            FieldError fieldError = bindingResult.getFieldError();
            String desc = Optional.ofNullable(fieldError).map(f -> f.getField() + f.getDefaultMessage()).orElse("");
            result.setDesc(desc);
        } else {
            result.setMessage(e.getMessage());
        }
        log.warn(PARAM_INVALID, e);
        return result;
    }

    /**
     * 业务异常
     */
    @ExceptionHandler(value = ProjectTemplateException.class)
    @ResponseBody
    public Results templateException(ProjectTemplateException e) {
        Results results = ObjectResults.createErrorResult(e.getMessage());
        log.warn("lionException", e);
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
        feishuAlertUtil.alert(e, ContextEnv.getUserEmail(), customMessage);
        log.error("systemException", e);
        return results;
    }
}
