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

import com.thales.ifec.service.ingestion.domain.OffloadsMaster;
import com.thales.ifec.service.ingestion.domain.RthmStatus;
import java.util.Date;
import nl.jqno.equalsverifier.EqualsVerifier;
import org.junit.Test;

public class OffloadsMasterTest {

  private Date testDate = new Date();

  @Test
  public void testOffloadMasterWithNullValue() {
    OffloadsMaster offloadMaster = new OffloadsMaster();

    assertThat(offloadMaster.getAirlineId()).isEqualTo(0);
    assertThat(offloadMaster.getArrAirport()).isNull();
    assertThat(offloadMaster.getArrTime()).isNull();
    assertThat(offloadMaster.getDepAirport()).isNull();
    assertThat(offloadMaster.getDepTime()).isNull();
    assertThat(offloadMaster.getFailureReason()).isNull();
    assertThat(offloadMaster.getFileName()).isNull();
    assertThat(offloadMaster.getFileSize()).isEqualTo(0);
    assertThat(offloadMaster.getFlightLegIds()).isNull();
    assertThat(offloadMaster.getFlightNumber()).isNull();
    assertThat(offloadMaster.getTvFlightId()).isNull();
    assertThat(offloadMaster.getId()).isEqualTo(0);
    assertThat(offloadMaster.getOffloadDate()).isNull();
    assertThat(offloadMaster.getRemarks()).isNull();
    assertThat(offloadMaster.getSource()).isNull();
    assertThat(offloadMaster.getStatus()).isNull();
    assertThat(offloadMaster.getRthmStatus()).isNull();
    assertThat(offloadMaster.getTailsignFound()).isNull();
    assertThat(offloadMaster.getTailsignInFile()).isNull();
    assertThat(offloadMaster.getTailsignSource()).isNull();
    assertThat(offloadMaster.getUploadedTime()).isNull();
    assertThat(offloadMaster.getOffloadType()).isNull();
  }

  @Test
  public void testOffloadMasterWithValues() {
    OffloadsMaster offloadMaster =
        new OffloadsMaster(1, 1, "Test.tgz", 34234, testDate, "New", RthmStatus.PROCESSED, "tet",
            "ttt", "AAL234", "5c4ae97e50bc594772155402", testDate, testDate, "LAX", "JFK", true,
            testDate, "reason", "remarks", "rtrt", "test", "test", "TEST");

    assertThat(offloadMaster.getAirlineId()).isEqualTo(1);
    assertThat(offloadMaster.getArrAirport()).isEqualTo("JFK");
    assertThat(offloadMaster.getArrTime()).isEqualTo(testDate);
    assertThat(offloadMaster.getDepAirport()).isEqualTo("LAX");
    assertThat(offloadMaster.getDepTime()).isEqualTo(testDate);
    assertThat(offloadMaster.getFailureReason()).isEqualTo("reason");
    assertThat(offloadMaster.getFileName()).isEqualTo("Test.tgz");
    assertThat(offloadMaster.getFileSize()).isEqualTo(34234);
    assertThat(offloadMaster.getFlightLegIds()).isEqualTo("rtrt");
    assertThat(offloadMaster.getFlightNumber()).isEqualTo("AAL234");
    assertThat(offloadMaster.getTvFlightId()).isEqualTo("5c4ae97e50bc594772155402");
    assertThat(offloadMaster.getId()).isEqualTo(1);
    assertThat(offloadMaster.getOffloadDate()).isEqualTo(testDate);
    assertThat(offloadMaster.getRemarks()).isEqualTo("remarks");
    assertThat(offloadMaster.getSource()).isEqualTo("test");
    assertThat(offloadMaster.getStatus()).isEqualTo("New");
    assertThat(offloadMaster.getRthmStatus()).isEqualTo(RthmStatus.PROCESSED);
    assertThat(offloadMaster.getTailsignFound()).isEqualTo("ttt");
    assertThat(offloadMaster.getTailsignInFile()).isEqualTo("tet");
    assertThat(offloadMaster.getTailsignSource()).isEqualTo("test");
    assertThat(offloadMaster.getUploadedTime()).isEqualTo(testDate);
    assertThat(offloadMaster.getOffloadType()).isEqualTo("TEST");
  }

  @Test
  public void testOffloadMasterWithValuesBySetter() {
    OffloadsMaster offloadMaster = new OffloadsMaster();
    offloadMaster.setAirlineId(1);
    offloadMaster.setArrAirport("JFK");
    offloadMaster.setArrTime(testDate);
    offloadMaster.setDepAirport("LAX");
    offloadMaster.setDepTime(testDate);
    offloadMaster.setFailureReason("reason");
    offloadMaster.setFileName("Test.tgz");
    offloadMaster.setFileSize(34234);
    offloadMaster.setFlightLegIds("rtrt");
    offloadMaster.setFlightNumber("AAL234");
    offloadMaster.setTvFlightId("5c4ae97e50bc594772155402");
    offloadMaster.setId(1);
    offloadMaster.setOffloadDate(testDate);
    offloadMaster.setOppFound(true);
    offloadMaster.setRemarks("remarks");
    offloadMaster.setSource("test");
    offloadMaster.setStatus("New");
    offloadMaster.setRthmStatus(RthmStatus.NONE);
    offloadMaster.setTailsignFound("ttt");
    offloadMaster.setTailsignSource("test");
    offloadMaster.setTailsignInFile("tet");
    offloadMaster.setUploadedTime(testDate);
    offloadMaster.setOffloadType("BITE");

    assertThat(offloadMaster.toString()).isEqualTo(offloadMaster.toString());
    assertThat(offloadMaster.getAirlineId()).isEqualTo(1);
    assertThat(offloadMaster.getArrAirport()).isEqualTo("JFK");
    assertThat(offloadMaster.getArrTime()).isEqualTo(testDate);
    assertThat(offloadMaster.getDepAirport()).isEqualTo("LAX");
    assertThat(offloadMaster.getDepTime()).isEqualTo(testDate);
    assertThat(offloadMaster.getFailureReason()).isEqualTo("reason");
    assertThat(offloadMaster.getFileName()).isEqualTo("Test.tgz");
    assertThat(offloadMaster.getFileSize()).isEqualTo(34234);
    assertThat(offloadMaster.getFlightLegIds()).isEqualTo("rtrt");
    assertThat(offloadMaster.getFlightNumber()).isEqualTo("AAL234");
    assertThat(offloadMaster.getTvFlightId()).isEqualTo("5c4ae97e50bc594772155402");
    assertThat(offloadMaster.getId()).isEqualTo(1);
    assertThat(offloadMaster.getOffloadDate()).isEqualTo(testDate);
    assertThat(offloadMaster.getRemarks()).isEqualTo("remarks");
    assertThat(offloadMaster.getSource()).isEqualTo("test");
    assertThat(offloadMaster.getStatus()).isEqualTo("New");
    assertThat(offloadMaster.getRthmStatus()).isEqualTo(RthmStatus.NONE);
    assertThat(offloadMaster.getTailsignFound()).isEqualTo("ttt");
    assertThat(offloadMaster.getTailsignInFile()).isEqualTo("tet");
    assertThat(offloadMaster.getTailsignSource()).isEqualTo("test");
    assertThat(offloadMaster.getUploadedTime()).isEqualTo(testDate);
    assertThat(offloadMaster.isOppFound()).isEqualTo(true);
    assertThat(offloadMaster.getOffloadType()).isEqualTo("BITE");

    EqualsVerifier.forClass(OffloadsMaster.class).verify();
  }
}
