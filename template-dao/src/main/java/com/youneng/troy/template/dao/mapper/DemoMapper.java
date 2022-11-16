package com.youneng.troy.template.dao.mapper;

import com.youneng.troy.template.dao.po.UserPO;
import java.util.List;
import org.springframework.stereotype.Repository;

/**
 * @author : sunjianzhi
 * @version V1.0
 * @Project: ProjectTemplate
 * @Package com.youneng.troy.template.dao.mapper
 * @Description:
 * @date Date : 2022年11月16日 14:53
 */
@Repository
public interface DemoMapper {

    UserPO findById(String id);

    List<UserPO> findUsersByName(String nameKeyword);
}
