package com.youneng.troy.template.service.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.youneng.troy.template.dao.mapper.DemoMapper;
import com.youneng.troy.template.service.dto.UserDTO;

import cn.hutool.core.bean.BeanUtil;

/**
 * @author : sunjianzhi
 * @version V1.0
 * @Project: ProjectTemplate
 * @Package com.youneng.troy.template.service.service
 * @Description:
 * @date Date : 2022年11月16日 17:55
 */
@Service
public class DemoService {

    @Autowired
    private DemoMapper demoMapper;

    public UserDTO getUserById(String id) {
        return BeanUtil.toBean(demoMapper.findById(id), UserDTO.class);
    }

    public List<UserDTO> getUsersByName(String nameKeyword) {
        return BeanUtil.copyToList(demoMapper.findUsersByName(nameKeyword), UserDTO.class);
    }
}
