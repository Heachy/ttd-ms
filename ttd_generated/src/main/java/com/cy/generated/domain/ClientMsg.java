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
public class ClientMsg {
    private String name;

    private String phoneEnd;

    private Integer building;

    private Integer layer;

    private String addressDetail;
}
