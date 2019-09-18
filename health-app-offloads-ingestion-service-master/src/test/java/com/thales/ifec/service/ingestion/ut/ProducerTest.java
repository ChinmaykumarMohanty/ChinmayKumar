/*
 * Copyright 2018-2019 Thales and/or its affiliates. All rights reserved.
 *  
 *   NOTICE - THE INFORMATION CONTAINED HEREIN IS PROPRIETARY AND CONFIDENTIAL
 *   TO THALES AVIONICS, INC. (THALES) IN WHOLE OR IN PART AND SHALL NOT BE
 *   USED OR DISCLOSED IN WHOLE OR IN PART WITHOUT FIRST OBTAINING THE WRITTEN
 *   PERMISSION OF THALES.
 */

package com.thales.ifec.service.ingestion.ut;

import static org.mockito.BDDMockito.willDoNothing;
import static org.mockito.Matchers.anyString;
import static org.mockito.MockitoAnnotations.initMocks;

import com.thales.ifec.service.ingestion.configuration.Producer;
import com.thales.ifec.service.ingestion.configuration.RmqConfig;
import com.thales.ifec.service.ingestion.domain.OffloadType;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.amqp.core.AmqpTemplate;

public class ProducerTest {

  @Mock
  private AmqpTemplate amqpTemplate;

  @Mock
  private RmqConfig mockRmqConfig;

  @InjectMocks
  private Producer producer;

  @Before
  public void setUp() {
    initMocks(this);
  }

  @Test
  public void testTvPerformanceOffload() {
    willDoNothing().given(amqpTemplate).convertAndSend(anyString(), anyString(), anyString());

    producer.produceMsg("BITE_test_offload.tgz", OffloadType.TVPERFORMANCE);
  }

  @Test
  public void testBiteOffload() {
    willDoNothing().given(amqpTemplate).convertAndSend(anyString(), anyString(), anyString());

    producer.produceMsg("BITE_test_offload.tgz", OffloadType.BITE);
  }
}
