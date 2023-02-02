package com.youneng.troy.template.web.controller;

import cn.hutool.core.bean.BeanUtil;
import com.alicp.jetcache.anno.Cached;
import com.youneng.seal.api.resp.ListObjectResults;
import com.youneng.troy.template.biz.biz.DemoBiz;
import com.youneng.troy.template.biz.bo.UserGetBO;
import com.youneng.troy.template.service.dto.UserDTO;
import com.youneng.troy.template.web.param.UserGetParam;
import com.youneng.troy.template.web.vo.UserVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

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
@RequestMapping("/template/api/demo")
public class DemoController {

    @Autowired
    private DemoBiz demoBiz;

    @PostMapping("/get/user")
    public ListObjectResults<UserVO> getUsers(@Validated @RequestBody UserGetParam userGetParam) {
        UserGetBO userGetBO = BeanUtil.toBean(userGetParam, UserGetBO.class);
        List<UserDTO> userDTOS = demoBiz.getUserByCondition(userGetBO);
        return ListObjectResults.createSuccessResult(BeanUtil.copyToList(userDTOS, UserVO.class));
    }

    @PostMapping("/get/user/bycache")
    @Cached(name="cache:ProjectTemplate:DemoController:getUserByCache:")
    public ListObjectResults<UserVO> getUserByCache(@Validated @RequestBody UserGetParam userGetParam) {
        UserGetBO userGetBO = BeanUtil.toBean(userGetParam, UserGetBO.class);
        List<UserDTO> userDTOS = demoBiz.getUserByConditionByCache(userGetBO);
        return ListObjectResults.createSuccessResult(BeanUtil.copyToList(userDTOS, UserVO.class));
    }

    
}
