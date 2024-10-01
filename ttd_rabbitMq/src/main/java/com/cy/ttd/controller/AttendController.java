package com.cy.ttd.controller;

import com.cy.common.api.CommonResult;
import com.cy.ttd.service.PtService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author Haechi
 */
@RestController
@RequestMapping("/attend")
public class AttendController {

    private final PtService ptService;

    public AttendController(PtService ptService) {
        this.ptService = ptService;
    }

    @RequestMapping("/open")
    public CommonResult<String> open() {

        System.out.println("开始点名");

        ptService.putMsg("点名已开启",5000);

        return CommonResult.success("点名已开启");
    }

    @RequestMapping("/get")
    public CommonResult<String> get() {

        System.out.println("开始点名");

        Object msg = ptService.getMsg();

        return CommonResult.success(msg.toString());
    }

}
