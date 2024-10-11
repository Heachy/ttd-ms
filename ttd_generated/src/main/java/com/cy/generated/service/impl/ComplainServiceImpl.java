package com.cy.generated.service.impl;

import com.cy.generated.domain.Complain;
import com.cy.generated.domain.ParcelTask;
import com.cy.generated.service.ComplainService;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author Heachy
 */
@Service
public class ComplainServiceImpl implements ComplainService {

    MongoTemplate mongoTemplate;

    public ComplainServiceImpl(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    @Override
    public boolean addComplain(Long complainantId, String taskId, String reason) {

        ParcelTask parcelTask = mongoTemplate.findById(taskId, ParcelTask.class, "parcel_task");

        if (parcelTask == null) {
            return false;
        }

        if(parcelTask.getPublisherId().equals(complainantId)){
            if(parcelTask.getReceiverId()==null){
                return false;
            }else{
                // 发布者投诉接收者
                // 查找是否已经投诉过
                Criteria criteria = new Criteria();

                criteria.and("taskId").is(taskId);

                criteria.and("complainantId").is(complainantId);

                criteria.and("respondentId").is(parcelTask.getReceiverId());

                Complain complain = mongoTemplate.findOne(new Query(criteria), Complain.class, "complain");

                if (complain != null) {
                    return false;
                }

                mongoTemplate.save(new Complain(null, taskId, complainantId, parcelTask.getReceiverId(), reason), "complain");

                return true;
            }
        }else if (parcelTask.getReceiverId().equals(complainantId)) {

            // 接收者投诉发布者
            // 查找是否已经投诉过
            Criteria criteria = new Criteria();

            criteria.and("taskId").is(taskId);

            criteria.and("complainantId").is(complainantId);

            criteria.and("respondentId").is(parcelTask.getPublisherId());

            Complain complain = mongoTemplate.findOne(new Query(criteria), Complain.class, "complain");

            if (complain != null) {
                return false;
            }
            mongoTemplate.save(new Complain(null, taskId, complainantId, parcelTask.getPublisherId(), reason), "complain");

            return true;
        }else{
            return false;
        }

    }

    @Override
    public List<Complain> getComplainList() {

        return mongoTemplate.findAll(Complain.class, "complain");
    }

}
