package com.cy.ttd.service;

import org.springframework.amqp.rabbit.annotation.*;
import org.springframework.stereotype.Service;

/**
 * @author Haechi
 */
@RabbitListener(
        bindings = @QueueBinding(
                value = @Queue(value = "dead_queue",autoDelete = "false"),
                exchange = @Exchange(value = "dead_exchange")
))
@Service
public class DeadQueService {

    @RabbitHandler
    public void receive(String msg) {
        System.out.println("过期任务：" + msg);
    }
}
