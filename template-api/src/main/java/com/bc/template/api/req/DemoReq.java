package com.bc.template.api.req;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import org.hibernate.validator.constraints.Length;
import lombok.Data;

/**
 * @author : sunjianzhi
 * @version V1.0
 * @Project: ProjectTemplate
 * @Package com.bc.template.web.param
 * @Description:
 * @date Date : 2022年10月13日 10:17
 */
@Data
public class DemoReq {

    @NotNull
    private Long id;
    @Length(min = 1, max = 10)
    private String name;
    @Max(100)
    @Min(1)
    private int age;
}
