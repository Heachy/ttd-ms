package com.cy.ttd.service;

import org.springframework.amqp.core.MessagePostProcessor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

/**
 * @author Haechi
 */
@Service
public class ReceiverService {
    private final RabbitTemplate rabbitTemplate;

    public ReceiverService(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    /**
     *  1: 定义交换机
     */
    private final String exchangeName = "pt_exchange";
    /**
     *  2: 路由key
     */
    private final String routeKey = "receiver";

    public void putMsg(String msg) {

        rabbitTemplate.convertAndSend(exchangeName,routeKey, msg);
    }

    public Object getMsg(){
        return  rabbitTemplate.receiveAndConvert("receiver_queue");
    }
}
