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

import com.jcraft.jsch.ChannelSftp.LsEntry;
import com.thales.ifec.service.ingestion.configuration.SftpConfig;
import com.thales.ifec.service.ingestion.domain.OutputMessage;
import nl.jqno.equalsverifier.EqualsVerifier;
import nl.jqno.equalsverifier.Warning;
import org.junit.Before;
import org.junit.Test;
import org.springframework.integration.file.remote.session.SessionFactory;
import org.springframework.test.util.ReflectionTestUtils;

public class SftpConfigTest {

  private SftpConfig testSftpConfig = new SftpConfig();

  /**
   * Setting up test suite.
   * 
   */
  @Before
  public void setUp() {
    ReflectionTestUtils.setField(testSftpConfig, "sftpHost", "localhost");
    ReflectionTestUtils.setField(testSftpConfig, "sftpUser", "testuser");
    ReflectionTestUtils.setField(testSftpConfig, "sftpPort", 22);
    ReflectionTestUtils.setField(testSftpConfig, "sftpRemoteFolder", "/tmp");
    ReflectionTestUtils.setField(testSftpConfig, "sftpUploadFileFilter", "*.tgz");
  }

  @Test
  public void testSftpConfig() {

    assertThat(testSftpConfig.getSftpHost()).isEqualTo("localhost");
    assertThat(testSftpConfig.getSftpUser()).isEqualTo("testuser");
    assertThat(testSftpConfig.getSftpPort()).isEqualTo(22);
    assertThat(testSftpConfig.getSftpRemoteFolder()).isEqualTo("/tmp");
    assertThat(testSftpConfig.getSftpUploadFileFilter()).isEqualTo("*.tgz");

    SftpConfig another = new SftpConfig();
    another.setSftpHost(testSftpConfig.getSftpHost());
    another.setSftpPort(testSftpConfig.getSftpPort());
    another.setSftpUser(testSftpConfig.getSftpUser());
    another.setSftpRemoteFolder(testSftpConfig.getSftpRemoteFolder());
    another.setSftpUploadFileFilter(testSftpConfig.getSftpUploadFileFilter());

    assertThat(another.toString()).isEqualTo(testSftpConfig.toString());
    assertThat(testSftpConfig.equals(another)).isTrue();

    EqualsVerifier.forClass(OutputMessage.class).suppress(Warning.STRICT_INHERITANCE).verify();
  }

  @Test
  public void testSftpSessionFactory() {
    SessionFactory<LsEntry> sessionFactory = testSftpConfig.sftpSessionFactory();
    assertThat(sessionFactory).isNotNull();
  }
}
