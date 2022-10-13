package com.youneng.troy.template.api.interfaces;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import com.youneng.seal.api.resp.ObjectResults;
import com.youneng.troy.template.api.req.DemoReq;
import com.youneng.troy.template.api.resp.DemoResp;

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
     */
    @PostMapping("/hello")
    ObjectResults<DemoResp> hello(@Validated @RequestBody DemoReq demoReq);

}
