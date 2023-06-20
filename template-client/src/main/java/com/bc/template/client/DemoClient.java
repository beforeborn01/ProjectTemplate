package com.bc.template.client;

import com.bc.template.api.interfaces.DemoApi;
import org.springframework.beans.factory.annotation.Autowired;
public class DemoClient {

    @Autowired
    private DemoApi demoApi;

    //调用template-api的hello方法
    public void hello() {

    }
}
