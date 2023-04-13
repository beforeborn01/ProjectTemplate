package com.bc.template.dao.mapper;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.List;
import javax.annotation.Resource;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.context.SpringBootTest;

import com.bc.template.MysqlContainerBase;
import com.bc.template.dao.po.UserPO;
import com.bc.template.web.ApplicationStarter;

/**
 * @author : sunjianzhi
 * @version V1.0
 * @Project: ProjectTemplate
 * @Package com.bc.template.dao.mapper
 * @Description:
 * @date Date : 2022年11月16日 15:05
 */
@SpringBootTest(classes = ApplicationStarter.class)
@ExtendWith({MysqlContainerBase.class}) // 表明依赖的测试套件,可以是多个
public class DemoMapperTest {

    @Resource
    private DemoMapper demoMapper;

    @Test
    public void should_find_correct_user_by_id() {
        UserPO userPO = demoMapper.findById("1");
        assertNotNull(userPO);
        assertEquals("1", userPO.getId());
        assertEquals("zhangsan", userPO.getName());
        assertEquals(30, userPO.getAge());
    }

    @Test
    public void should_find_correct_users_by_name_keyword() {
        List<UserPO> userList = demoMapper.findUsersByName("zhang");
        assertNotNull(userList);
        assertEquals(2, userList.size());
    }
}
