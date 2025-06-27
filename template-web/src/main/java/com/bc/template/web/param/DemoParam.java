package com.bc.template.web.param;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

/**
 * @author : sunjianzhi
 * @version V1.0
 * @Project: ProjectTemplate
 * @Package com.bc.template.web.param
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
