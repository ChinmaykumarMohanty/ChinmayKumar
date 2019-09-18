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

import com.thales.ifec.service.ingestion.domain.OutputMessage;
import nl.jqno.equalsverifier.EqualsVerifier;
import nl.jqno.equalsverifier.Warning;
import org.junit.Test;


public class OutputMessageTest {

  @Test
  public void testOutputMessageWithNullValue() {
    OutputMessage message = new OutputMessage();

    assertThat(message.getCode()).isEqualTo(0);
    assertThat(message.getMessage()).isNull();
    assertThat(message.getDescription()).isNull();
  }

  @Test
  public void testOutputMessageWithValues() {
    OutputMessage message = new OutputMessage(200, "Success", "Success message");

    assertThat(message.getCode()).isEqualTo(200);
    assertThat(message.getMessage()).isEqualTo("Success");
    assertThat(message.getDescription()).isEqualTo("Success message");
  }

  @Test
  public void testOutputMessageWithValuesBySetter() {
    OutputMessage message = new OutputMessage();
    message.setCode(200);
    message.setDescription("Success message");
    message.setMessage("Success");

    assertThat(message.toString()).isEqualTo(message.toString());
    assertThat(message.getCode()).isEqualTo(200);
    assertThat(message.getMessage()).isEqualTo("Success");
    assertThat(message.getDescription()).isEqualTo("Success message");
    EqualsVerifier.forClass(OutputMessage.class).suppress(Warning.STRICT_INHERITANCE).verify();
  }
}
