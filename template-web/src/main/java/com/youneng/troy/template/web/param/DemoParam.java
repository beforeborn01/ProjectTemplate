package com.youneng.troy.template.web.param;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

/**
 * @author : sunjianzhi
 * @version V1.0
 * @Project: ProjectTemplate
 * @Package com.youneng.troy.template.web.param
 * @Description:
 * @date Date : 2022年10月13日 10:17
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class DemoParam {

    @NotNull
    private Long id;
    @Length(min = 1, max = 10)
    private String name;
    @Max(100)
    @Min(1)
    private int age;
}
