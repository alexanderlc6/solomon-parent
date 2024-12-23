package com.steven.solomon.properties;

import java.util.Map;

import com.steven.solomon.pojo.enums.SwitchModeEnum;
import org.springframework.boot.autoconfigure.mongo.MongoProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@ConfigurationProperties(prefix = "spring.data.mongodb")
public class TenantMongoProperties {

  public Map<String,MongoProperties> tenant;

  /**
   * mongo缓存模式（默认单库）
   */
  private SwitchModeEnum mode = SwitchModeEnum.NORMAL;

  //是否启用
  private boolean enabled = true;

  public boolean getEnabled() {
    return enabled;
  }

  public void setEnabled(boolean enabled) {
    this.enabled = enabled;
  }

  public SwitchModeEnum getMode() {
    return mode;
  }

  public void setMode(SwitchModeEnum mode) {
    this.mode = mode;
  }

  public Map<String, MongoProperties> getTenant() {
    return tenant;
  }

  public void setTenant(Map<String, MongoProperties> tenant) {
    this.tenant = tenant;
  }
}
