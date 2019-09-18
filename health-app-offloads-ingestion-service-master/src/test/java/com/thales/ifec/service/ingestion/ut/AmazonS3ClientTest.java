/*
 * Copyright 2018-2019 Thales and/or its affiliates. All rights reserved.
 *  
 *   NOTICE - THE INFORMATION CONTAINED HEREIN IS PROPRIETARY AND CONFIDENTIAL
 *   TO THALES AVIONICS, INC. (THALES) IN WHOLE OR IN PART AND SHALL NOT BE
 *   USED OR DISCLOSED IN WHOLE OR IN PART WITHOUT FIRST OBTAINING THE WRITTEN
 *   PERMISSION OF THALES.
 */

package com.thales.ifec.service.ingestion.ut;

import com.amazonaws.services.s3.AmazonS3;
import com.thales.ifec.service.ingestion.configuration.AmazonS3Client;
import org.assertj.core.api.Assertions;
import org.junit.Before;
import org.junit.Test;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.util.ReflectionTestUtils;


@TestPropertySource({"file:src/test/resources/application.properties"})
public class AmazonS3ClientTest {

  private AmazonS3Client s3client = new AmazonS3Client();

  /**
   * Setting up test suite.
   * 
   */
  @Before
  public void setUp() {
    ReflectionTestUtils.setField(s3client, "endpointUrl", "https://s3.console.aws.amazon.com");
    ReflectionTestUtils.setField(s3client, "bucketName", "health-payloads-dev");
    ReflectionTestUtils.setField(s3client, "kaLogsBucketName", "ka-logs-dev");
    ReflectionTestUtils.setField(s3client, "accessKey", "accessKey");
    ReflectionTestUtils.setField(s3client, "secretKey", "secretKey");
    ReflectionTestUtils.setField(s3client, "kaLogsAccessKey", "kaLogsAccessKey");
    ReflectionTestUtils.setField(s3client, "kaLogsSecretKey", "kaLogsSecretKey");
    ReflectionTestUtils.setField(s3client, "region", "us-west-1");

  }

  @Test
  public void testGetClient() {
    AmazonS3 awsclient = s3client.getClient();
    Assertions.assertThat(awsclient.getRegionName()).isEqualTo("us-west-1");

  }

  @Test
  public void testGetKaClient() {
    AmazonS3 awsclient = s3client.getKaLogsClient();
    Assertions.assertThat(awsclient.getRegionName()).isEqualTo("us-west-1");

  }

  @Test
  public void testGetBucketName() {
    Assertions.assertThat(s3client.getBucketName()).isEqualTo("health-payloads-dev");
  }

  @Test
  public void testGetKaLogsBucketName() {
    Assertions.assertThat(s3client.getKalogsbucketName()).isEqualTo("ka-logs-dev");
  }

  @Test
  public void testGetEndpointUrl() {
    Assertions.assertThat(s3client.getEndpoint()).isEqualTo("https://s3.console.aws.amazon.com");
  }
}
