package com.steven.solomon.service;

import com.steven.solomon.properties.FileChoiceProperties;

/**
 *  鲨鱼云存储文件实现类
 */
public class SharktechService extends S3Service {

  public SharktechService(FileChoiceProperties properties) {
    super(properties);
  }

}