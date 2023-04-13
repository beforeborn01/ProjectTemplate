package com.bc.template.dao.po;

import java.util.Date;
import lombok.Data;

/**
 * @author : sunjianzhi
 * @version V1.0
 * @Project: ProjectTemplate
 * @Package com.bc.template.dao.po
 * @Description:
 * @date Date : 2022年11月16日 14:54
 */
@Data
public class UserPO {

    private String id;

    private String name;

    private int age;

    private String creator;

    private String updator;

    private Date createTime;

    private Date updateTime;
}
