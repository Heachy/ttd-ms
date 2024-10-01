package com.cy.generated.service;

import com.cy.generated.domain.SocketHistory;
import com.cy.generated.domain.SocketMsg;

/**
 * @author Haechi
 */
public interface SocketHistoryService {

    /**
     * 根据id获取socket历史记录
     * @param id id
     * @return socket历史记录
     */
    SocketHistory getSocketHistoryById(String id);

    /**
     * 保存socket历史记录
     * @param taskId 任务id
     * @param socketMsg socket消息
     * @return 是否成功
     */
    boolean saveSocketHistory(String taskId,SocketMsg socketMsg);

    /**
     * 发送图片
     * @param taskId 任务id
     * @param url 图片url
     */
    void sendPicture(String taskId,String url);

}
