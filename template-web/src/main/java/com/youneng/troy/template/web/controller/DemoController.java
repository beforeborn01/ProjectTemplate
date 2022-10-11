package com.youneng.troy.template.web.controller;

import com.xdf.pscommon.env.SealEnv;
import com.xdf.pscommon.env.SealEnvHeaderKeyEnum;
import com.youneng.seal.api.resp.ObjectResults;
import javax.validation.Valid;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import lombok.Data;
import org.hibernate.validator.constraints.Length;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author : sunjianzhi
 * @version V1.0
 * @Project: ProjectTemplate
 * @Package com.youneng.troy.template.web.controller
 * @Description:
 * @date Date : 2022年10月11日 11:20
 */
@Valid
@RestController
@RequestMapping("template/api/demo")
public class DemoController {

    /**
     * 示例
     */
    @RequestMapping("/hello")
    public ObjectResults<Void> hello(@RequestBody DemoParam demoParam) {
        System.out.println(demoParam);
        return ObjectResults.createSuccessResult();
    }

    @Data
    static class DemoParam{

        @NotNull(message="id不能为空")
        private Long id;
        @Length(max=10,message = "name最多10个字符")
        private String name;
        @Max(100)
        @Min(1)
        private Integer age;
    }
}
