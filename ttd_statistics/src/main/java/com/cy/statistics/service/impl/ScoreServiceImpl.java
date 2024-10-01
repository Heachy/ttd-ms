package com.cy.statistics.service.impl;

import com.cy.statistics.dao.Score;
import com.cy.statistics.service.ScoreService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;

/**
 * @author Haechi
 */
@Service
public class ScoreServiceImpl implements ScoreService {

    MongoTemplate mongoTemplate;

    private static final String COLLECTION_NAME="score";

    private static final String [] SCORE_ARR={"oneStar","twoStar","threeStar","fourStar","fiveStar"};

    public ScoreServiceImpl(@Autowired MongoTemplate mongoTemplate)
    {
        this.mongoTemplate = mongoTemplate;
    }


    @Override
    public void incScore(Long id,  byte stars) {

        Query query = new Query();

        Criteria criteria = Criteria.where("id").is(id.toString());

        query.addCriteria(criteria);

        mongoTemplate.updateFirst(query, new Update().inc(SCORE_ARR[stars-1], 1), Score.class, COLLECTION_NAME);

    }

    @Override
    public void createScore(Long id) {
        Score score = new Score(id.toString(),0,0,0,0,1);

        mongoTemplate.save(score,COLLECTION_NAME);
    }

    @Override
    public double getScore(Long id) {
        Score score = mongoTemplate.findById(id.toString(), Score.class, COLLECTION_NAME);

        if(score==null){
            return 0;
        }
        long total = score.getOneStar() + score.getTwoStar() * 2L + score.getThreeStar() * 3L + score.getFourStar() * 4L
                + score.getFiveStar() * 5L;
        long count = score.getOneStar() + score.getTwoStar() + score.getThreeStar() + score.getFourStar()
                + score.getFiveStar();

        return (double) total /count;
    }
}
