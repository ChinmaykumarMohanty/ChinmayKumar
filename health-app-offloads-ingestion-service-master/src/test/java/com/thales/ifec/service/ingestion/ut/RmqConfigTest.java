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

import com.thales.ifec.service.ingestion.configuration.RmqConfig;
import java.util.HashMap;
import java.util.Map;
import org.junit.Before;
import org.junit.Test;
import org.springframework.test.util.ReflectionTestUtils;


public class RmqConfigTest {

  private RmqConfig testRmqConfig = new RmqConfig();

  private Map<String, String> exchangeMap = new HashMap<>();
  Map<String, String> keyMap = new HashMap<>();

  private static final String KEY_TVPERF = "tvperf";

  private static final String KEY_BITE = "bite";

  /**
   * Setting up test suite.
   * 
   */
  @Before
  public void setUp() {

    exchangeMap.put(KEY_TVPERF, "ingestion.tvperf");
    exchangeMap.put(KEY_BITE, "ingestion.bite");

    keyMap.put(KEY_TVPERF, "offload.tvperformance");
    keyMap.put(KEY_BITE, "offload.bite");

    ReflectionTestUtils.setField(testRmqConfig, "rmqExchangeMap", exchangeMap);
    ReflectionTestUtils.setField(testRmqConfig, "rmqRoutingKeyMap", keyMap);
  }


  @Test
  public void rmqConfig() {

    assertThat(testRmqConfig.getRmqExchangeMap().keySet()).isNotEmpty();
    assertThat(testRmqConfig.getRmqExchangeMap().keySet()).contains(KEY_TVPERF, KEY_BITE);
    assertThat(testRmqConfig.getRmqExchangeMap().get(KEY_TVPERF)).isNotEmpty();
    assertThat(testRmqConfig.getRmqExchangeMap().get(KEY_TVPERF)).isEqualTo("ingestion.tvperf");
    assertThat(testRmqConfig.getRmqExchangeMap().get(KEY_BITE)).isNotEmpty();
    assertThat(testRmqConfig.getRmqExchangeMap().get(KEY_BITE)).isEqualTo("ingestion.bite");

    assertThat(testRmqConfig.getRmqRoutingKeyMap().keySet()).isNotEmpty();
    assertThat(testRmqConfig.getRmqRoutingKeyMap().keySet()).contains(KEY_TVPERF, KEY_BITE);
    assertThat(testRmqConfig.getRmqRoutingKeyMap().get(KEY_TVPERF)).isNotEmpty();
    assertThat(testRmqConfig.getRmqRoutingKeyMap().get(KEY_TVPERF))
        .isEqualTo("offload.tvperformance");
    assertThat(testRmqConfig.getRmqRoutingKeyMap().get(KEY_BITE)).isNotEmpty();
    assertThat(testRmqConfig.getRmqRoutingKeyMap().get(KEY_BITE)).isEqualTo("offload.bite");

    assertThat(testRmqConfig.toString()).isNotEmpty();

  }


  @Test
  public void rmqConfig_GetterSetter() {

    RmqConfig another = new RmqConfig();
    another.setRmqExchangeMap(exchangeMap);
    another.setRmqRoutingKeyMap(keyMap);

    assertThat(testRmqConfig.equals(another)).isTrue();

  }



}
