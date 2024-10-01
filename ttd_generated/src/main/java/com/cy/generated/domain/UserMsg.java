package com.cy.generated.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

/**
 * @author Haechi
 */
@Document(collection = "user_msg")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserMsg {
    @Id
    private String id;

    private Integer defaultIndex;

    private List<ClientMsg> msgList;
}
