/*
 * Copyright 2018-2019 Thales and/or its affiliates. All rights reserved.
 *  
 *   NOTICE - THE INFORMATION CONTAINED HEREIN IS PROPRIETARY AND CONFIDENTIAL
 *   TO THALES AVIONICS, INC. (THALES) IN WHOLE OR IN PART AND SHALL NOT BE
 *   USED OR DISCLOSED IN WHOLE OR IN PART WITHOUT FIRST OBTAINING THE WRITTEN
 *   PERMISSION OF THALES.
 */

package com.thales.ifec.service.ingestion.ut;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.MockitoAnnotations.initMocks;

import com.thales.ifec.service.ingestion.domain.OutputMessage;
import com.thales.ifec.service.ingestion.service.FileService;
import com.thales.ifec.service.ingestion.web.OffloadIngestionController;
import java.io.IOException;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

public class OffloadIngestionControllerTest {

  @InjectMocks
  private OffloadIngestionController ingestionController;

  @Mock
  private FileService fileService;

  @Mock
  private MultipartFile file;

  @Before
  public void setUp() {
    initMocks(this);
    
    given(file.getOriginalFilename()).willReturn("test/offloda/file");
  }

  @Test
  public void testSuccessScenario() throws IOException {
    OutputMessage message = new OutputMessage(200, "Success", "File uploaded successfully");
    given(fileService.uploadFile(file)).willReturn(message);

    ResponseEntity<OutputMessage> msg = ingestionController.upload(file);
    assertThat(msg.getStatusCodeValue()).isEqualTo(HttpStatus.OK.value());
  }

  @Test
  public void testFailureScenario() throws IOException {
    OutputMessage message =
        new OutputMessage(400, "Validation for the file failed", "Invalid file or Missing param..");
    given(fileService.uploadFile(file)).willReturn(message);

    ResponseEntity<OutputMessage> msg = ingestionController.upload(file);
    assertThat(msg.getStatusCodeValue()).isEqualTo(HttpStatus.BAD_REQUEST.value());
  }

  @Test
  public void biteOffload_WhenSuccess() throws IOException {
    OutputMessage message = new OutputMessage(200, "Success", "File uploaded successfully");
    given(fileService.uploadFile(file)).willReturn(message);

    ResponseEntity<OutputMessage> msg = ingestionController.upload(file);

    assertThat(msg.getStatusCodeValue()).isEqualTo(HttpStatus.OK.value());
    assertThat(msg.getBody().getDescription()).containsIgnoringCase("success");
  }

  @Test
  public void biteOffload_WhenFailure() throws IOException {
    OutputMessage message =
        new OutputMessage(400, "Unable to offload BITE file", "File already exists");
    given(fileService.uploadFile(file)).willReturn(message);

    ResponseEntity<OutputMessage> msg = ingestionController.upload(file);

    assertThat(msg.getStatusCodeValue()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    assertThat(msg.getBody().getMessage()).containsIgnoringCase("Unable to offload BITE file");
    assertThat(msg.getBody().getDescription()).containsIgnoringCase("File already exists");
  }

}
