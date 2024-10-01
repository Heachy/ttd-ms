package com.cy.generated.service.impl;

import cn.dev33.satoken.stp.StpUtil;
import com.cy.generated.domain.SocketHistory;
import com.cy.generated.domain.SocketMsg;
import com.cy.generated.mapper.TtdUserMapper;
import com.cy.generated.service.SocketHistoryService;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;

/**
 * @author Haechi
 */
@Service
public class SocketHistoryServiceImpl implements SocketHistoryService {
    MongoTemplate mongoTemplate;

    TtdUserMapper ttdUserMapper;

    public SocketHistoryServiceImpl(MongoTemplate mongoTemplate , TtdUserMapper ttdUserMapper) {
        this.mongoTemplate = mongoTemplate;

        this.ttdUserMapper = ttdUserMapper;
    }
    @Override
    public SocketHistory getSocketHistoryById(String id) {
        return mongoTemplate.findById(id,SocketHistory.class,"socket_history");
    }

    @Override
    public boolean saveSocketHistory(String taskId,SocketMsg socketMsg) {
        SocketHistory history = mongoTemplate.findById(taskId, SocketHistory.class, "socket_history");

        if(history != null&&"ing".equals(history.getStatus())){
            Query query = Query.query(Criteria.where("_id").is(taskId));

            history.getSocketMsgList().add(socketMsg);

            Update update = Update.update("socketMsgList",history.getSocketMsgList());

            mongoTemplate.updateFirst(query,update,"socket_history");

            return true;
        }else{
            return false;
        }
    }

    @Override
    public void sendPicture(String taskId, String url) {
        SocketHistory history = mongoTemplate.findById(taskId, SocketHistory.class, "socket_history");

        if(history != null){
            Long id=ttdUserMapper.getUserIdByOpenId(StpUtil.getLoginId().toString());

            Query query = Query.query(Criteria.where("_id").is(taskId));

            history.getSocketMsgList().add(new SocketMsg(2,id,url));

            Update update = Update.update("socketMsgList",history.getSocketMsgList());

            mongoTemplate.updateFirst(query,update,"socket_history");
        }
    }
}
