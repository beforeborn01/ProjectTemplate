package com.youneng.troy.template.web.handler;

import com.youneng.troy.template.common.exception.ProjectTemplateException;
import com.youneng.troy.template.service.util.DingTalkAlertUtil;
import com.youneng.troy.template.web.util.ContextEnv;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.ResponseEntity.BodyBuilder;
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

import javax.servlet.http.HttpServletRequest;
import javax.validation.ValidationException;
import java.util.Optional;

/**
 * @author sunjianzhi
 */
@ControllerAdvice
@Slf4j
public class GlobalRequestExceptionHandler {

    private static final int BIZ_EXCEPTION_CODE = 5001;

    @Autowired
    private DingTalkAlertUtil dingTalkAlertUtil;

    /**
     * 参数异常
     * //codeRules 注意这里的参数异常不应作为给用户提示的目的
     *
     * @return
     */
    @ExceptionHandler(value = {ValidationException.class, ServletRequestBindingException.class, MethodArgumentTypeMismatchException.class, MethodArgumentNotValidException.class, HttpMessageNotReadableException.class})
    @ResponseBody
    public ResponseEntity<String> paramsException(Exception e) {
        //返回400，表示错误的请求
        BodyBuilder bodyBuilder = ResponseEntity.badRequest();
        if (e instanceof MethodArgumentNotValidException) {
            BindingResult bindingResult = ((MethodArgumentNotValidException) e).getBindingResult();
            FieldError fieldError = bindingResult.getFieldError();
            String desc = Optional.ofNullable(fieldError).map(f -> f.getField() + f.getDefaultMessage()).orElse("");
            bodyBuilder.body(desc);
        } else {
            bodyBuilder.body(e.getMessage());
        }
        log.warn("paramsException", e);
        return bodyBuilder.build();
    }

    /**
     * 业务异常
     */
    @ExceptionHandler(value = ProjectTemplateException.class)
    @ResponseBody
    public ResponseEntity<String> templateException(ProjectTemplateException e) {
        //返回自定义状态码5001，表示业务异常
        BodyBuilder bodyBuilder = ResponseEntity.status(BIZ_EXCEPTION_CODE);
        log.warn("templateException", e);
        return bodyBuilder.body(e.getMessage());
    }

    /**
     * 其他异常处理
     */
    @ExceptionHandler(value = Exception.class)
    @ResponseBody
    public ResponseEntity<String> allException(HttpServletRequest req, Exception e) {
        //返回自定义状态码5001，表示业务异常
        BodyBuilder bodyBuilder = ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR);
        Object urlObject = req.getAttribute(HandlerMapping.BEST_MATCHING_PATTERN_ATTRIBUTE);
        String customMessage = "URL : " + (urlObject == null ? req.getRequestURL() : urlObject);
        dingTalkAlertUtil.alert(e, ContextEnv.getUserEmail(), customMessage);
        log.error("systemException", e);
        return bodyBuilder.body("{\"error\":\"系统异常，请稍后再试\"}");
    }
}
