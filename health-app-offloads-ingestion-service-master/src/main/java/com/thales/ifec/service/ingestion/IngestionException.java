/*
 * Copyright 2018-2019 Thales and/or its affiliates. All rights reserved.
 *  
 *   NOTICE - THE INFORMATION CONTAINED HEREIN IS PROPRIETARY AND CONFIDENTIAL
 *   TO THALES AVIONICS, INC. (THALES) IN WHOLE OR IN PART AND SHALL NOT BE
 *   USED OR DISCLOSED IN WHOLE OR IN PART WITHOUT FIRST OBTAINING THE WRITTEN
 *   PERMISSION OF THALES.
 */

package com.thales.ifec.service.ingestion;

/**
 * This class can be used to wraps standard Java checked exceptions and enriches them with a custom
 * message.
 */
public class IngestionException extends Exception {

  private static final long serialVersionUID = -7732365724441950758L;

  public IngestionException(final String message) {
    super(message);
  }

  public IngestionException(final String message, final Throwable cause) {
    super(message, cause);
  }

}
