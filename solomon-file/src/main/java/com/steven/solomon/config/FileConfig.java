package com.steven.solomon.config;

import com.steven.solomon.code.FileErrorCode;
import com.steven.solomon.constant.code.BaseCode;
import com.steven.solomon.constant.code.BaseExceptionCode;
import com.steven.solomon.enums.FileChoiceEnum;
import com.steven.solomon.logger.LoggerUtils;
import com.steven.solomon.namingRules.DateNamingRulesGenerationService;
import com.steven.solomon.namingRules.FileNamingRulesGenerationService;
import com.steven.solomon.namingRules.OriginalNamingRulesGenerationService;
import com.steven.solomon.namingRules.SnowflakeNamingRulesGenerationService;
import com.steven.solomon.namingRules.UUIDNamingRulesGenerationService;
import com.steven.solomon.properties.FileChoiceProperties;
import com.steven.solomon.service.BOSService;
import com.steven.solomon.service.COSService;
import com.steven.solomon.service.DefaultService;
import com.steven.solomon.service.FileServiceInterface;
import com.steven.solomon.service.MinIoService;
import com.steven.solomon.service.OBSService;
import com.steven.solomon.service.OSSService;
import com.steven.solomon.utils.I18nUtils;
import org.slf4j.Logger;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

@Component
public class FileConfig {

  private final Logger logger = LoggerUtils.logger(FileConfig.class);

  @Bean
  @ConditionalOnMissingBean(FileServiceInterface.class)
  public FileServiceInterface fileService(FileChoiceProperties fileProperties){
    logger.info("选择 {} 文件服务", fileProperties.getChoice());
    switch (fileProperties.getChoice()) {
      case MINIO:
        return new MinIoService(fileProperties);
      case OSS:
        return new OSSService(fileProperties);
      case OBS:
        return new OBSService(fileProperties);
      case COS:
        return new COSService(fileProperties);
      case BOS:
        return new BOSService(fileProperties);
      default:
        return new DefaultService();
    }
  }

  @Bean
  @ConditionalOnMissingBean(FileNamingRulesGenerationService.class)
  public FileNamingRulesGenerationService fileNamingMethodService(FileChoiceProperties fileProperties){
    logger.info("选择 {} 命名规则", fileProperties.getFileNamingMethod());
    switch (fileProperties.getFileNamingMethod()) {
      case DATE:
        return new DateNamingRulesGenerationService();
      case UUID:
        return new UUIDNamingRulesGenerationService();
      case SNOWFLAKE:
        return new SnowflakeNamingRulesGenerationService();
      default:
        return new OriginalNamingRulesGenerationService();
    }
  }
}
