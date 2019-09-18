/*
 * Copyright 2018-2019 Thales and/or its affiliates. All rights reserved.
 *
 * NOTICE - THE INFORMATION CONTAINED HEREIN IS PROPRIETARY AND CONFIDENTIAL
 * TO THALES AVIONICS, INC. (THALES) IN WHOLE OR IN PART AND SHALL NOT BE
 * USED OR DISCLOSED IN WHOLE OR IN PART WITHOUT FIRST OBTAINING THE WRITTEN
 * PERMISSION OF THALES.  
 */
package com.thales.ifec.service.ingestion.web;

import com.thales.ifec.service.ingestion.configuration.Producer;
import com.thales.ifec.service.ingestion.domain.OutputMessage;
import com.thales.ifec.service.ingestion.service.FileService;
import java.io.IOException;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
//import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartException;
import org.springframework.web.multipart.MultipartFile;


@RestController
@RequestMapping("/api/v1")
@Slf4j
public class OffloadIngestionController {

  @Autowired
  Producer producer;

  @Autowired
  FileService fileService;

  /**
   * To process the uploaded file.
   * @param file offload-file to be processed
   * @return ResponseEntity OutputMessage
   * @throws IOException Exception to be thrown
   */
  //@PreAuthorize("hasAuthority('Everyone') || #oauth2.hasScope('upload_offloads')")
  @PostMapping("/upload")
  public ResponseEntity<OutputMessage> upload(@RequestParam("file") MultipartFile file)
      throws IOException {
    log.info("Preparing to upload file :{} ", file.getOriginalFilename());
    
    OutputMessage message = fileService.uploadFile(file);
    return ResponseEntity.status(message.getCode()).body(message);
  }

  /**
   * To handle multipart exception.
   * @param ex multipart exception
   * @return ResponseEntity OutputMessage
   */
  @ExceptionHandler(MultipartException.class)
  public ResponseEntity<OutputMessage> handleMutlipartException(MultipartException ex) {

    OutputMessage message = new OutputMessage();
    message.setCode(HttpStatus.SC_BAD_REQUEST);
    message.setDescription("Invalid file or Missing param..");
    message.setMessage(ex.getMessage());
    return ResponseEntity.status(message.getCode()).body(message);
  }

}
