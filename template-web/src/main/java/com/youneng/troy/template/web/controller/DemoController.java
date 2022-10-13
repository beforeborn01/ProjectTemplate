package com.youneng.troy.template.web.controller;

import com.youneng.troy.template.web.param.DemoParam;
import javax.validation.Valid;

import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.youneng.seal.api.resp.ObjectResults;

/**
 * @author : sunjianzhi
 * @version V1.0
 * @Project: ProjectTemplate
 * @Package com.youneng.troy.template.web.controller
 * @Description:
 * @date Date : 2022年10月11日 11:20
 */
@Validated
@RestController
@RequestMapping("template/api/demo")
public class DemoController {

    /**
     * 示例
     */
    @PostMapping("/hello")
    public ObjectResults<Void> hello(@Validated @RequestBody DemoParam demoParam) {
        System.out.println(demoParam);
        return ObjectResults.createSuccessResult();
    }

}
