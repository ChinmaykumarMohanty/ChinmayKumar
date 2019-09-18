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

import com.thales.ifec.service.ingestion.domain.RthmStatus;
import com.thales.ifec.service.ingestion.util.RthmStatusConverter;
import org.junit.Test;


public class RthmStatusConverterTest {

  private RthmStatusConverter enumConverter = new RthmStatusConverter();

  @Test
  public void toDatabaseColumn() {

    RthmStatus rthmStatus = null;
    assertThat(enumConverter.convertToDatabaseColumn(rthmStatus)).isNull();

    // Test RthmStatus.UPLOADED
    rthmStatus = RthmStatus.UPLOADED;
    String uploadedStatus = enumConverter.convertToDatabaseColumn(rthmStatus);

    assertThat(uploadedStatus).isNotEmpty();
    assertThat(uploadedStatus).isEqualTo(RthmStatus.UPLOADED.getValue());

    // Test RthmStatus.NONE
    rthmStatus = RthmStatus.NONE;
    uploadedStatus = enumConverter.convertToDatabaseColumn(rthmStatus);
    assertThat(uploadedStatus).isNotEmpty();
    assertThat(uploadedStatus).isEqualTo(RthmStatus.NONE.getValue());

    // Test RthmStatus.PROCESSED
    rthmStatus = RthmStatus.PROCESSED;
    uploadedStatus = enumConverter.convertToDatabaseColumn(rthmStatus);
    assertThat(uploadedStatus).isNotEmpty();
    assertThat(uploadedStatus).isEqualTo(RthmStatus.PROCESSED.getValue());
  }


  @Test
  public void convertToEntityAttribute() {

    assertThat(enumConverter.convertToEntityAttribute("")).isNull();
    assertThat(enumConverter.convertToEntityAttribute(null)).isNull();
    assertThat(enumConverter.convertToEntityAttribute("somevalue")).isNull();
    assertThat(enumConverter.convertToEntityAttribute("None")).isNull();

    assertThat(enumConverter.convertToEntityAttribute("none")).isEqualTo(RthmStatus.NONE);
    assertThat(enumConverter.convertToEntityAttribute("uploaded")).isEqualTo(RthmStatus.UPLOADED);
    assertThat(enumConverter.convertToEntityAttribute("processed")).isEqualTo(RthmStatus.PROCESSED);

    assertThat(enumConverter.convertToEntityAttribute(RthmStatus.NONE.getValue()))
        .isEqualTo(RthmStatus.NONE);
    assertThat(enumConverter.convertToEntityAttribute(RthmStatus.UPLOADED.getValue()))
        .isEqualTo(RthmStatus.UPLOADED);
    assertThat(enumConverter.convertToEntityAttribute(RthmStatus.PROCESSED.getValue()))
        .isEqualTo(RthmStatus.PROCESSED);

  }


}
