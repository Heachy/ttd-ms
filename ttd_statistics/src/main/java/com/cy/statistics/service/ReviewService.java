package com.cy.statistics.service;

import com.cy.statistics.dao.Review;
import com.cy.statistics.vo.ReviewVO;

import java.util.List;

/**
 * @author Haechi
 */
public interface ReviewService {

    /**
     * 添加评价
     *
     * @param review 评价
     */
    void addReview(Review review);

    /**
     * 获取评价列表
     * @param receiverId 接单者id
     * @param page 页码
     * @param pageSize 页大小
     * @return 评价列表
     */
    List<ReviewVO> getReviewList(Long receiverId, int page, int pageSize);

    ReviewVO getReview(String taskId);

}
