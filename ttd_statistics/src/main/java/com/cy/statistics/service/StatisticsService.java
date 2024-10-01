package com.cy.statistics.service;


import com.cy.statistics.dao.Statistics;
import java.util.Optional;

/**
 * @author Lambda
 */
public interface StatisticsService
{
    /**
     * 获取统计信息
     * @param id 接单者id
     * @return 统计信息
     */
    Optional<Statistics> getStatistics(String id);
    /**
     * 插入统计信息
     * @param statistics 统计信息
     */
    void insertStatistics(Statistics statistics);

    /**
     * 创建统计信息
     * @param id 接单者id
     */
    void createStatistics(Long id);

    /**
     * 增加统计信息
     * @param id 接单者id
     * @param field 字段
     */
    void incStatistics(Long id,String field);
}
