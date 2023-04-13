package com.bc.template.web.util;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpMethod;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

import static java.nio.charset.StandardCharsets.ISO_8859_1;
import static java.nio.charset.StandardCharsets.UTF_8;

/**
 * @author lishuai17
 * @create 2019-03-27 15:56
 * @desc
 **/
@Slf4j
public class RequestJsonUtil {

    private static final String QUOT = "%22";

    public static String getRequestJsonString(HttpServletRequest request) {
        try {
            String submitMethod = request.getMethod();
            // GET
            if (HttpMethod.GET.toString().equals(submitMethod)) {
                if (StringUtils.isNotEmpty(request.getQueryString())) {
                    return new String(request.getQueryString().getBytes(ISO_8859_1), UTF_8).replaceAll(QUOT, "\"");
                }
                return new String("".getBytes(ISO_8859_1), UTF_8).replaceAll(QUOT, "\"");
            }

            // POST
            String requestString = getRequestPostStr(request);

            if (StringUtils.isNotBlank(requestString)) {
                return requestString;
            }
            if (StringUtils.isNotEmpty(request.getQueryString())) {
                return new String(request.getQueryString().getBytes(ISO_8859_1), UTF_8).replaceAll(QUOT, "\"");
            }
            return new String("".getBytes(ISO_8859_1), UTF_8).replaceAll(QUOT, "\"");
        } catch (IOException e) {
            log.error("获取请求参数异常", e);
        }
        return null;
    }

    /**
     * 描述:获取 post 请求的 byte[] 数组
     */
    public static byte[] getRequestPostBytes(HttpServletRequest request) throws IOException {
        int contentLength = request.getContentLength();
        if (contentLength < 0) {
            return null;
        }
        byte[] buffer = new byte[contentLength];
        for (int i = 0; i < contentLength;) {

            int readLen = request.getInputStream().read(buffer, i, contentLength - i);
            if (readLen == -1) {
                break;
            }
            i += readLen;
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
        byte[] buffer = getRequestPostBytes(request);
        String charEncoding = request.getCharacterEncoding();
        if (charEncoding == null) {
            charEncoding = UTF_8.name();
        }
        if (null == buffer) {
            return null;
        } else {
            return new String(buffer, charEncoding);
        }
    }

}
