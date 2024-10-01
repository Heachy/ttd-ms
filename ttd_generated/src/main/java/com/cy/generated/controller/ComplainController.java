package com.cy.generated.controller;

import cn.dev33.satoken.stp.StpUtil;
import com.cy.common.api.CommonResult;
import com.cy.generated.domain.Complain;
import com.cy.generated.mapper.TtdUserMapper;
import com.cy.generated.service.ComplainService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/complain")
public class ComplainController {

    ComplainService complainService;

    TtdUserMapper ttdUserMapper;

    public ComplainController(ComplainService complainService, TtdUserMapper ttdUserMapper) {
        this.complainService = complainService;
        this.ttdUserMapper = ttdUserMapper;
    }

    @PostMapping("/add")
    public CommonResult<String> addComplain(@RequestBody Complain complain) {

        Long id=ttdUserMapper.getUserIdByOpenId(StpUtil.getLoginId().toString());

        boolean result = complainService.addComplain(id, complain.getTaskId(), complain.getReason());

        if(result) {
            return CommonResult.success("投诉成功");
        }else {
            return CommonResult.failed("投诉失败");
        }
    }

    @GetMapping("/list")
    public CommonResult<List<Complain>> getComplainList() {
        return CommonResult.success(complainService.getComplainList());
    }
}
