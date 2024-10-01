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
public class IncidentalMsg {

    /**
     * 订单id
     */
    private String taskId;

    /**
     * 订单类型
     */
    private String type;

    /**
     * 取件码
     */
    private String code;

    /**
     * 手机尾号
     */
    private String phone;

    /**
     * 取件人姓名
     */
    private String name;
    /**
     * 送货的详细地址
     */
    private String address;

    /**
     * 取件身份码
     */
    private String picture;
}
