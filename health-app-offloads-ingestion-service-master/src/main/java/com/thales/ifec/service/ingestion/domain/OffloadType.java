/*
 * Copyright 2018-2019 Thales and/or its affiliates. All rights reserved.
 *  
 *   NOTICE - THE INFORMATION CONTAINED HEREIN IS PROPRIETARY AND CONFIDENTIAL
 *   TO THALES AVIONICS, INC. (THALES) IN WHOLE OR IN PART AND SHALL NOT BE
 *   USED OR DISCLOSED IN WHOLE OR IN PART WITHOUT FIRST OBTAINING THE WRITTEN
 *   PERMISSION OF THALES.
 */

package com.thales.ifec.service.ingestion.domain;

public enum OffloadType {

  TVPERFORMANCE("TVPERF"), BITE("BITE"), KALOGS("KALOGS"), CONNECTIVITY("CONNECTIVITY"), USAGE("USAGE");

  private String type;

  OffloadType(String type) {
    this.type = type;
  }

  public String getType() {
    return type;
  }
  
  /**
   * To return actual offload type.
   * 
   * @param type offload type
   * @return
   */
  public static OffloadType fromStatus(String type) {

    for (OffloadType offloadType : OffloadType.values()) {
      if (offloadType.getType().equals(type)) {
        return offloadType;
      }
    }
    return null;
  }

  @Override
  public String toString() {
    return type;
  }

}
