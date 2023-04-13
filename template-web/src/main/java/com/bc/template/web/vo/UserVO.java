package com.bc.template.web.vo;

import lombok.Data;

import java.io.Serializable;

/**
 * @author : sunjianzhi
 * @version V1.0
 * @Project: ProjectTemplate
 * @Package com.bc.template.web.vo
 * @Description:
 * @date Date : 2022年11月16日 18:17
 */
@Data
public class UserVO implements Serializable {

    private String id;

    private String name;

    private Integer age;
}
