package com.youneng.troy.template.api.interfaces;

import com.youneng.troy.template.api.req.DemoReq;
import com.youneng.troy.template.api.resp.DemoResp;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * @author : sunjianzhi
 * @version V1.0
 * @Project: ProjectTemplate
 * @Package com.youneng.troy.template.api.interfaces
 * @Description:
 * @date Date : 2022年10月13日 10:18
 */
@FeignClient(value = "template", path = "/template/feign/api/demo")
public interface DemoApi {

    /**
     * 示例
     * //codeRules 入参bean以Req结尾，出参以Resp结尾
     */
    @PostMapping("/hello")
    ResponseEntity<DemoResp> hello(@Validated @RequestBody DemoReq demoReq);

    /**
     * 示例
     */
    @PostMapping("/error")
    ResponseEntity<String> error(@Validated @RequestBody DemoReq demoReq);

}
