package com.cy.generated.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

/**
 * @author Haechi
 */
@Document(collection = "chat_msg")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChatMsg {
    @Id
    private String id;

    @Indexed
    private String taskId;

    private String role;

    private Boolean isRead;

    @Indexed
    private Long receiverId;

    private int msgNum;

    private SocketMsg lastMsg;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
    private Date time;
}
