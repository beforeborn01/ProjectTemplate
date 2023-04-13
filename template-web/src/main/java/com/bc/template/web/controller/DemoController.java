package com.bc.template.web.controller;

import cn.hutool.core.bean.BeanUtil;
import com.alicp.jetcache.anno.Cached;
import com.bc.template.biz.biz.DemoBiz;
import com.bc.template.biz.bo.UserGetBO;
import com.bc.template.common.results.ListObjectResults;
import com.bc.template.common.results.ObjectResults;
import com.bc.template.service.dto.UserDTO;
import com.bc.template.web.param.DemoParam;
import com.bc.template.web.param.UserGetParam;
import com.bc.template.web.vo.UserVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author : sunjianzhi
 * @version V1.0
 * @Project: ProjectTemplate
 * @Package com.bc.template.web.controller
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
    @ResponseBody
    public ListObjectResults<UserVO> getUsers(@Validated @RequestBody UserGetParam userGetParam) {
        UserGetBO userGetBO = BeanUtil.toBean(userGetParam, UserGetBO.class);
        List<UserDTO> userDTOS = demoBiz.getUserByCondition(userGetBO);
        return ListObjectResults.ok(BeanUtil.copyToList(userDTOS, UserVO.class));
    }

    @PostMapping("/get/user/bycache")
    @Cached(name = "cache:ProjectTemplate:DemoController:getUserByCache:")
    public ListObjectResults<UserVO> getUserByCache(@Validated @RequestBody UserGetParam userGetParam) {
        UserGetBO userGetBO = BeanUtil.toBean(userGetParam, UserGetBO.class);
        List<UserDTO> userDTOS = demoBiz.getUserByConditionByCache(userGetBO);
        return ListObjectResults.ok(BeanUtil.copyToList(userDTOS, UserVO.class));
    }

    /**
     * 示例
     */
    @PostMapping("/hello")
    public ObjectResults<Void> hello(@Validated @RequestBody DemoParam demoParam) {
        return ObjectResults.ok();
    }

    /**
     * 示例
     */
    @PostMapping("/error")
    public ResponseEntity<Void> error(@Validated @RequestBody DemoParam demoParam) {
        throw new RuntimeException("error");
    }

}
