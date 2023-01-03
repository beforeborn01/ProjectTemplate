package com.youneng.troy.template.web.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.youneng.seal.api.resp.ListObjectResults;
import com.youneng.seal.api.resp.ObjectResults;
import com.youneng.troy.template.biz.biz.DemoBiz;
import com.youneng.troy.template.biz.bo.UserGetBO;
import com.youneng.troy.template.service.dto.UserDTO;
import com.youneng.troy.template.web.param.DemoParam;
import com.youneng.troy.template.web.param.UserGetParam;
import com.youneng.troy.template.web.vo.UserVO;

import cn.hutool.core.bean.BeanUtil;

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

    @Autowired
    private DemoBiz demoBiz;

    @PostMapping("/get/user")
    public ListObjectResults<UserVO> getUsers(@Validated @RequestBody UserGetParam userGetParam) {
        UserGetBO userGetBO = BeanUtil.toBean(userGetParam, UserGetBO.class);
        List<UserDTO> userDTOS = demoBiz.getUserByCondition(userGetBO);
        return ListObjectResults.createSuccessResult(BeanUtil.copyToList(userDTOS, UserVO.class));
    }

    /**
     * 示例
     */
    @PostMapping("/hello")
    public ObjectResults<Void> hello(@Validated @RequestBody DemoParam demoParam) {
        return ObjectResults.createSuccessResult();
    }

    /**
     * 示例
     */
    @PostMapping("/error")
    public ObjectResults<Void> error(@Validated @RequestBody DemoParam demoParam) {
        throw new RuntimeException("error");
    }

}
