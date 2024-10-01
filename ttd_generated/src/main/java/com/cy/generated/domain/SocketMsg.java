package com.cy.generated.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author Haechi
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class SocketMsg {

    /**
     * 消息类型 1.文本 2.图片url 3.详细信息 0.任务结束无法继续聊天
     */
    private Integer type;

    private Long senderId;

    private Object msg;
}
