package com.bc.template.web.param;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author : sunjianzhi
 * @version V1.0
 * @Project: ProjectTemplate
 * @Package com.bc.template.web.param
 * @Description:
 * @date Date : 2022年11月16日 17:59
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserGetParam {

    private String id;

    private String nameKeyword;
}
