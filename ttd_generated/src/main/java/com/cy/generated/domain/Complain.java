package com.cy.generated.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "complain")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Complain {

    @Id
    private String id;

    @Indexed
    private String taskId;

    private Long complainantId;

    private Long respondentId;

    private String reason;
}
