package com.youneng.troy.template.web.controller;

import com.youneng.seal.api.resp.ObjectResults;
import com.youneng.troy.template.web.param.DemoParam;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Validated
@RestController
@RequestMapping("/template/api/demo")
public class DemoWithoutDependencyController {

    /**
     * 示例
     */
    @PostMapping(value = "/hello",consumes = {MediaType.APPLICATION_JSON_VALUE},produces = {MediaType.APPLICATION_JSON_VALUE})
    public ObjectResults<Void> hello(@Validated @RequestBody DemoParam demoParam) {
        return ObjectResults.createSuccessResult();
    }

    /**
     * 示例
     */
    @PostMapping("/error")
    public ObjectResults<Void> error(@Validated @RequestBody DemoParam demoParam) {
        throw new RuntimeException("error");
    }
}
