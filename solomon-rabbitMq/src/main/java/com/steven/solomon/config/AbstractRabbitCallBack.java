package com.steven.solomon.config;

import com.steven.solomon.utils.logger.LoggerUtils;
import org.slf4j.Logger;
import org.springframework.amqp.core.ReturnedMessage;
import org.springframework.amqp.rabbit.connection.CorrelationData;

public abstract class AbstractRabbitCallBack {

    public final Logger logger = LoggerUtils.logger(AbstractRabbitCallBack.class);

    /**
     * 保存mq消费成功或失败后方法
     */
    public abstract void saveRabbitCallBack(CorrelationData correlationData, boolean ack, String cause);

    /**
     * 保存mq消息丢失方法
     */
    public abstract void saveReturnedMessage(ReturnedMessage returned);
}
