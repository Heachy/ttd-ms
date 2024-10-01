package com.cy.statistics.service;


/**
 * @author Haechi
 */
public interface ScoreService {

    /**
     * 增加评分
     * @param id 接单者id
     * @param stars 评分
     */
    void incScore(Long id, byte stars);

    /**
     * 创建评分
     * @param id 接单者id
     */
    void createScore(Long id);

    /**
     * 获取评分
     * @param id 接单者id
     * @return 评分
     */
    double getScore(Long id);

}
