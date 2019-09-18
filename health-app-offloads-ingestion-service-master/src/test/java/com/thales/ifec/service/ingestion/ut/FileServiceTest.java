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
import static org.mockito.BDDMockito.willDoNothing;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.PutObjectResult;
import com.thales.ifec.service.ingestion.IngestionException;
import com.thales.ifec.service.ingestion.configuration.AmazonS3Client;
import com.thales.ifec.service.ingestion.configuration.Producer;
import com.thales.ifec.service.ingestion.configuration.SftpConfig.UploadGateway;
import com.thales.ifec.service.ingestion.domain.OffloadType;
import com.thales.ifec.service.ingestion.domain.OffloadsMaster;
import com.thales.ifec.service.ingestion.domain.OffloadsMasterRepository;
import com.thales.ifec.service.ingestion.domain.OutputMessage;
import com.thales.ifec.service.ingestion.domain.RthmStatus;
import com.thales.ifec.service.ingestion.service.FileService;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Date;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.http.HttpStatus;
import org.springframework.messaging.MessageDeliveryException;
import org.springframework.messaging.MessagingException;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;



public class FileServiceTest {

  @Mock
  private AmazonS3 client;

  @Mock
  private AmazonS3Client s3client;

  @Mock
  private Producer producer;

  @Mock
  private OffloadsMasterRepository tvRepo;

  @Mock
  private UploadGateway mockGateway;

  @InjectMocks
  private FileService fileService;

  private MultipartFile multiPartFile;
  private static final String TEST_FILE_CONTENT = "test data";

  /**
   * Setting up test suite.
   * 
   */
  @Before
  public void setUp() {
    initMocks(this);

    given(s3client.getBucketName()).willReturn("test");
    given(s3client.getEndpoint()).willReturn("test");
    given(s3client.getClient()).willReturn(client);

    willDoNothing().given(producer).produceMsg(any(String.class), any(OffloadType.class));

    doReturn(new OffloadsMaster(1, 1, "Test.tgz", 34234, new Date(), "New", RthmStatus.PROCESSED,
        "tet", "ttt", "AAL234", "5c4ae97e50bc594772155402", new Date(), new Date(), "LAX", "JFK",
        true, new Date(), "reason", "remarks", "rtrt", "test", "test", "TEST")).when(tvRepo)
            .save(any(OffloadsMaster.class));

    doReturn(new PutObjectResult()).when(client).putObject(any(PutObjectRequest.class));
  }

  @Test
  public void tvPerformanceFileUpload_WhenSuccess() {
    given(tvRepo.findByFileName(any(String.class))).willReturn(null);

    multiPartFile = new MockMultipartFile("file", "TVPERF_20180620002641_AAL_N344PP_933.tgz",
        "application/octet-stream", TEST_FILE_CONTENT.getBytes());

    OutputMessage outputMsg = fileService.uploadFile(multiPartFile);
    assertThat(outputMsg.getCode()).isEqualTo(HttpStatus.OK.value());
    assertThat(outputMsg.getDescription()).containsIgnoringCase("uploaded successfully");
  }

  @Test
  public void tvPerformanceFileUploadEndTgzFileWith_WhenSuccess() {
    given(tvRepo.findByFileName(any(String.class))).willReturn(null);

    multiPartFile = new MockMultipartFile("file", "TVPERF_20180620002641_AAL_N344PP_933.TGZ",
        "application/octet-stream", TEST_FILE_CONTENT.getBytes());

    OutputMessage outputMsg = fileService.uploadFile(multiPartFile);
    assertThat(outputMsg.getCode()).isEqualTo(HttpStatus.OK.value());
    assertThat(outputMsg.getDescription()).containsIgnoringCase("uploaded successfully");
  }

  @Test
  public void fileUpload_Exception() throws IOException {
    multiPartFile = new MockMultipartFile("file", "", "application/octet-stream", new byte[0]);

    OutputMessage outputMsg = fileService.uploadFile(multiPartFile);
    assertThat(outputMsg.getCode()).isEqualTo(HttpStatus.CONFLICT.value());
    assertThat(outputMsg.getDescription()).contains("Exception occurred while reading the file");
  }

  @Test
  public void tvPerformanceFileUpload_FileNameWithInvalidDateFormat() {
    multiPartFile = new MockMultipartFile("file", "TVPERF_201213123_AAL.tgz",
        "application/octet-stream", TEST_FILE_CONTENT.getBytes());

    OutputMessage outputMsg = fileService.uploadFile(multiPartFile);
    assertThat(outputMsg.getCode()).isEqualTo(HttpStatus.OK.value());
    assertThat(outputMsg.getDescription()).containsIgnoringCase("uploaded successfully");
  }

  @Test
  public void tvPerformanceFileUpload_FileNameWithInvalidDate() {
    multiPartFile = new MockMultipartFile("file", "TVPERF_20121312300000_AAL.tgz",
        "application/octet-stream", TEST_FILE_CONTENT.getBytes());

    OutputMessage outputMsg = fileService.uploadFile(multiPartFile);
    assertThat(outputMsg.getCode()).isEqualTo(HttpStatus.OK.value());
    assertThat(outputMsg.getDescription()).containsIgnoringCase("uploaded successfully");
  }

  @Test
  public void tvPerformanceFileUpload_DateParsingError() {
    multiPartFile = new MockMultipartFile("file", "TVPERF_20120000300000_AAL.tgz",
        "application/octet-stream", TEST_FILE_CONTENT.getBytes());

    OutputMessage outputMsg = fileService.uploadFile(multiPartFile);
    assertThat(outputMsg.getCode()).isEqualTo(HttpStatus.OK.value());
    assertThat(outputMsg.getDescription()).containsIgnoringCase("uploaded successfully");
  }

  @Test
  public void tvPerformanceFileUpload_WithInvalidFileExtension() {
    multiPartFile = new MockMultipartFile("file", "TVPERF_201213123.txt",
        "application/octet-stream", TEST_FILE_CONTENT.getBytes());

    OutputMessage outputMsg = fileService.uploadFile(multiPartFile);
    assertThat(outputMsg.getCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
  }

  @Test
  public void tvPerformanceFileUpload_WithWrongFormat() {
    multiPartFile = new MockMultipartFile("file", "TVPERF_20180620002641_AAL_N344PP_933..txt",
        "application/octet-stream", TEST_FILE_CONTENT.getBytes());

    OutputMessage outputMsg = fileService.uploadFile(multiPartFile);
    assertThat(outputMsg.getCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    assertThat(outputMsg.getDescription()).contains("Invalid file format");
  }

  @Test
  public void tvPerformanceFileUpload_WhenFileAlreadyUploaded() {
    OffloadsMaster offloadMaster = new OffloadsMaster();
    String testFileName = "TVPERF_20180620002641_AAL_N344PP_933.tgz";
    offloadMaster.setFileName(testFileName);
    given(tvRepo.findByFileName(testFileName)).willReturn(offloadMaster);

    multiPartFile = new MockMultipartFile("file", testFileName, "application/octet-stream",
        TEST_FILE_CONTENT.getBytes());

    OutputMessage outputMsg = fileService.uploadFile(multiPartFile);

    assertThat(outputMsg.getCode()).isEqualTo(HttpStatus.CONFLICT.value());
    assertThat(outputMsg.getDescription()).contains("File has already been uploaded");
  }

  @Test
  public void tvPerformanceFileUploadWithInvalidAirline() {
    multiPartFile = new MockMultipartFile("file", "TVPERF_N344PP_933.tgz",
        "application/octet-stream", TEST_FILE_CONTENT.getBytes());

    OutputMessage message = fileService.uploadFile(multiPartFile);

    assertThat(message.getCode()).isEqualTo(HttpStatus.OK.value());
    assertThat(message.getDescription()).containsIgnoringCase("uploaded successfully");
  }

  @SuppressWarnings("unchecked")
  @Test
  public void biteFileUpload() throws InterruptedException, ExecutionException, IOException {
    String testFileName = "BITE_20190116022144_OMA_C-FSDB_ACA971.tgz";
    File offloadfile = new File("src/test/resources", testFileName);

    final Future<String> mockResponse = mock(Future.class);
    when(mockResponse.get()).thenReturn(testFileName);

    multiPartFile = new MockMultipartFile("file", testFileName, "application/octet-stream",
        new FileInputStream(offloadfile));

    OutputMessage outputMsg = fileService.uploadFile(multiPartFile);
    assertThat(outputMsg.getCode()).isEqualTo(HttpStatus.OK.value());
    assertThat(outputMsg.getDescription()).containsIgnoringCase("uploaded successfully");
  }

  @SuppressWarnings("unchecked")
  @Test
  public void biteFileUploadEndTgz() throws InterruptedException, ExecutionException, IOException {
    String testFileName = "BITE_20190216022144_OMA_C-FSDB_ACA971.TGZ";
    File offloadfile = new File("src/test/resources", testFileName);

    final Future<String> mockResponse = mock(Future.class);
    when(mockResponse.get()).thenReturn(testFileName);

    multiPartFile = new MockMultipartFile("file", testFileName, "application/octet-stream",
        new FileInputStream(offloadfile));

    OutputMessage outputMsg = fileService.uploadFile(multiPartFile);
    assertThat(outputMsg.getCode()).isEqualTo(HttpStatus.OK.value());
    assertThat(outputMsg.getDescription()).containsIgnoringCase("uploaded successfully");
  }

  @SuppressWarnings("unchecked")
  @Test
  public void biteFileUpload_WithValidTailSign()
      throws IOException, InterruptedException, ExecutionException {
    String testFileName = "BITE_20181021223237_OMA_A40-BH_WY163.tgz";
    File offloadfile = new File("src/test/resources", testFileName);

    final Future<String> mockResponse = mock(Future.class);
    when(mockResponse.get()).thenReturn(testFileName);

    given(mockGateway.uploadToBiteServer(any(File.class))).willReturn(mockResponse);

    multiPartFile = new MockMultipartFile("file", testFileName, "application/octet-stream",
        new FileInputStream(offloadfile));

    OutputMessage outputMsg = fileService.uploadFile(multiPartFile);
    assertThat(outputMsg.getCode()).isEqualTo(HttpStatus.OK.value());
    assertThat(outputMsg.getDescription()).containsIgnoringCase("uploaded successfully");
  }

  @SuppressWarnings("unchecked")
  @Test
  public void biteFileUpload_Iseries()
      throws IOException, InterruptedException, ExecutionException {
    String testFileName = "BITE_032603_A7-AHG.tgz";
    File offloadfile = new File("src/test/resources", testFileName);

    final Future<String> mockResponse = mock(Future.class);
    when(mockResponse.get()).thenReturn(testFileName);

    given(mockGateway.uploadToBiteServer(any(File.class))).willReturn(mockResponse);

    multiPartFile = new MockMultipartFile("file", testFileName, "application/octet-stream",
        new FileInputStream(offloadfile));

    OutputMessage outputMsg = fileService.uploadFile(multiPartFile);
    assertThat(outputMsg.getCode()).isEqualTo(HttpStatus.OK.value());
    assertThat(outputMsg.getDescription()).containsIgnoringCase("uploaded successfully");
  }

  @SuppressWarnings("unchecked")
  @Test
  public void biteFileUpload_WithRthmData()
      throws IOException, InterruptedException, ExecutionException {
    String testFileName = "BITE_20190116022144_OMA_C-FSDB_ACA971.tgz";
    File offloadfile = new File("src/test/resources", testFileName);

    final Future<String> mockResponse = mock(Future.class);
    when(mockResponse.get()).thenReturn(testFileName);

    given(mockGateway.uploadToBiteServer(any(File.class))).willReturn(mockResponse);

    multiPartFile = new MockMultipartFile("file", testFileName, "application/octet-stream",
        new FileInputStream(offloadfile));

    OutputMessage outputMsg = fileService.uploadFile(multiPartFile);
    assertThat(outputMsg.getCode()).isEqualTo(HttpStatus.OK.value());
    assertThat(outputMsg.getDescription()).containsIgnoringCase("uploaded successfully");
  }

  @SuppressWarnings("unchecked")
  @Test
  public void biteFileUploadEndTgzWithRthmData()
      throws IOException, InterruptedException, ExecutionException {
    String testFileName = "BITE_20190216022144_OMA_C-FSDB_ACA971.TGZ";
    File offloadfile = new File("src/test/resources", testFileName);

    final Future<String> mockResponse = mock(Future.class);
    when(mockResponse.get()).thenReturn(testFileName);

    given(mockGateway.uploadToBiteServer(any(File.class))).willReturn(mockResponse);

    multiPartFile = new MockMultipartFile("file", testFileName, "application/octet-stream",
        new FileInputStream(offloadfile));

    OutputMessage outputMsg = fileService.uploadFile(multiPartFile);
    assertThat(outputMsg.getCode()).isEqualTo(HttpStatus.OK.value());
    assertThat(outputMsg.getDescription()).containsIgnoringCase("uploaded successfully");
  }

  @Test
  public void biteFileUpload_WithInvalidExtension() throws IOException {
    multiPartFile = new MockMultipartFile("file", "BITE_20181021223237_OMA_WY163.RAR",
        "application/octet-stream", TEST_FILE_CONTENT.getBytes());

    OutputMessage outputMsg = fileService.uploadFile(multiPartFile);
    assertThat(outputMsg.getCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
  }

  @Test
  public void biteFileUpload_WhenSftpException() {
    given(mockGateway.uploadToBiteServer(any(File.class)))
        .willThrow(new MessagingException("Failed to process offload",
            new MessageDeliveryException("The destination file already exists")));

    multiPartFile = new MockMultipartFile("file", "BITE_20181021223237_OMA_A40-BH_WY163.tgz",
        "application/octet-stream", TEST_FILE_CONTENT.getBytes());

    OutputMessage outputMsg = fileService.uploadFile(multiPartFile);

    assertThat(outputMsg.getCode()).isEqualTo(HttpStatus.CONFLICT.value());
    assertThat(outputMsg.getDescription())
        .containsIgnoringCase("The destination file already exists");
  }

  @Test
  public void biteFileUpload_WhenOtherException() {

    given(mockGateway.uploadToBiteServer(any(File.class)))
        .willThrow(new IllegalArgumentException("Failed to process offload",
            new IngestionException("The destination file already exists")));

    multiPartFile = new MockMultipartFile("file", "BITE_20181021223237_OMA_A40-BH_WY163.tgz",
        "application/octet-stream", TEST_FILE_CONTENT.getBytes());

    OutputMessage outputMsg = fileService.uploadFile(multiPartFile);

    assertThat(outputMsg.getCode()).isEqualTo(HttpStatus.CONFLICT.value());
    assertThat(outputMsg.getMessage()).containsIgnoringCase("Failed to process offload");
  }

  @Test
  public void biteFileUploadWhenIoException() throws IOException {
    String testFileName = "BITE_20190110042824_AAL_INVALID.tgz";
    File offloadfile = new File("src/test/resources", testFileName);

    multiPartFile = new MockMultipartFile("file", "BITE_20181021223237_OMA_A40-BH_WY163.tgz",
        "application/octet-stream", new FileInputStream(offloadfile));

    OutputMessage outputMsg = fileService.uploadFile(multiPartFile);

    assertThat(outputMsg.getCode()).isEqualTo(HttpStatus.CONFLICT.value());
    assertThat(outputMsg.getDescription())
        .containsIgnoringCase("Exception occurred while extracting the Bite offload file");
  }

  @Test
  public void testFileUpload_WithUnsupportedUploadType() {
    multiPartFile = new MockMultipartFile("file", "OFFLOAD_20181121103045_OMA_A409BH_WY163.tgz",
        "application/octet-stream", TEST_FILE_CONTENT.getBytes());

    OutputMessage outputMsg = fileService.uploadFile(multiPartFile);

    assertThat(outputMsg.getCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    assertThat(outputMsg.getDescription()).containsIgnoringCase("Upload type not supported");
  }

  @Test
  public void kaLogsFileUpload_WithInvalidFileExtension() {
    multiPartFile = new MockMultipartFile("file", "Ka_United_201213123.txt",
        "application/octet-stream", TEST_FILE_CONTENT.getBytes());

    OutputMessage outputMsg = fileService.uploadFile(multiPartFile);
    assertThat(outputMsg.getCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
  }

  @Test
  public void kaLogsFileUpload_WithWrongFormat() {
    multiPartFile = new MockMultipartFile("file", "Ka_United_N37437_UAL513_20190410053545..txt",
        "application/octet-stream", TEST_FILE_CONTENT.getBytes());

    OutputMessage outputMsg = fileService.uploadFile(multiPartFile);
    assertThat(outputMsg.getCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    assertThat(outputMsg.getDescription()).contains("Invalid file format");
  }

  @SuppressWarnings("unchecked")
  @Test
  public void kaLogsFileUpload() throws IOException, InterruptedException, ExecutionException {

    String testFileName = "Ka_United_N37437_UAL513_20190410053545.zip";
    final File offloadfile = new File("src/test/resources", testFileName);
    final Future<String> mockResponse = mock(Future.class);

    when(mockResponse.get()).thenReturn(testFileName);
    given(s3client.getKaLogsClient()).willReturn(client);
    given(s3client.getKalogsbucketName()).willReturn("test");

    multiPartFile = new MockMultipartFile("file", testFileName, "application/octet-stream",
        new FileInputStream(offloadfile));

    OutputMessage outputMsg = fileService.uploadFile(multiPartFile);
    assertThat(outputMsg.getCode()).isEqualTo(HttpStatus.OK.value());
  }

}
