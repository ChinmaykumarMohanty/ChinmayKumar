/*
 * Copyright 2018-2019 Thales and/or its affiliates. All rights reserved.
 *  
 *   NOTICE - THE INFORMATION CONTAINED HEREIN IS PROPRIETARY AND CONFIDENTIAL
 *   TO THALES AVIONICS, INC. (THALES) IN WHOLE OR IN PART AND SHALL NOT BE
 *   USED OR DISCLOSED IN WHOLE OR IN PART WITHOUT FIRST OBTAINING THE WRITTEN
 *   PERMISSION OF THALES.
 */

package com.thales.ifec.service.ingestion.it;

import static org.mockito.BDDMockito.given;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.requestParameters;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.securityContext;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.thales.ifec.service.ingestion.OffloadIngestionApplication;
import com.thales.ifec.service.ingestion.domain.OutputMessage;
import com.thales.ifec.service.ingestion.service.FileService;
import com.thales.ifec.service.ingestion.util.OffloadIngestionTestConfiguration;
import com.thales.ifec.service.ingestion.util.WithMockAdminUser;
import com.thales.ifec.service.ingestion.util.WithMockNonAdminUser;
import com.thales.ifec.service.ingestion.web.OffloadIngestionController;
import java.io.FileNotFoundException;
import java.io.IOException;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.restdocs.JUnitRestDocumentation;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.restdocs.payload.FieldDescriptor;
import org.springframework.restdocs.request.ParameterDescriptor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers;
import org.springframework.security.web.FilterChainProxy;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;



@RunWith(SpringRunner.class)
@SpringBootTest(
    classes = {OffloadIngestionApplication.class, OffloadIngestionTestConfiguration.class},
    webEnvironment = RANDOM_PORT, properties = {"okta.client.token=FAKE_TEST_TOKEN",
        "okta.client.orgUrl=http://localhost:${wiremock.server.port}",})
@AutoConfigureTestDatabase
@AutoConfigureRestDocs
public class OffloadIngestionApiTest {

  @Rule
  public JUnitRestDocumentation restDocumentation =
      new JUnitRestDocumentation("target/generated-snippets");

  @Mock
  private FileService fileService;

  @InjectMocks
  private OffloadIngestionController controller;

  @Autowired
  FilterChainProxy springSecurityFilterChain;

  @Autowired
  private WebApplicationContext webApplicationContext;

  private MockMvc mockMvc;

  private MockMultipartFile multiFile;

  /**
   * Setting up test suite.
   * 
   */
  @Before
  public void setUp() throws FileNotFoundException, IOException {
    initMocks(this);
    this.mockMvc = MockMvcBuilders.standaloneSetup(controller)
        .apply(SecurityMockMvcConfigurers.springSecurity(springSecurityFilterChain))
        .apply(documentationConfiguration(this.restDocumentation)).build();

    multiFile = new MockMultipartFile("file", "TVPERF_20180620002641_AAL_N344PP_933.tgz",
        "application/octet-stream", "test data".getBytes());
  }

  @Test
  @WithMockAdminUser
  public void post_ApiDocumentation() throws Exception {

    OutputMessage message = new OutputMessage(200, "Success", "File uploaded successfully");
    given(fileService.uploadFile(multiFile)).willReturn(message);

    FieldDescriptor[] responseFieldsDescriptor = getReponseFieldsDescriptor();
    ParameterDescriptor[] requestParametersDescriptor = getRequestParametersDescriptor();

    // act and assert
    this.mockMvc
        .perform(RestDocumentationRequestBuilders.fileUpload("/api/v1/upload").file(multiFile)
            .param("file", "TVPERF_20180620002641_AAL_N344PP_933.tgz")
            .with(securityContext(SecurityContextHolder.getContext())))
        .andExpect(status().isOk())
        .andDo(document("upload-file", requestParameters(requestParametersDescriptor),
            responseFields(responseFieldsDescriptor)));
  }

  @Test
  @WithMockNonAdminUser
  public void upload_WithNormalUser() throws Exception {

    OutputMessage message = new OutputMessage(200, "Success", "File uploaded successfully");
    given(fileService.uploadFile(multiFile)).willReturn(message);

    // act and assert
    this.mockMvc.perform(RestDocumentationRequestBuilders.fileUpload("/api/v1/upload")
        .file(multiFile).param("file", "TVPERF_20180620002641_AAL_N344PP_933.tgz")
        .with(securityContext(SecurityContextHolder.getContext()))).andExpect(status().isOk());
  }

  @Test
  @WithMockNonAdminUser
  public void post_ApiInvalid() throws Exception {

    OutputMessage message =
        new OutputMessage(400, "Validation for the file failed", "Invalid file format");
    given(fileService.uploadFile(multiFile)).willReturn(message);

    // act and assert
    this.mockMvc
        .perform(post("/api/v1/upload").content(multiFile.getBytes())
            .contentType(MediaType.MULTIPART_FORM_DATA)
            .param("file", "TVPERF_20180620002641_AAL_N344PP_933.tgz")
            .with(securityContext(SecurityContextHolder.getContext())))
        .andExpect(status().is4xxClientError());
  }

  @Test
  public void tvPerformanceUpload_WhenNotAuthenticated_ShouldReturn401() throws Exception {

    this.mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext)
        .apply(SecurityMockMvcConfigurers.springSecurity()).build();

    mockMvc
        .perform(post("/api/v1/upload").content(multiFile.getBytes())
            .contentType(MediaType.MULTIPART_FORM_DATA)
            .param("file", "TVPERF_20180620002641_AAL_N344PP_933.tgz"))
        .andExpect(status().isUnauthorized());
  }

  @Test
  @WithMockNonAdminUser
  public void offload_InvalidRequestParam_ShouldReturn404() throws Exception {

    // Content type of 'octet' stream should throw Multi-part Exception
    this.mockMvc
        .perform(post("/api/v1/upload").content(multiFile.getBytes())
            .contentType(MediaType.APPLICATION_OCTET_STREAM)
            .param("file", "TVPERF_20180620002641_AAL_N344PP_933.tgz")
            .with(securityContext(SecurityContextHolder.getContext())))
        .andExpect(status().isBadRequest());
  }

  @Test
  @WithMockNonAdminUser
  public void upload_KaLogsWithNormalUser() throws Exception {

    OutputMessage message = new OutputMessage(200, "Success", "File uploaded successfully");
    given(fileService.uploadFile(multiFile)).willReturn(message);

    // act and assert
    this.mockMvc.perform(RestDocumentationRequestBuilders.fileUpload("/api/v1/upload")
        .file(multiFile).param("file", "Ka_United_N37437_UAL513_20190410053545.zip")
        .with(securityContext(SecurityContextHolder.getContext()))).andExpect(status().isOk());
  }

  private ParameterDescriptor[] getRequestParametersDescriptor() {
    return new ParameterDescriptor[] {parameterWithName("file")
        .description("[mandatory] Offload file (TV Performance / BITE  / Ka Logs) to be uploaded")};
  }

  private FieldDescriptor[] getReponseFieldsDescriptor() {
    return new FieldDescriptor[] {
        fieldWithPath("code").description("Http status code of the response")
            .type(String.class.getSimpleName()),
        fieldWithPath("message").description("User defined message for the response")
            .type(String.class.getSimpleName()),
        fieldWithPath("description").description("Detailed description of the error")
            .type(String.class.getSimpleName())};
  }
}
