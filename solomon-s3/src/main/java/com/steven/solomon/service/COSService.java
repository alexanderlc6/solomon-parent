package com.steven.solomon.service;

import com.baidubce.Protocol;
import com.qcloud.cos.COSClient;
import com.qcloud.cos.ClientConfig;
import com.qcloud.cos.auth.BasicCOSCredentials;
import com.qcloud.cos.auth.COSCredentials;
import com.qcloud.cos.http.HttpProtocol;
import com.qcloud.cos.model.*;
import com.qcloud.cos.region.Region;
import com.steven.solomon.lambda.Lambda;
import com.steven.solomon.namingRules.FileNamingRulesGenerationService;
import com.steven.solomon.properties.FileChoiceProperties;
import com.steven.solomon.verification.ValidateUtils;
import java.io.InputStream;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;
import org.springframework.web.multipart.MultipartFile;

/**
 * 腾讯云文件实现类
 */
public class COSService extends AbstractFileService {

  private final COSClient client;

  public COSService(FileChoiceProperties properties, FileNamingRulesGenerationService fileNamingRulesGenerationService) {
    super(properties,fileNamingRulesGenerationService);
    this.client = initClient(properties);
  }

  private static COSClient initClient(FileChoiceProperties properties){
    COSCredentials credentials  = new BasicCOSCredentials(properties.getAccessKey(), properties.getSecretKey());
    ClientConfig configuration = null;
    if(ValidateUtils.isNotEmpty(properties.getRegionName())){
      Region         region       = new Region(properties.getRegionName());
      configuration = new ClientConfig(region);
    } else {
      configuration = new ClientConfig();
    }
    configuration.setConnectionTimeout(properties.getConnectionTimeout());
    configuration.setSocketTimeout(properties.getSocketTimeout());
    boolean             isHttps         = properties.getEndpoint().contains("https");
    configuration.setHttpProtocol(isHttps ? HttpProtocol.https : HttpProtocol.http);
    return new COSClient(credentials, configuration);
  }

  @Override
  protected void multipartUpload(MultipartFile file, String bucketName, long fileSize, String uploadId, String filePath,int partCount)
      throws Exception {
    // 分割文件并上传分片
    List<PartETag> partETags = new ArrayList<>();
    for (int i = 0; i < partCount; i++) {
      InputStream inputStream = file.getInputStream();
      // 跳到每个分块的开头
      long skipBytes = partSize * i;
      inputStream.skip(skipBytes);

      // 计算每个分块的大小
      long size = partSize < fileSize - skipBytes ?
                  partSize : fileSize - skipBytes;

      // 创建上传分片请求
      UploadPartRequest uploadPartRequest = new UploadPartRequest();
      uploadPartRequest.setBucketName(bucketName);
      uploadPartRequest.setKey(filePath);
      uploadPartRequest.setUploadId(uploadId);
      uploadPartRequest.setInputStream(inputStream);
      uploadPartRequest.setPartSize(size);
      uploadPartRequest.setPartNumber(i + 1);
      // 上传分片
      UploadPartResult uploadPartResult = client.uploadPart(uploadPartRequest);
      partETags.add(uploadPartResult.getPartETag());
    }
    // 完成分片上传
    CompleteMultipartUploadRequest compRequest = new CompleteMultipartUploadRequest(
        bucketName, filePath, uploadId, partETags);

    client.completeMultipartUpload(compRequest);
  }

  @Override
  public boolean bucketExists(String bucketName) throws Exception {
    return client.doesBucketExist(bucketName);
  }

  @Override
  protected void upload(MultipartFile file, String bucketName, String filePath) throws Exception  {
    client.putObject(new PutObjectRequest(bucketName,filePath,file.getInputStream(),null));
  }

  @Override
  protected void delete(String bucketName, String filePath) throws Exception  {
    client.deleteObject(bucketName, filePath);
  }

  @Override
  protected String shareUrl(String bucketName, String filePath, long expiry) throws Exception  {
    return client.generatePresignedUrl(bucketName,filePath,new Date(System.currentTimeMillis()+ TimeUnit.SECONDS.toMillis(expiry))).toString();
  }

  @Override
  protected InputStream getObject(String bucketName, String filePath) throws Exception  {
    return client.getObject(bucketName, filePath).getObjectContent();
  }

  @Override
  protected void createBucket(String bucketName) throws Exception  {
    client.createBucket(bucketName);
  }

  @Override
  protected boolean checkObjectExist(String bucketName, String objectName) throws Exception {
    return client.doesObjectExist(bucketName,objectName);
  }

  @Override
  protected void copyFile(String sourceBucket, String targetBucket, String sourceObjectName, String targetObjectName)
      throws Exception {
    client.copyObject(sourceBucket,sourceObjectName,targetBucket,targetObjectName);
  }

  @Override
  public List<String> listObjects(String bucketName,String key) throws Exception {
    if(ValidateUtils.isEmpty(bucketName) || !bucketExists(bucketName)){
      return new ArrayList<>();
    }
    ObjectListing response = ValidateUtils.isEmpty(key) ? client.listObjects(bucketName) : client.listObjects(bucketName,key);
    return Lambda.toList(response.getObjectSummaries(), COSObjectSummary::getKey);
  }

  @Override
  public String initiateMultipartUploadTask(String bucketName, String objectName) throws Exception {
    InitiateMultipartUploadRequest initiateMultipartUploadRequest = new InitiateMultipartUploadRequest(bucketName,objectName);
    InitiateMultipartUploadResult  initResponse                   = client.initiateMultipartUpload(initiateMultipartUploadRequest);
    return initResponse.getUploadId();
  }

  @Override
  protected void abortMultipartUpload(String uploadId, String bucketName, String filePath) throws Exception {
    client.abortMultipartUpload(new AbortMultipartUploadRequest(bucketName,filePath,uploadId));
  }

  @Override
  public void deleteBucket(String bucketName) throws Exception {
    if(ValidateUtils.isEmpty(bucketName)){
      logger.error("deleteBucket方法中,请求参数为空,删除桶失败");
    }
    client.deleteBucket(bucketName);
  }

  @Override
  public List<String> getBucketList() throws Exception {
    List<Bucket> listBucketsResponse = client.listBuckets();
    List<String> bucketNameList      = new ArrayList<>();
    if(ValidateUtils.isNotEmpty(listBucketsResponse)){
      for(Bucket bucket : listBucketsResponse){
        bucketNameList.add(bucket.getName());
      }
    }
    return bucketNameList;
  }
}
