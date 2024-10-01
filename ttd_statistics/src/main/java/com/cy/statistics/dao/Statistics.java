package com.cy.statistics.dao;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.io.Serializable;

/**
 * @author Lambda
 * @date 2023/11/12
 */
@Document(collection = "statistics")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Statistics implements Serializable
{
    /**
     * 用户id
     */
    @Id
    private String id;

    /**
     * 逃单数
     */
    private Integer escapes;

    /**
     * 超时单数
     */
    private Integer timeouts;

    /**
     * 总接单数
     */
    private Integer totalOrders;

    /**
     * 完成单数
     */
    private Integer completes;

    /**
     * 好评数
     */
    private Integer positiveReviews;

    /**
     * 差评数
     */
    private Integer negativeReviews;
}
