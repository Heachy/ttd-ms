package com.cy.generated.service;

import com.cy.generated.domain.ChatMsg;
import com.cy.generated.vo.ChatMsgVO;

import java.util.List;

/**
 * @author Haechi
 */
public interface ChatMsgService {

    /**
     * 添加聊天记录
     *
     * @param chatMsg 聊天记录
     */
    void addChatMsg(ChatMsg chatMsg);

    /**
     * 获取聊天记录
     * @param taskId     任务id
     * @param receiverId 接收者id
     * @return 聊天记录
     */
    ChatMsg getChatMsg(String taskId, Long receiverId);

    /**
     * 获取聊天记录列表
     * @param receiverId 接收者id
     * @param page       页码
     * @param pageSize   页大小
     * @return 聊天记录列表
     */
    List<ChatMsgVO> getChatMsgList(Long receiverId,int page,int pageSize);

    /**
     * 清除聊天记录
     * @param taskId     任务id
     * @param receiverId 接收者id
     */
    void clearChatMsg(String taskId, Long receiverId);


    void test();
}
