package com.cy.ttd.service;

import com.cy.websocket.controller.InformWebsocketController;
import org.springframework.amqp.rabbit.annotation.*;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Set;

/**
 * @author Haechi
 */
@RabbitListener(
        bindings = @QueueBinding(
                value = @Queue(value = "dead_id_queue",autoDelete = "false"),
                exchange = @Exchange(value = "dead_exchange")
        ))
@Service
public class DeadIdQueService {
    private final RedisTemplate<String, Object> redisTemplate;

    public DeadIdQueService(RedisTemplate<String, Object> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    @RabbitHandler
    public void receive(String msg) {
        System.out.println("过期任务：" + msg);

        Object o = redisTemplate.opsForValue().get(msg);

        if (o != null) {
            System.out.println("骑手：" + msg+"还在接单");
        } else {
            System.out.println("骑手：" + msg+"已经不在接单");

            Set<Object> members = redisTemplate.opsForSet().members(msg+"Set");

            if (members != null) {
                for (Object member : members) {
                    redisTemplate.opsForSet().remove(member.toString(),msg);
                }

                redisTemplate.delete(msg+"Set");
            }
            InformWebsocketController.close(msg);
        }

    }
}
