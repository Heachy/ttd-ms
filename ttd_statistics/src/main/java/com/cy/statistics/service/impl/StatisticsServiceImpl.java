package com.cy.statistics.service.impl;

import com.cy.statistics.dao.Statistics;
import com.cy.statistics.service.StatisticsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * @author Lambda
 */
@Service
public class StatisticsServiceImpl implements StatisticsService
{
    MongoTemplate mongoTemplate;

    public StatisticsServiceImpl(@Autowired MongoTemplate mongoTemplate)
    {
        this.mongoTemplate = mongoTemplate;
    }

    @Override
    public Optional<Statistics> getStatistics(String id)
    {
        return Optional.ofNullable(mongoTemplate.findById(id, Statistics.class, "statistics"));
    }

    @Override
    public void insertStatistics(Statistics statistics) {

    }

    @Override
    public void createStatistics(Long id) {
        Statistics statistics = new Statistics();
        statistics.setId(id.toString());
        statistics.setEscapes(0);
        statistics.setTimeouts(0);
        statistics.setTotalOrders(0);
        statistics.setCompletes(0);
        statistics.setPositiveReviews(0);
        statistics.setNegativeReviews(0);
        mongoTemplate.save(statistics, "statistics");
    }

    @Override
    public void incStatistics(Long id, String field) {
        Query query = new Query();

        Criteria criteria = Criteria.where("id").is(id.toString());

        query.addCriteria(criteria);

        mongoTemplate.updateFirst(query, new Update().inc(field, 1), Statistics.class, "statistics");

    }


}
