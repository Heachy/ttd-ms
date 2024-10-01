package com.cy.service;


import com.cy.common.api.CommonResult;
import com.cy.dto.WxMsgDto;

import java.util.Map;

public interface WxService {
    CommonResult<Map<String, Object>> login(String code);

    boolean uploadMsg(String id,String name, String avatar);

    WxMsgDto getMsg(String id);
}
