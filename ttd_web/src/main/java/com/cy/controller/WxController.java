package com.cy.controller;

import com.cy.common.api.CommonResult;
import com.cy.service.impl.WxServiceImpl;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/wx")
public class WxController {
    @Value("${mytag}")

    private String mytag;

    WxServiceImpl wxService;

    public WxController(WxServiceImpl wxService) {
        this.wxService = wxService;
    }


    @PostMapping("/login")
    public CommonResult<Map<String,Object>> callback(@RequestBody Map<String,Object> msg){

        return wxService.login(msg.get("code").toString());
    }

    @PostMapping("/login/temp")
    public CommonResult<Map<String,Object>> temp(@RequestBody Map<String,Object> msg){
        System.out.println(mytag);

        return wxService.loginSe(msg.get("openid").toString());
    }

    @PostMapping("/test")
    public CommonResult<Map<String,Object>> test(String username,String password){
        Map<String,Object> map=new HashMap<>();

        map.put("username",username);

        map.put("password",password);

        return CommonResult.success(map);
    }

//    @PostMapping("/addMsg")
//    public CommonResult<String> test2(){
//        wxService.addMsg();
//
//        return CommonResult.success("map");
//    }
}
