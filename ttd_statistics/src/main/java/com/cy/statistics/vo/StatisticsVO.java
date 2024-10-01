package com.cy.statistics.vo;

import com.cy.statistics.dao.Statistics;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class StatisticsVO {
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

    private Double score;

    public StatisticsVO(Statistics statistics) {
        this.id = statistics.getId();
        this.completes = statistics.getCompletes();
        this.escapes = statistics.getEscapes();
        this.negativeReviews = statistics.getNegativeReviews();
        this.positiveReviews = statistics.getPositiveReviews();
        this.totalOrders = statistics.getTotalOrders();
        this.timeouts = statistics.getTimeouts();
    }
}
