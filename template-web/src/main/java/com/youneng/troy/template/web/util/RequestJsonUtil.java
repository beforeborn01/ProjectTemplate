package com.youneng.troy.template.web.util;

import com.xdf.pscommon.log4j2.core.LogManager;
import com.xdf.pscommon.log4j2.interfaces.Logger;
import java.io.IOException;
import javax.servlet.http.HttpServletRequest;
import org.apache.commons.lang3.StringUtils;

/**
 * @author lishuai17
 * @create 2019-03-27 15:56
 * @desc
 **/
public class RequestJsonUtil {

    public static final Logger logger = LogManager.getLogger(RequestJsonUtil.class);

    /***
     * 获取 request 中 json 字符串的内容
     *
     * @param request
     * @return : <code>byte[]</code>
     * @throws IOException
     */
    public static String getRequestJsonString(HttpServletRequest request) throws IOException {
        String submitMehtod = request.getMethod();
        // GET
        if (submitMehtod.equals("GET")) {
            if (StringUtils.isNotEmpty(request.getQueryString())) {
                return new String(request.getQueryString().getBytes("iso-8859-1"), "utf-8").replaceAll("%22", "\"");
            }
            return new String("".getBytes("iso-8859-1"), "utf-8").replaceAll("%22", "\"");
        }

        // POST
        String requestString = getRequestPostStr(request);

        if (StringUtils.isNotBlank(requestString)) {
            return requestString;
        }

        if (StringUtils.isNotEmpty(request.getQueryString())) {
            return new String(request.getQueryString().getBytes("iso-8859-1"), "utf-8").replaceAll("%22", "\"");
        }
        return new String("".getBytes("iso-8859-1"), "utf-8").replaceAll("%22", "\"");
    }

    public static String getRequestJsonStringNoException(HttpServletRequest request) {
        try {
            return getRequestJsonString(request);
        } catch (IOException e) {
            logger.error("获取请求参数异常", e);
        }
        return null;
    }

    /**
     * 描述:获取 post 请求的 byte[] 数组
     * 
     * <pre>
     * 举例：
     * </pre>
     * 
     * @param request
     * @return
     * @throws IOException
     */
    public static byte[] getRequestPostBytes(HttpServletRequest request) throws IOException {
        int contentLength = request.getContentLength();
        if (contentLength < 0) {
            return null;
        }
        byte buffer[] = new byte[contentLength];
        for (int i = 0; i < contentLength;) {

            int readlen = request.getInputStream().read(buffer, i, contentLength - i);
            if (readlen == -1) {
                break;
            }
            i += readlen;
        }
        return buffer;
    }

    /**
     * 描述:获取 post 请求内容
     * 
     * <pre>
     * 举例：
     * </pre>
     * 
     * @param request
     * @return
     * @throws IOException
     */
    public static String getRequestPostStr(HttpServletRequest request) throws IOException {
        byte buffer[] = getRequestPostBytes(request);
        String charEncoding = request.getCharacterEncoding();
        if (charEncoding == null) {
            charEncoding = "UTF-8";
        }
        if (null == buffer) {
            return null;
        } else {
            return new String(buffer, charEncoding);
        }
    }

}
