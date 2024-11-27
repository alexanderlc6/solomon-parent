package com.steven.solomon.service;

import com.steven.solomon.namingRules.FileNamingRulesGenerationService;
import com.steven.solomon.properties.FileChoiceProperties;
/**
 * 七牛云文件实现类
 */
public class KODOService extends S3Service {

  public KODOService(FileChoiceProperties properties, FileNamingRulesGenerationService fileNamingRulesGenerationService) {
    super(properties,fileNamingRulesGenerationService);
  }

}
