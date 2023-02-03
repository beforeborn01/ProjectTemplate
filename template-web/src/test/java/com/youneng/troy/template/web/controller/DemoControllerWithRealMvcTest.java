package com.youneng.troy.template.web.controller;

import com.youneng.seal.api.resp.ObjectResults;
import com.youneng.troy.template.MysqlContainerBase;
import com.youneng.troy.template.RedisContainerBase;
import com.youneng.troy.template.web.ApplicationStarter;
import com.youneng.troy.template.web.param.DemoParam;
import com.youneng.troy.template.web.param.UserGetParam;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * @author : sunjianzhi
 * @version V1.0
 * @Project: ProjectTemplate
 * @Package com.youneng.troy.template.web.controller
 * @Description:
 * @date Date : 2022年12月08日 10:59
 */
//注意必须指定webEnvironment，才能真正启动web容器
@SpringBootTest(classes = ApplicationStarter.class, webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@ExtendWith({MysqlContainerBase.class, RedisContainerBase.class}) // 表明依赖的测试套件
//@DirtiesContext // 配置后则会启动一个新的spring容器
public class DemoControllerWithRealMvcTest {

    private final RestTemplate restTemplate = new RestTemplate();

    @Test
    public void should_get_correct_result() {
        HttpEntity<UserGetParam> request = new HttpEntity<>(new UserGetParam("1", null));
        ResponseEntity<ObjectResults> response = restTemplate.postForEntity("http://localhost:9915/template/api/demo/get/user", request, ObjectResults.class);
        ObjectResults results = response.getBody();
        assertEquals(200, response.getStatusCodeValue());
        assertEquals(1, results.getStatus());
    }

    @Test
    public void should_get_invalid_param_id_exception() {
        HttpEntity<DemoParam> request = new HttpEntity<>(new DemoParam(null, null,1));
        ResponseEntity<ObjectResults> response = restTemplate.postForEntity("http://localhost:9915/template/api/demo/hello", request, ObjectResults.class);
        ObjectResults results = response.getBody();
        assertEquals(200, response.getStatusCodeValue());
        assertEquals(0, results.getStatus());
        assertEquals("参数校验异常",results.getMessage());
    }

    @Test
    public void should_get_invalid_param_name_exception() {
        HttpEntity<DemoParam> request = new HttpEntity<>(new DemoParam(1L, "123456789101111",1));
        ResponseEntity<ObjectResults> response = restTemplate.postForEntity("http://localhost:9915/template/api/demo/hello", request, ObjectResults.class);
        ObjectResults results = response.getBody();
        assertEquals(200, response.getStatusCodeValue());
        assertEquals(0, results.getStatus());
        assertEquals("参数校验异常",results.getMessage());
    }

    @Test
    public void should_get_invalid_param_age_exception() {
        HttpEntity<DemoParam> request = new HttpEntity<>(new DemoParam(1L, "zhangsan",111));
        ResponseEntity<ObjectResults> response = restTemplate.postForEntity("http://localhost:9915/template/api/demo/hello", request, ObjectResults.class);
        ObjectResults results = response.getBody();
        assertEquals(200, response.getStatusCodeValue());
        assertEquals(0, results.getStatus());
        assertEquals("参数校验异常",results.getMessage());
    }

}
