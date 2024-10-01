package com.cy.statistics.service.impl;

import com.cy.statistics.dao.Review;
import com.cy.statistics.service.ReviewService;
import com.cy.statistics.service.ScoreService;
import com.cy.statistics.service.StatisticsService;
import com.cy.statistics.vo.ReviewVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author Haechi
 */
@Service
public class ReviewServiceImpl implements ReviewService {

    MongoTemplate mongoTemplate;

    StatisticsService statisticsService;

    ScoreService scoreService;

    private static final String COLLECTION_NAME="review";


    public ReviewServiceImpl(@Autowired MongoTemplate mongoTemplate,@Autowired StatisticsService statisticsService,@Autowired ScoreService scoreService)
    {
        this.mongoTemplate = mongoTemplate;
        this.statisticsService=statisticsService;
        this.scoreService=scoreService;
    }


    @Override
    public void addReview(Review review) {

        if(review.getStars()<3){
            statisticsService.incStatistics(review.getReceiverId(),"negativeReviews");
        } else if (review.getStars()>3) {
            statisticsService.incStatistics(review.getReceiverId(),"positiveReviews");
        }

        scoreService.incScore(review.getReceiverId(), review.getStars());

        mongoTemplate.save(review,COLLECTION_NAME);

    }

    @Override
    public List<ReviewVO> getReviewList(Long receiverId, int page, int pageSize) {

        if(page < 1) {
            page = 1;
        }
        if (pageSize < 1) {
            pageSize = 10;
        }

        Pageable pageable = PageRequest.of(page - 1, pageSize);

        Sort sort = Sort.by(Sort.Direction.DESC,"time");

        Criteria criteria = new Criteria();

        criteria.and("receiverId").is(receiverId);

        Query query = new Query(criteria);

        query.with(sort).with(pageable);

        return mongoTemplate.find(query, ReviewVO.class,COLLECTION_NAME);

    }

    @Override
    public ReviewVO getReview(String taskId) {
        Criteria criteria = new Criteria();

        criteria.and("taskId").is(taskId);

        return mongoTemplate.findOne(new Query(criteria),ReviewVO.class,COLLECTION_NAME);
    }
}
