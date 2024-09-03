package com.steven.solomon.service;

import com.steven.solomon.annotation.MessageListener;
import org.springframework.amqp.core.*;
import org.springframework.stereotype.Service;

/**
 * RabbitMq广播队列注册
 */
@Service("fanoutMQService")
public class FanoutMQService extends AbstractMQService {

    @Override
    protected AbstractExchange initExchange(String exchangeName, MessageListener messageListener) {
        return new FanoutExchange(exchangeName);
    }

    @Override
    protected Binding initBinding(Queue queue, AbstractExchange exchange, String routingKey, MessageListener messageListener) {
        return BindingBuilder.bind(queue).to((FanoutExchange) exchange);
    }
}
