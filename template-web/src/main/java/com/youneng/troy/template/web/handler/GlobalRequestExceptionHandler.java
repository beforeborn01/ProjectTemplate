package com.youneng.troy.template.web.handler;

import com.xdf.pscommon.log4j2.core.LogManager;
import com.xdf.pscommon.log4j2.interfaces.Logger;
import com.xdf.seal.openfeign.exception.SealOpenFeignResponseException;
import com.youneng.seal.api.BaseStatusEnum;
import com.youneng.seal.api.resp.ObjectResults;
import com.youneng.seal.api.resp.Results;
import com.youneng.tiger.common.exception.TigerException;
import com.youneng.tiger.common.exception.VerifyException;
import com.youneng.troy.template.web.util.DingtalkAlertUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.ServletRequestBindingException;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import javax.servlet.http.HttpServletRequest;
import javax.validation.ValidationException;
import java.beans.PropertyEditorSupport;
import java.util.Date;

@ControllerAdvice
public class GlobalRequestExceptionHandler {

    public static final Logger logger = LogManager.getLogger(GlobalRequestExceptionHandler.class);

    private static final String SYSTEM_ERROR_MSG = "系统异常 请稍后重试";

    @Autowired
    private DingtalkAlertUtils dingtalkAlertUtils;



    /**
     * openFeign e
     *
     * @param req
     * @param e
     * @return
     */
    @ExceptionHandler(value = SealOpenFeignResponseException.class)
    @ResponseBody
    public Results sealOpenFeignResponseException(HttpServletRequest req, SealOpenFeignResponseException e) {
        return e.getResponseObj();
    }
    /**
     * 参数异常
     *
     * @param req
     * @param e
     * @return
     */
    @ExceptionHandler(value = {ValidationException.class, ServletRequestBindingException.class, MethodArgumentTypeMismatchException.class, MethodArgumentNotValidException.class})
    @ResponseBody
    public Results paramsException(HttpServletRequest req, Exception e) {
        Results results = ObjectResults.createErrorResult(null);
        results.setMessage(SYSTEM_ERROR_MSG);
        if (e instanceof MethodArgumentNotValidException) {
            ObjectError objectError = ((MethodArgumentNotValidException)e).getBindingResult().getAllErrors().get(0);
            String desc = objectError.getDefaultMessage();
            results.setStatus(BaseStatusEnum.ERROR.getStatus());
            results.setMessage(desc);
        }
        return results;
    }


    /**
     * 业务异常
     *
     * @param req
     * @param e
     * @return
     */
    @ExceptionHandler(value = TigerException.class)
    @ResponseBody
    public Results tigerException(HttpServletRequest req, TigerException e) {
        Results results = ObjectResults.createErrorResult(e.getMessage());
        logger.error("TigerException", e);
        return results;
    }

    /**
     * 系统异常
     *
     * @param req
     * @param e
     * @return
     */
    @ExceptionHandler(value = Exception.class)
    @ResponseBody
    public Results allException(HttpServletRequest req, Exception e) {

        Results results = ObjectResults.createErrorResult(SYSTEM_ERROR_MSG);

        dingtalkAlertUtils.dingtalkAlert(req, e);

        logger.error("ExceptionHandler : ", e);

        return results;
    }

}
