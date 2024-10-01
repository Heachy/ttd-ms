package com.cy.ttd.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Haechi
 */
@Configuration
public class RabbitMqConfig {

    /**
     * 快递任务队列
     */
    @Bean
    public Queue ptQueue() {
        return new Queue("pt_queue",true,false,false,deadQueueArgs());
    }

    /**
     * 死信队列
     */
    @Bean
    public Queue deadQueue() {
        return new Queue("dead_queue", true, false, false);
    }

    /**
     * 接收者队列
     */
    @Bean
    public Queue receiverQueue() {
        Map<String, Object> map = deadQueueArgs();

        // 设置过期时间
        map.put("x-message-ttl", 5*60*1000);

        map.put("x-dead-letter-routing-key", "dead_id");
        return new Queue("receiver_queue", true, false, false,map);
    }
    /**
     * 死id队列
     */
    @Bean
    public Queue deadIdQueue() {
        return new Queue("dead_id_queue", true, false, false);
    }

    /**
     * 快递任务交换机
     */
    @Bean
    DirectExchange ptExchange() {
        return new DirectExchange("pt_exchange", true, false);
    }

    /**
     * 死信交换机
     */
    @Bean
    DirectExchange deadExchange() {
        return new DirectExchange("dead_exchange", true, false);
    }

    /**
     * 绑定 任务队列 到 正常交换机
     */
    @Bean
    public Binding ptBinding() {
        return BindingBuilder.bind(ptQueue()).to(ptExchange()).with("parcel_task");
    }

    /**
     * 绑定 死信队列 到 死信交换机
     */
    @Bean
    Binding deadRouteBinding() {
        return BindingBuilder.bind(deadQueue())
                .to(deadExchange())
                .with("dead_pt");
    }

    /**
     * 绑定 死id队列 到 死信交换机
     */
    @Bean
    Binding deadIdRouteBinding() {
        return BindingBuilder.bind(deadIdQueue())
                .to(deadExchange())
                .with("dead_id");
    }

    /**
     * 绑定 接收者队列 到 正常交换机
     */
    @Bean
    public Binding receiverBinding() {
        return BindingBuilder.bind(receiverQueue()).to(ptExchange()).with("receiver");
    }

    /**
     * 转发到 死信队列，配置参数
     */
    private Map<String, Object> deadQueueArgs() {
        Map<String, Object> map = new HashMap<>(2);
        // 绑定该队列到死信交换机
        map.put("x-dead-letter-exchange", "dead_exchange");
        map.put("x-dead-letter-routing-key", "dead_pt");
        return map;
    }
}
