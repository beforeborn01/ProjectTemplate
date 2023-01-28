package com.youneng.troy.template.web.util;

import com.alibaba.ttl.TransmittableThreadLocal;
import org.apache.commons.lang3.StringUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * 登录人相关信息
 *
 * @author sunjianzhi
 */
public class ContextEnv {

    private static final TransmittableThreadLocal<Map<String, String>> CONTEXT_ENV_THREAD_LOCAL = new TransmittableThreadLocal<Map<String, String>>();

    /**
     * 登录人的email
     */
    public static final String USER_EMAIL = "userEmail";
    /**
     * 登录人的名字
     */
    public static final String USER_NAME = "userName";

    /**
     * 获取Context
     * 
     * @return
     */
    public static Map<String, String> getContext() {

        if (CONTEXT_ENV_THREAD_LOCAL.get() == null) {
            CONTEXT_ENV_THREAD_LOCAL.set(new HashMap<>(16));
        }
        return CONTEXT_ENV_THREAD_LOCAL.get();
    }

    /**
     * 设定环境变量信息
     * 
     * @param key
     * @param value
     */
    public static void setContextEnv(String key, String value) {
        if (StringUtils.isBlank(key) || StringUtils.isBlank(value)) {
            return;
        }
        getContext().put(key, value);
    }

    /**
     * 清除当前线程数据
     */
    public static void clean() {
        CONTEXT_ENV_THREAD_LOCAL.remove();
    }

    /**
     * 获取登录人的email
     * 
     * @return
     */
    public static String getUserEmail() {
        return getContext().get(USER_EMAIL);
    }

    /**
     * 获取登录人的名字
     * 
     * @return
     */
    public static String getUserName() {
        return getContext().get(USER_NAME);
    }

}
