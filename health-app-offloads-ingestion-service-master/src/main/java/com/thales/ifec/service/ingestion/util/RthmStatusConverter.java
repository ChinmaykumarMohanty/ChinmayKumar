/*
 * Copyright 2018-2019 Thales and/or its affiliates. All rights reserved.
 *  
 *   NOTICE - THE INFORMATION CONTAINED HEREIN IS PROPRIETARY AND CONFIDENTIAL
 *   TO THALES AVIONICS, INC. (THALES) IN WHOLE OR IN PART AND SHALL NOT BE
 *   USED OR DISCLOSED IN WHOLE OR IN PART WITHOUT FIRST OBTAINING THE WRITTEN
 *   PERMISSION OF THALES.
 */

package com.thales.ifec.service.ingestion.util;

import com.thales.ifec.service.ingestion.domain.RthmStatus;
import javax.persistence.AttributeConverter;
import javax.persistence.Converter;


@Converter
public class RthmStatusConverter implements AttributeConverter<RthmStatus, String> {

  @Override
  public String convertToDatabaseColumn(RthmStatus status) {

    return (status == null) ? null : status.getValue();
  }

  @Override
  public RthmStatus convertToEntityAttribute(String value) {

    return (value != null && !value.isEmpty()) ? RthmStatus.fromStatus(value) : null;
  }

}
