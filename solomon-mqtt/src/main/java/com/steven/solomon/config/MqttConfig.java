package com.steven.solomon.config;

import com.steven.solomon.profile.MqttProfile;
import com.steven.solomon.profile.TenantMqttProfile;
import com.steven.solomon.service.MqttInitService;
import com.steven.solomon.service.impl.DefaultMqttInitService;
import com.steven.solomon.spring.SpringUtil;
import com.steven.solomon.utils.MqttUtils;
import com.steven.solomon.verification.ValidateUtils;
import java.util.Map;
import java.util.Map.Entry;
import javax.annotation.PostConstruct;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.integration.annotation.IntegrationComponentScan;
import org.springframework.integration.channel.DirectChannel;
import org.springframework.messaging.MessageChannel;

@Configuration
@IntegrationComponentScan
@EnableConfigurationProperties(value={TenantMqttProfile.class,})
@Import(value = {MqttUtils.class})
public class MqttConfig {

  private final TenantMqttProfile profile;

  private final MqttUtils mqttUtils;

  public MqttConfig(TenantMqttProfile profile, ApplicationContext applicationContext, MqttUtils mqttUtils) {
    this.profile = profile;
    this.mqttUtils = mqttUtils;
    SpringUtil.setContext(applicationContext);
  }

  @PostConstruct
  public void afterPropertiesSet() throws Exception {
    Map<String,MqttProfile> tenantProfileMap = profile.getTenant();
    if(ValidateUtils.isEmpty(tenantProfileMap)){
      return ;
    }
    Map<String, MqttInitService> abstractMQMap = SpringUtil.getBeansOfType(MqttInitService.class);
    MqttInitService mqttInitService = ValidateUtils.isNotEmpty(abstractMQMap) ? abstractMQMap.values().stream().findFirst().get() : new DefaultMqttInitService(mqttUtils);
    for(Entry<String,MqttProfile> entry: tenantProfileMap.entrySet()){
      mqttInitService.initMqttClient(entry.getKey(),entry.getValue());
    }
  }

  @Bean
  @ConditionalOnMissingBean(MessageChannel.class)
  public MessageChannel mqttInputChannel() {
    return new DirectChannel();
  }

}
