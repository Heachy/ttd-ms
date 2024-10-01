package com.cy.controller;


import cn.dev33.satoken.stp.StpUtil;
import com.alibaba.fastjson.JSON;
import com.cy.common.api.CommonResult;
import com.cy.dto.WxMsgDto;
import com.cy.generated.domain.ClientMsg;
import com.cy.generated.domain.StudentMsg;
import com.cy.generated.domain.UserMsg;
import com.cy.generated.mapper.TtdUserMapper;
import com.cy.generated.service.TtdUserService;
import com.cy.service.WxService;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * @author Haechi
 */
@RestController
@RequestMapping("/user")
public class UserController {
    TtdUserService ttdUserService;

    TtdUserMapper ttdUserMapper;

    WxService wxService;

    public UserController(TtdUserService ttdUserService, TtdUserMapper ttdUserMapper, WxService wxService) {
        this.ttdUserService = ttdUserService;
        this.ttdUserMapper = ttdUserMapper;
        this.wxService = wxService;
    }

    @PostMapping("/msg")
    public CommonResult<String> msg(@RequestBody ClientMsg msg) {
        ttdUserService.addClientMsg(msg);

        return CommonResult.success("success");
    }

    @PostMapping("/msg/update")
    public CommonResult<String> updateMsg(@RequestBody Map<String,Object> map) {
        Object msg = map.get("msg");

        ClientMsg clientMsg = JSON.parseObject(JSON.toJSONString(msg), ClientMsg.class);

        System.out.println(clientMsg);

        ttdUserService.updateClientMsg(clientMsg,(int)map.get("index"));

        return CommonResult.success("success");
    }

    @GetMapping("/msg")
    public CommonResult<UserMsg> msg() {
        return CommonResult.success(ttdUserService.getUserMsg());
    }

    @DeleteMapping("/msg")
    public CommonResult<String> deleteMsg(@RequestBody Map<String,Object> map) {
        ttdUserService.deleteClientMsg((int)map.get("index"));

        return CommonResult.success("success");
    }

    @PostMapping("/msg/default")
    public CommonResult<String> msgDefault(@RequestBody Map<String,Object> map) {
        ttdUserService.setDefaultMsg((int)map.get("index"));

        return CommonResult.success("success");
    }

    @PostMapping("/certificate")
    public CommonResult<String> certificate(@RequestBody StudentMsg studentMsg) {
        Map<String, Object> map = ttdUserService.certificate(studentMsg);

        if(map.get("isSuccess").equals(false)){
            return CommonResult.failed(map.get("msg").toString());
        }

        return CommonResult.success("认证成功");
    }

    @GetMapping("/certificate")
    public CommonResult<StudentMsg> getCertificate() {
        StudentMsg certificate = ttdUserService.getCertificate();

        if (certificate == null) {
            return CommonResult.failed("未认证");
        }

        return CommonResult.success(certificate);
    }

    @PostMapping("/wxMsg")
    public CommonResult<String> wxMsg(@RequestBody Map<String,Object> map) {
        Long id=ttdUserMapper.getUserIdByOpenId(StpUtil.getLoginId().toString());


        wxService.uploadMsg(id.toString(),map.get("name").toString(),map.get("avatar").toString());

        return CommonResult.success("上传成功");
    }

    @GetMapping("/wxMsg")
    public CommonResult<WxMsgDto> getWxMsg() {
        Long id=ttdUserMapper.getUserIdByOpenId(StpUtil.getLoginId().toString());

        return CommonResult.success(wxService.getMsg(id.toString()));
    }
}
