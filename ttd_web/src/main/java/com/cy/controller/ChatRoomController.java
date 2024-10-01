package com.cy.controller;

import cn.dev33.satoken.stp.StpUtil;
import com.cy.generated.mapper.TtdUserMapper;
import com.cy.generated.service.ChatMsgService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author Haechi
 */
@RestController
@RequestMapping("/chat")
public class ChatRoomController {

    ChatMsgService chatMsgService;

    TtdUserMapper ttdUserMapper;

    public ChatRoomController(ChatMsgService chatMsgService, TtdUserMapper ttdUserMapper) {
        this.chatMsgService = chatMsgService;
        this.ttdUserMapper = ttdUserMapper;
    }

    @GetMapping("/list")
    public Object getChatMsgList(int page, int pageSize) {
        Long id=ttdUserMapper.getUserIdByOpenId(StpUtil.getLoginId().toString());

        return chatMsgService.getChatMsgList(id, page, pageSize);
    }

//    @PostMapping("/test")
//    public void test(){
//        chatMsgService.test();
//    }
}
