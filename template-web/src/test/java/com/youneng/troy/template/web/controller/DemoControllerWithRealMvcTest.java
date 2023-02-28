package com.youneng.troy.template.web.controller;

import com.youneng.troy.template.MysqlContainerBase;
import com.youneng.troy.template.RedisContainerBase;
import com.youneng.troy.template.common.results.BaseStatusEnum;
import com.youneng.troy.template.common.results.ListObjectResults;
import com.youneng.troy.template.common.results.ObjectResults;
import com.youneng.troy.template.web.ApplicationStarter;
import com.youneng.troy.template.web.param.DemoParam;
import com.youneng.troy.template.web.param.UserGetParam;
import org.junit.jupiter.api.Named;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpEntity;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.web.client.ResponseErrorHandler;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

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

    private final RestTemplate restTemplate;

    {
        restTemplate = new RestTemplate();
        //自定义ErrorHandler，防止400、500错误码抛异常
        restTemplate.setErrorHandler(new ResponseErrorHandler() {
            @Override
            public boolean hasError(ClientHttpResponse response) {
                return false;
            }

            @Override
            public void handleError(ClientHttpResponse response) {

            }
        });
    }

    @Test
    public void should_get_correct_result() {
        HttpEntity<UserGetParam> request = new HttpEntity<>(new UserGetParam("1", null));
        ListObjectResults response = restTemplate.postForObject("http://localhost:9915/template/api/demo/get/user", request, ListObjectResults.class);
        List users = response.getData();
        assertNotNull(users);
        assertEquals(BaseStatusEnum.SUCCESS.getStatus(), response.getStatus());
        assertEquals(1, users.size());
    }

    @ParameterizedTest(name = "supporting {0}")
    @MethodSource
    public void should_get_invalid_param_exception(DemoParam demoParam) {
        HttpEntity<DemoParam> request = new HttpEntity<>(demoParam);
        ObjectResults<Void> response = restTemplate.postForObject("http://localhost:9915/template/api/demo/hello", request, ObjectResults.class);
        assertEquals(BaseStatusEnum.ERROR, response.getStatus());
    }

    public static Stream<Arguments> should_get_invalid_param_exception() {
        return Stream.of(Arguments.of(Named.of("id invalid", new DemoParam(null, null, 1))),
                Arguments.of(Named.of("name invalid", new DemoParam(1L, "123456789101111", 1))),
                Arguments.of(Named.of("age invalid", new DemoParam(1L, "zhangsan", 111))));
    }

//    @Test
//    public void should_get_invalid_param_id_exception() {
//        HttpEntity<DemoParam> request = new HttpEntity<>(new DemoParam(null, null, 1));
//        ObjectResults<Void> response = restTemplate.postForObject("http://localhost:9915/template/api/demo/hello", request, ObjectResults.class);
//        assertEquals(HttpStatus.BAD_REQUEST, response.getStatus());
//    }
//
//    @Test
//    public void should_get_invalid_param_name_exception() {
//        HttpEntity<DemoParam> request = new HttpEntity<>(new DemoParam(1L, "123456789101111", 1));
//        ResponseEntity<Void> response = restTemplate.postForEntity("http://localhost:9915/template/api/demo/hello", request, Void.class);
//        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
//    }
//
//    @Test
//    public void should_get_invalid_param_age_exception() {
//        HttpEntity<DemoParam> request = new HttpEntity<>(new DemoParam(1L, "zhangsan", 111));
//        ResponseEntity<Void> response = restTemplate.postForEntity("http://localhost:9915/template/api/demo/hello", request, Void.class);
//        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
//    }

}
