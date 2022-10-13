package com.youneng.troy.template.web.util;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpMethod;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * @author : sunjianzhi
 * @version V1.0
 * @Project: ProjectTemplate
 * @Package com.youneng.troy.template.web.util
 * @Description:
 * @date Date : 2022年10月11日 10:47
 */
public class HttpTest {

    @Test
    public void test() {
        System.out.println(HttpMethod.GET.toString());
    }

    @Test
    public void testGroupBy() {
        long start = System.currentTimeMillis();
        List<Person> list = new ArrayList<>();
        for (int i = 0; i < 50000; i++) {
            list.add(new Person((long)(Math.random() * 10000), "name" + Math.random() * 10000, "desc---------" + Math.random() * 100000));
        }
        Map<Long, List<Person>> map = list.stream().collect(Collectors.groupingBy(Person::getId));
        System.out.println(map.size());
        System.out.println("use time=" + (System.currentTimeMillis() - start));
    }

    @Data
    @AllArgsConstructor
    class Person {
        private Long id;
        private String name;
        private String desc;
    }

}
