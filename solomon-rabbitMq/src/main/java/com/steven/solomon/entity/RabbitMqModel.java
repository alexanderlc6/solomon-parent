package com.steven.solomon.entity;


import com.steven.solomon.pojo.entity.BaseMq;

import java.io.Serializable;
import java.util.Map;

/**
 * 基础发送MQ基类
 */
public class RabbitMqModel<T> extends BaseMq<T> {

    private static final long serialVersionUID = -5799719724717056583L;
    /**
     * 交换机
     */
    private String exchange;
    /**
     * 路由规则
     */
    private String routingKey;
    /**
     * 设置头部属性
     */
    private Map<String, Object> headers;

    /**
     * 消息是否持久
     */
    private boolean messagePersistent = true;

    /**
     * 响应队列
     */
    private String replyTo;

    /**
     * 消息优先级
     */
    private Integer priority;

    public Integer getPriority() {
        return priority;
    }

    public void setPriority(Integer priority) {
        this.priority = priority;
    }

    public String getReplyTo() {
        return replyTo;
    }

    public void setReplyTo(String replyTo) {
        this.replyTo = replyTo;
    }

    public boolean getMessagePersistent() {
        return messagePersistent;
    }

    public void setMessagePersistent(boolean messagePersistent) {
        this.messagePersistent = messagePersistent;
    }

    public Map<String, Object> getHeaders() {
        return headers;
    }

    public void setHeaders(Map<String, Object> headers) {
        this.headers = headers;
    }

    public RabbitMqModel() {
        super();
    }

    public RabbitMqModel(T body) {
        super(body);
    }

    public RabbitMqModel(String exchange, T body) {
        super(body);
        this.exchange = exchange;
    }

    public RabbitMqModel(String exchange, String routingKey, T body) {
        super(body);
        this.exchange = exchange;
        this.routingKey = routingKey;
    }

    public RabbitMqModel(String exchange, String routingKey, T body, boolean messagePersistent) {
        super(body);
        this.exchange = exchange;
        this.routingKey = routingKey;
        this.messagePersistent = messagePersistent;
    }

    public String getExchange() {
        return exchange;
    }

    public void setExchange(String exchange) {
        this.exchange = exchange;
    }

    public String getRoutingKey() {
        return routingKey;
    }

    public void setRoutingKey(String routingKey) {
        this.routingKey = routingKey;
    }

}
