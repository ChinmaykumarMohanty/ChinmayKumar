/*
 * Copyright 2018-2019 Thales and/or its affiliates. All rights reserved.
 *  
 *   NOTICE - THE INFORMATION CONTAINED HEREIN IS PROPRIETARY AND CONFIDENTIAL
 *   TO THALES AVIONICS, INC. (THALES) IN WHOLE OR IN PART AND SHALL NOT BE
 *   USED OR DISCLOSED IN WHOLE OR IN PART WITHOUT FIRST OBTAINING THE WRITTEN
 *   PERMISSION OF THALES.
 */

package com.thales.ifec.service.ingestion.configuration;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;


@Component
public class AmazonS3Client {

  @Value("${amazonProperties.endpointUrl}")
  private String endpointUrl;

  @Value("${amazonProperties.bucketName}")
  private String bucketName;

  @Value("${amazonProperties.kalogsbucketName}")
  private String kaLogsBucketName;

  @Value("${amazonProperties.accessKey}")
  private String accessKey;

  @Value("${amazonProperties.secretKey}")
  private String secretKey;


  @Value("${amazonProperties.kalogs.accessKey}")
  private String kaLogsAccessKey;

  @Value("${amazonProperties.kalogs.secretKey}")
  private String kaLogsSecretKey;


  @Value("${amazonProperties.region}")
  private String region;

  /**
   * To get AWS access key and secret key.
   * 
   * @return AmazonS3
   */
  public AmazonS3 getClient() {
    BasicAWSCredentials creds = new BasicAWSCredentials(this.accessKey, this.secretKey);
    return AmazonS3ClientBuilder.standard().withCredentials(new AWSStaticCredentialsProvider(creds))
        .withRegion(this.region).build();
  }

  /**
   * To get KA Logs access key and secret key.
   * 
   * @return AmazonS3
   */
  public AmazonS3 getKaLogsClient() {
    BasicAWSCredentials creds = new BasicAWSCredentials(this.kaLogsAccessKey, this.kaLogsSecretKey);
    return AmazonS3ClientBuilder.standard().withCredentials(new AWSStaticCredentialsProvider(creds))
        .withRegion(this.region).build();
  }


  public String getBucketName() {
    return this.bucketName;
  }

  public String getEndpoint() {
    return this.endpointUrl;
  }

  public String getKalogsbucketName() {
    return kaLogsBucketName;
  }
}
