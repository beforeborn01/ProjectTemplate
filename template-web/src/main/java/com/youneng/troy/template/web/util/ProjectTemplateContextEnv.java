package com.youneng.troy.template.web.util;

import com.alibaba.ttl.TransmittableThreadLocal;
import com.xdf.pscommon.log4j2.core.LogManager;
import com.xdf.pscommon.log4j2.interfaces.Logger;
import java.util.HashMap;
import java.util.Map;
import org.apache.commons.lang3.StringUtils;

/**
 * 登录人相关信息
 */
public class ProjectTemplateContextEnv {

    public static final Logger logger = LogManager.getLogger(ProjectTemplateContextEnv.class);

    private static final TransmittableThreadLocal<Map<String, String>> tigerContextEnvThreadLocal = new TransmittableThreadLocal<Map<String, String>>();

    /**
     * 登录人的email
     */
    public static final String USER_EMAIL = "userEmail";
    /**
     * 登录人的名字
     */
    public static final String USER_NAME = "userName";
    /**
     * 登录人的角色
     */
    public static final String USER_ROLE_TYP = "userRoleTyp";

    /**
     * 获取Context
     * 
     * @return
     */
    public static Map<String, String> getContext() {

        if (tigerContextEnvThreadLocal.get() == null) {
            tigerContextEnvThreadLocal.set(new HashMap<>());
        }
        return tigerContextEnvThreadLocal.get();
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
        tigerContextEnvThreadLocal.remove();
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

    /**
     * 获取登录人的角色
     * 
     * @return
     */
    public static Integer getUserRoleType() {
        String type = getContext().get(USER_ROLE_TYP);
        return StringUtils.isBlank(type) ? null : Integer.parseInt(type);
    }
}
