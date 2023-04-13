package com.bc.template.web.controller;

import com.bc.template.MysqlContainerBase;
import com.bc.template.RedisContainerBase;
import com.bc.template.common.exception.ProjectTemplateException;
import com.bc.template.common.results.ListObjectResults;
import com.bc.template.web.ApplicationStarter;
import com.bc.template.web.param.UserGetParam;
import com.bc.template.web.vo.UserVO;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;
import java.time.Duration;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author : sunjianzhi
 * @version V1.0
 * @Project: ProjectTemplate
 * @Package com.bc.template.web.controller
 * @Description:
 * @date Date : 2022年12月08日 10:59
 */
@SpringBootTest(classes = ApplicationStarter.class)
@ExtendWith({MysqlContainerBase.class, RedisContainerBase.class}) // 表明依赖的测试套件
//@DirtiesContext // 配置后则会启动一个新的spring容器
public class DemoControllerTest {

    @Resource
    private DemoController demoController;

    @Test
    public void should_get_user_by_id() {
        ListObjectResults<UserVO> users = demoController.getUsers(new UserGetParam("1", ""));
        assertNotNull(users.getData());
        assertEquals(1, users.getData().size());
        assertEquals("1", users.getData().get(0).getId());
    }

    @Test
    public void should_get_user_by_keyword() {
        ListObjectResults<UserVO> users = demoController.getUsers(new UserGetParam("", "zhang"));
        assertNotNull(users.getData());
        assertEquals(2, users.getData().size());
    }

    @Test
    public void should_throw_exception_when_id_keyword_both_assign() {
        assertThrows(ProjectTemplateException.class, () -> demoController.getUsers(new UserGetParam("1", "zhang")));
    }

    @Test
    public void should_use_cache_when_second_invoke() {
        demoController.getUserByCache(new UserGetParam("", "zhang"));
        assertTimeout(Duration.ofSeconds(1), () -> demoController.getUserByCache(new UserGetParam("", "zhang")));
    }
}
