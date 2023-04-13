package com.bc.template.dao.mapper;

import com.bc.template.dao.po.UserPO;
import java.util.List;
import org.springframework.stereotype.Repository;

/**
 * @author : sunjianzhi
 * @version V1.0
 * @Project: ProjectTemplate
 * @Package com.bc.template.dao.mapper
 * @Description:
 * @date Date : 2022年11月16日 14:53
 */
@Repository
public interface DemoMapper {

    UserPO findById(String id);

    List<UserPO> findUsersByName(String nameKeyword);
}
