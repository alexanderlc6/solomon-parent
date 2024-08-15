package com.steven.solomon.config;

import com.steven.solomon.annotation.Mqtt;
import com.steven.solomon.init.AbstractMessageLineRunner;
import com.steven.solomon.profile.MqttProfile;
import com.steven.solomon.profile.TenantMqttProfile;
import com.steven.solomon.service.MqttInitService;
import com.steven.solomon.service.impl.DefaultMqttInitService;
import com.steven.solomon.spring.SpringUtil;
import com.steven.solomon.utils.MqttUtils;
import com.steven.solomon.verification.ValidateUtils;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Configuration
@EnableConfigurationProperties(value={TenantMqttProfile.class,})
@Import(value = {MqttUtils.class})
public class MqttConfig extends AbstractMessageLineRunner {

    private final TenantMqttProfile profile;

    private final MqttUtils mqttUtils;

    public MqttConfig(TenantMqttProfile profile, ApplicationContext applicationContext, MqttUtils mqttUtils) {
        this.profile = profile;
        this.mqttUtils = mqttUtils;
        SpringUtil.setContext(applicationContext);
    }

    @Override
    public void init(List<Object> clazzList) throws Exception {
        Map<String,MqttProfile> tenantProfileMap = profile.getTenant();
        if(ValidateUtils.isEmpty(tenantProfileMap)){
            logger.debug("AbstractMessageLineRunner:没有MQTT配置");
            return;
        }
        if(ValidateUtils.isNotEmpty(clazzList)){
            logger.debug("AbstractMessageLineRunner:没有MQTT消费者");
            return;
        }
        Map<String, MqttInitService> abstractMQMap = SpringUtil.getBeansOfType(MqttInitService.class);
        MqttInitService mqttInitService = ValidateUtils.isNotEmpty(abstractMQMap) ? abstractMQMap.values().stream().findFirst().get() : new DefaultMqttInitService(mqttUtils);
        for(Map.Entry<String,MqttProfile> entry: tenantProfileMap.entrySet()){
            mqttInitService.initMqttClient(entry.getKey(),entry.getValue(), clazzList);
        }
    }

    @Override
    public List<Object> getQueueClazzList() {
        return new ArrayList<>(SpringUtil.getBeansWithAnnotation(Mqtt.class).values());
    }
}
