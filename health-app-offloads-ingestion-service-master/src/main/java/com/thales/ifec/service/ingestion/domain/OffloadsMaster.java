/*
 * Copyright 2018-2019 Thales and/or its affiliates. All rights reserved.
 *  
 *   NOTICE - THE INFORMATION CONTAINED HEREIN IS PROPRIETARY AND CONFIDENTIAL
 *   TO THALES AVIONICS, INC. (THALES) IN WHOLE OR IN PART AND SHALL NOT BE
 *   USED OR DISCLOSED IN WHOLE OR IN PART WITHOUT FIRST OBTAINING THE WRITTEN
 *   PERMISSION OF THALES.
 */

package com.thales.ifec.service.ingestion.domain;

import com.thales.ifec.service.ingestion.util.RthmStatusConverter;
import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Table(name = "offloads_master")
public class OffloadsMaster {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private int id;

  @Column(name = "airlineid")
  private int airlineId;

  @Column(name = "filename", updatable = true)
  private String fileName;

  @Column(name = "filesize", updatable = true)
  private long fileSize;

  @Column(name = "offloaddate", updatable = true)
  private Date offloadDate;

  @Column(name = "status", updatable = true)
  private String status;

  @Column(name = "rthmStatus", updatable = true)
  @Convert(converter = RthmStatusConverter.class)
  private RthmStatus rthmStatus;

  @Column(name = "tailsigninfile", updatable = true)
  private String tailsignInFile;

  @Column(name = "tailsignfound", updatable = true)
  private String tailsignFound;

  @Column(name = "flightnumber", updatable = true)
  private String flightNumber;

  @Column(name = "tvFlightId", updatable = true)
  private String tvFlightId;

  @Column(name = "deptime", updatable = true)
  private Date depTime;

  @Column(name = "arrtime", updatable = true)
  private Date arrTime;

  @Column(name = "depairport", updatable = true)
  private String depAirport;

  @Column(name = "arrairport", updatable = true)
  private String arrAirport;

  @Column(name = "oppfound", updatable = true)
  private boolean oppFound;

  @Column(name = "uploadedtime", updatable = true)
  private Date uploadedTime;

  @Column(name = "failurereason", updatable = true)
  private String failureReason;

  @Lob
  @Column(name = "remarks", updatable = true)
  private String remarks;

  @Column(name = "flightlegids", updatable = true)
  private String flightLegIds;

  @Column(name = "source", updatable = true)
  private String source;

  @Column(name = "tailsignsource", updatable = true)
  private String tailsignSource;

  @Column(name = "offloadType", updatable = true)
  private String offloadType;
}
