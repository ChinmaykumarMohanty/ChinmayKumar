/*
 * Copyright 2018-2019 Thales and/or its affiliates. All rights reserved.
 *  
 *   NOTICE - THE INFORMATION CONTAINED HEREIN IS PROPRIETARY AND CONFIDENTIAL
 *   TO THALES AVIONICS, INC. (THALES) IN WHOLE OR IN PART AND SHALL NOT BE
 *   USED OR DISCLOSED IN WHOLE OR IN PART WITHOUT FIRST OBTAINING THE WRITTEN
 *   PERMISSION OF THALES.
 */

package com.thales.ifec.service.ingestion.configuration;

import com.thales.ifec.service.ingestion.domain.OffloadType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class Producer {

  @Autowired
  private AmqpTemplate amqpTemplate;

  @Autowired
  private RmqConfig rmqConfig;
  
  /**
   * To produce offload msg to rabbit MQ.
   * 
   */
  public void produceMsg(String msg, OffloadType offloadType) {

    String exchange = rmqConfig.getRmqExchangeMap().get(offloadType.name().toLowerCase());
    String routingKey = rmqConfig.getRmqRoutingKeyMap().get(offloadType.name().toLowerCase());

    log.info("Sending offload message for processing, Exchange:{}, Routing key : {}", exchange,
        routingKey);
    amqpTemplate.convertAndSend(exchange, routingKey, msg);

    log.info("Message '{}' send successfully", msg);
  }
}
