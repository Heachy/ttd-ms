package com.cy.controller;

import cn.dev33.satoken.stp.StpUtil;
import com.cy.common.api.CommonResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


/**
 * @author Heachy
 */
@RestController
@RequestMapping("/token")
public class TokenController {

    @PostMapping("/renew")
    public CommonResult<String> renew() {
        StpUtil.renewTimeout(3*24*60*60);

        return CommonResult.success("成功更新token时间");
    }
}
