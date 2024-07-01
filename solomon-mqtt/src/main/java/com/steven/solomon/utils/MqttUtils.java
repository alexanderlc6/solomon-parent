package com.steven.solomon.utils;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.lang.UUID;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.steven.solomon.annotation.Mqtt;
import com.steven.solomon.consumer.AbstractConsumer;
import com.steven.solomon.entity.MqttModel;
import com.steven.solomon.profile.MqttProfile;
import com.steven.solomon.profile.MqttProfile.MqttWill;
import com.steven.solomon.spring.SpringUtil;
import com.steven.solomon.utils.logger.LoggerUtils;
import com.steven.solomon.service.SendService;
import com.steven.solomon.verification.ValidateUtils;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import javax.annotation.Resource;

import org.eclipse.paho.client.mqttv3.*;
import org.slf4j.Logger;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.stereotype.Component;

@Configuration
public class MqttUtils implements SendService<MqttModel> {

  private Logger logger = LoggerUtils.logger(MqttUtils.class);

  private Map<String,MqttClient> clientMap = new HashMap<>();

  private Map<String,MqttConnectOptions> optionsMap = new HashMap<>();

  public Map<String, MqttConnectOptions> getOptionsMap() {
    return optionsMap;
  }

  public void putOptionsMap(String tenantCode, MqttConnectOptions options) {
    this.optionsMap.put(tenantCode,options);
  }

  public Map<String, MqttClient> getClientMap() {
    return clientMap;
  }

  public void putClient(String tenantCode,MqttClient client) {
    this.clientMap.put(tenantCode, client);
  }

  /**
   *   发送消息
   *   @param data 消息内容
   */
  @Override
  public void send(MqttModel data) throws Exception {
    // 获取客户端实例
    ObjectMapper mapper = new ObjectMapper();
    try {
      // 转换消息为json字符串
      String json = mapper.writeValueAsString(data);
      getClientMap().get(data.getTenantCode()).getTopic(data.getTopic()).publish(new MqttMessage(json.getBytes(StandardCharsets.UTF_8)));
    } catch (JsonProcessingException e) {
      logger.error(String.format("MQTT: 主题[%s]发送消息转换json失败", data.getTopic()));
    } catch (MqttException e) {
      logger.error(String.format("MQTT: 主题[%s]发送消息失败", data.getTopic()));
    }
  }

  @Override
  public void sendDelay(MqttModel data, long delay) throws Exception {
    send(data);
  }

  @Override
  public void sendExpiration(MqttModel data, long expiration) throws Exception {
    send(data);
  }

  /**
   * 订阅消息
   * @param tenantCode 租户编码
   * @param topic 主题
   * @param qos 消息质量
   * @param consumer 消费者
   */
  public void subscribe(String tenantCode,String topic,int qos, IMqttMessageListener consumer) throws MqttException {
    if(ValidateUtils.isEmpty(topic)){
      return;
    }
    getClientMap().get(tenantCode).subscribe(topic, qos,consumer);
  }

  /**
   * 订阅消息
   * @param client mqtt连接
   */
  public void subscribe(MqttClient client) throws MqttException {
    List<Object> clazzList = new ArrayList<>(SpringUtil.getBeansWithAnnotation(Mqtt.class).values());
    if (ValidateUtils.isNotEmpty(clazzList)) {
      for (Object abstractConsumer : clazzList) {
        Mqtt mqtt = AnnotationUtils.findAnnotation(abstractConsumer.getClass(), Mqtt.class);
        if (ValidateUtils.isEmpty(mqtt)) {
          continue;
        }
        for (String topic : mqtt.topics()) {
          client.subscribe(topic, mqtt.qos(), (IMqttMessageListener) BeanUtil.copyProperties(abstractConsumer,abstractConsumer.getClass(), (String) null));
        }
      }
    }
  }

  /**
   * 取消订阅
   * @param topic 主题
   */
  public void unsubscribe(String tenantCode,String[] topic) throws MqttException {
    if(ValidateUtils.isEmpty(topic)){
      return;
    }
    getClientMap().get(tenantCode).unsubscribe(topic);
  }

  /**
   * 关闭连接
   */
  public void disconnect(String tenantCode) throws MqttException {
    getClientMap().get(tenantCode).disconnect();
  }

  /**
   * 重新连接
   */
  public void reconnect(String tenantCode) throws MqttException {
    MqttClient client = getClientMap().get(tenantCode);
    if(!client.isConnected()){
      client.connect(getOptionsMap().get(tenantCode));
      subscribe(client);
    }
  }

  public MqttConnectOptions initMqttConnectOptions(MqttProfile mqttProfile) {
    MqttConnectOptions mqttConnectOptions = new MqttConnectOptions();
    mqttConnectOptions.setUserName(mqttProfile.getUserName());
    mqttConnectOptions.setPassword(mqttProfile.getPassword().toCharArray());
    mqttConnectOptions.setServerURIs(new String[]{mqttProfile.getUrl()});
    //设置同一时间可以发送的最大未确认消息数量
    mqttConnectOptions.setMaxInflight(mqttProfile.getMaxInflight());
    //设置超时时间
    mqttConnectOptions.setConnectionTimeout(mqttProfile.getCompletionTimeout());
    //设置自动重连
    mqttConnectOptions.setAutomaticReconnect(mqttProfile.getAutomaticReconnect());
    //cleanSession 设为 true;当客户端掉线时;服务器端会清除 客户端session;重连后 客户端会有一个新的session,cleanSession
    // 设为false，客户端掉线后 服务器端不会清除session，当重连后可以接收之前订阅主题的消息。当客户端上线后会接受到它离线的这段时间的消息
    mqttConnectOptions.setCleanSession(mqttProfile.getCleanSession());
    // 设置会话心跳时间 单位为秒   设置会话心跳时间 单位为秒 服务器会每隔1.5*20秒的时间向客户端发送心跳判断客户端是否在线，但这个方法并没有重连的机制
    mqttConnectOptions.setKeepAliveInterval(mqttProfile.getKeepAliveInterval());
    // 设置重新连接之间等待的最长时间
    mqttConnectOptions.setMaxReconnectDelay(mqttProfile.getMaxReconnectDelay());
    // 设置连接超时值,该值以秒为单位 0 禁用超时处理,这意味着客户端将等待，直到网络连接成功或失败.
    mqttConnectOptions.setConnectionTimeout(mqttProfile.getConnectionTimeout());
    // 设置执行器服务应等待的时间（以秒为单位）在强制终止之前终止.不建议更改除非您绝对确定需要，否则该值.
    mqttConnectOptions.setExecutorServiceTimeout(mqttProfile.getExecutorServiceTimeout());
    //设置遗嘱消息
    MqttWill will = mqttProfile.getWill();
    if (ValidateUtils.isNotEmpty(will)) {
      mqttConnectOptions.setWill(will.getTopic(), will.getMessage().getBytes(), will.getQos(), will.getRetained());
    }
    return mqttConnectOptions;
  }
}
