package com.cy.websocket.service.impl;

import com.alibaba.fastjson.JSON;
import com.cy.generated.domain.SocketMsg;
import com.cy.websocket.controller.InformWebsocketController;
import com.cy.websocket.service.InformSocketService;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Set;

/**
 * @author Haechi
 */
@Service
public class InformSocketServiceImpl implements InformSocketService {
    private final RedisTemplate<String, Object> redisTemplate;

    public InformSocketServiceImpl(RedisTemplate<String, Object> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    @Override
    public void sendPtInform(String taskId, String from, int building, int layer) {

        for(int i=layer;i<=10;i++){
            String key=from+"_"+building+"_"+i;

            Set<Object> members = redisTemplate.opsForSet().members(key);

            if (members != null) {
                for (Object userId : members) {
                    InformWebsocketController.sendUserTo(JSON.toJSONString(new SocketMsg(3,0L,taskId)), userId.toString());
                }
            }
        }

    }
}
