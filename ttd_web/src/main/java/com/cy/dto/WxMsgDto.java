package com.cy.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "wx_msg")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class WxMsgDto {
    @Id
    private String id;

    private String username;

    private String avatar;
}
