/*
 * Copyright 2018-2019 Thales and/or its affiliates. All rights reserved.
 *  
 *   NOTICE - THE INFORMATION CONTAINED HEREIN IS PROPRIETARY AND CONFIDENTIAL
 *   TO THALES AVIONICS, INC. (THALES) IN WHOLE OR IN PART AND SHALL NOT BE
 *   USED OR DISCLOSED IN WHOLE OR IN PART WITHOUT FIRST OBTAINING THE WRITTEN
 *   PERMISSION OF THALES.
 */

package com.thales.ifec.service.ingestion.domain;

/**
 * This class represents the RTHM Data status in BITE off-load.
 * 
 */
public enum RthmStatus {

  /** BITE Off-load contains RTHM data is uploaded. */
  UPLOADED("uploaded"),

  /** RTHM data in BITE off-load is processed and saved in MongoDB Atlas. */
  PROCESSED("processed"),

  /** Bite off-load with no RTHM data. **/
  NONE("none");


  private String status;


  private RthmStatus(String rthmStatus) {
    this.status = rthmStatus;
  }

  /**
   * Returns the RTHM status.
   * 
   * @return
   */
  public String getValue() {
    return status;
  }

  /**
   * Returns RthmStatus for a status value.
   * 
   * @param status Rthm Status value
   * @return
   */
  public static RthmStatus fromStatus(String status) {

    for (RthmStatus rthmStatus : RthmStatus.values()) {
      if (rthmStatus.getValue().equals(status)) {
        return rthmStatus;
      }
    }
    return null;
  }

}
