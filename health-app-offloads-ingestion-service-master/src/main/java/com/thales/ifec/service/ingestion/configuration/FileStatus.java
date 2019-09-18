/*
 * Copyright 2018-2019 Thales and/or its affiliates. All rights reserved.
 *  
 *   NOTICE - THE INFORMATION CONTAINED HEREIN IS PROPRIETARY AND CONFIDENTIAL
 *   TO THALES AVIONICS, INC. (THALES) IN WHOLE OR IN PART AND SHALL NOT BE
 *   USED OR DISCLOSED IN WHOLE OR IN PART WITHOUT FIRST OBTAINING THE WRITTEN
 *   PERMISSION OF THALES.
 */

package com.thales.ifec.service.ingestion.configuration;

/**
 * This class represents the status of off-load file upload.
 *
 */
public enum FileStatus {

  /** Newly uploaded TV-PERF off-load file - file is uploaded to S3 bucket for processing. */
  UPLOADED("Uploaded"),

  /**
   * Newly uploaded BITE off-load file - file is uploaded to S3 bucket for processing by Legacy BITE
   * tool.
   */
  NEW("New"),

  /**
   * Off load file is successfully processed and the content is saved in MongoDB Atlas.
   */
  PROCESSED("Processed"),

  /**
   * Processing of Off-load file cannot be completed due to reasons like invalid file, missing
   * mandatory files etc.
   */
  REJECTED("Rejected");

  private String status;

  private FileStatus(String fileStatus) {
    this.status = fileStatus;
  }

  /**
   * Return File upload Status.
   * 
   * @return
   */
  public String getValue() {
    return status;
  }
}
