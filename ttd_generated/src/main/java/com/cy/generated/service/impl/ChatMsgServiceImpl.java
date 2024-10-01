package com.cy.generated.service.impl;

import com.cy.generated.domain.ChatMsg;
import com.cy.generated.domain.ParcelTask;
import com.cy.generated.service.ChatMsgService;
import com.cy.generated.vo.ChatMsgVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author Haechi
 */
@Service
public class ChatMsgServiceImpl implements ChatMsgService {

    MongoTemplate mongoTemplate;

    private static final String COLLECTION_NAME = "chat_msg";

    public ChatMsgServiceImpl(@Autowired MongoTemplate mongoTemplate)
    {
        this.mongoTemplate = mongoTemplate;
    }

    @Override
    public void addChatMsg(ChatMsg chatMsg) {
        Criteria criteria = new Criteria();

        criteria.and("taskId").is(chatMsg.getTaskId());

        criteria.and("receiverId").is(chatMsg.getReceiverId());

        Query query = new Query(criteria);

        ChatMsg msg = mongoTemplate.findOne(query, ChatMsg.class);

        if(msg!=null) {
            msg.setMsgNum(msg.getMsgNum() + chatMsg.getMsgNum());
            msg.setLastMsg(chatMsg.getLastMsg());
            msg.setTime(chatMsg.getTime());
            msg.setIsRead(chatMsg.getIsRead());
            mongoTemplate.save(msg, COLLECTION_NAME);
        }else{
            mongoTemplate.save(chatMsg, COLLECTION_NAME);
        }
    }

    @Override
    public ChatMsg getChatMsg(String taskId, Long receiverId) {
        Criteria criteria = new Criteria();

        criteria.and("taskId").is(taskId);

        criteria.and("receiverId").is(receiverId);

        Query query = new Query(criteria);

        return mongoTemplate.findOne(query, ChatMsg.class,COLLECTION_NAME);
    }

    @Override
    public List<ChatMsgVO> getChatMsgList(Long receiverId,int page,int pageSize) {
        if(page < 1) {
            page = 1;
        }
        if (pageSize < 1) {
            pageSize = 10;
        }

        Pageable pageable = PageRequest.of(page - 1, pageSize);

        Sort sort = Sort.by(Sort.Direction.DESC,"time").and(Sort.by(Sort.Direction.DESC,"msgNum"));

        Criteria criteria = new Criteria();

        criteria.and("receiverId").is(receiverId);

        Query query = new Query(criteria);

        query.with(sort).with(pageable);

        List<ChatMsgVO> msgVOList = mongoTemplate.find(query, ChatMsgVO.class, COLLECTION_NAME);

        for (ChatMsgVO chatMsgVO : msgVOList) {
            if ("publisher".equals(chatMsgVO.getRole())) {
                chatMsgVO.setRole("骑手");
            } else {
                chatMsgVO.setRole("委托方");
            }
        }

        return msgVOList;
    }

    @Override
    public void clearChatMsg(String taskId, Long receiverId) {
        Criteria criteria = new Criteria();

        criteria.and("taskId").is(taskId);

        criteria.and("receiverId").is(receiverId);

        Query query = new Query(criteria);

        Update update = new Update();

        update.set("msgNum",0);

        update.set("isRead",true);

        mongoTemplate.updateFirst(query,update,COLLECTION_NAME);
    }

    @Override
    public void test() {
        List<ChatMsg> chatMsgs = mongoTemplate.findAll(ChatMsg.class, COLLECTION_NAME);

        for (ChatMsg chatMsg : chatMsgs) {
            ParcelTask parcelTask = mongoTemplate.findById(chatMsg.getTaskId(), ParcelTask.class, "parcel_task");

            if (parcelTask != null) {
                if (parcelTask.getReceiverId().equals(chatMsg.getReceiverId())) {
                    chatMsg.setRole("receiver");
                } else {
                    chatMsg.setRole("publisher");
                }
            }
            mongoTemplate.save(chatMsg, COLLECTION_NAME);
        }
    }
}
