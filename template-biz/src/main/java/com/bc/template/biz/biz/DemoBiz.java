package com.bc.template.biz.biz;

import java.util.Collections;
import java.util.List;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.bc.template.biz.bo.UserGetBO;
import com.bc.template.common.exception.ProjectTemplateException;
import com.bc.template.service.dto.UserDTO;
import com.bc.template.service.service.DemoService;

/**
 * @author : sunjianzhi
 * @version V1.0
 * @Project: ProjectTemplate
 * @Package com.bc.template.biz.biz
 * @Description:
 * @date Date : 2022年11月16日 17:53
 */
@Service
public class DemoBiz {
    @Autowired
    private DemoService demoService;

    public List<UserDTO> getUserByCondition(UserGetBO userGetBO) {
        // 业务规则判断
        String id = userGetBO.getId();
        String nameKeyword = userGetBO.getNameKeyword();
        if (StringUtils.isNotBlank(id) && StringUtils.isNotBlank(nameKeyword)) {
            throw new ProjectTemplateException("id和name不能同时指定");
        }
        //根据不同的参数调用不同的service方法
        if (StringUtils.isNotBlank(id)) {
            return Collections.singletonList(demoService.getUserById(id));
        }
        if (StringUtils.isNotBlank(nameKeyword)) {
            return demoService.getUsersByName(nameKeyword);
        }
        return null;
    }

    public List<UserDTO> getUserByConditionByCache(UserGetBO userGetBO) {
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        return this.getUserByCondition(userGetBO);
    }
}
