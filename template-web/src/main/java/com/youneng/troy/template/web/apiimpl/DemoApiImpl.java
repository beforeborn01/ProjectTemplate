package com.youneng.troy.template.web.apiimpl;

import com.youneng.troy.template.api.interfaces.DemoApi;
import com.youneng.troy.template.api.req.DemoReq;
import com.youneng.troy.template.api.resp.DemoResp;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author : sunjianzhi
 * @version V1.0
 * @Project: ProjectTemplate
 * @Package com.youneng.troy.template.web.apiimpl
 * @Description:
 * @date Date : 2022年10月13日 10:42
 */
@RestController
@RequestMapping("/template/feign/api/demo")
public class DemoApiImpl implements DemoApi {

    /**
     * 示例
     */
    @Override
    @PostMapping("/hello")
    public ResponseEntity<DemoResp> hello(@Validated @RequestBody DemoReq demoReq) {
        return ResponseEntity.ok(new DemoResp(demoReq.getId(), 10));
    }

    @Override
    @PostMapping("/error")
    public ResponseEntity<String> error(DemoReq demoReq) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("errorMsg");
    }
}
