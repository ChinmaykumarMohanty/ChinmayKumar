/*
 * Copyright 2018-2019 Thales and/or its affiliates. All rights reserved.
 *  
 *   NOTICE - THE INFORMATION CONTAINED HEREIN IS PROPRIETARY AND CONFIDENTIAL
 *   TO THALES AVIONICS, INC. (THALES) IN WHOLE OR IN PART AND SHALL NOT BE
 *   USED OR DISCLOSED IN WHOLE OR IN PART WITHOUT FIRST OBTAINING THE WRITTEN
 *   PERMISSION OF THALES.
 */

package com.thales.ifec.service.ingestion.configuration;

import com.jcraft.jsch.ChannelSftp.LsEntry;
import java.io.File;
import java.util.concurrent.Future;
import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.expression.common.LiteralExpression;
import org.springframework.integration.annotation.Gateway;
import org.springframework.integration.annotation.MessagingGateway;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.integration.file.remote.RemoteFileTemplate;
import org.springframework.integration.file.remote.gateway.AbstractRemoteFileOutboundGateway.Command;
import org.springframework.integration.file.remote.session.CachingSessionFactory;
import org.springframework.integration.file.remote.session.SessionFactory;
import org.springframework.integration.file.support.FileExistsMode;
import org.springframework.integration.sftp.gateway.SftpOutboundGateway;
import org.springframework.integration.sftp.session.DefaultSftpSessionFactory;
import org.springframework.integration.sftp.session.SftpRemoteFileTemplate;
import org.springframework.messaging.MessageHandler;


@Configuration
@Data
public class SftpConfig {

  @Value("${sftp.host}")
  private String sftpHost;

  @Value("${sftp.port:22}")
  private int sftpPort;

  @Value("${sftp.user}")
  private String sftpUser;

  @Value("${sftp.password}")
  private String sftpPasword;

  @Value("${sftp.remote.folder}")
  private String sftpRemoteFolder;

  @Value("${sftp.upload.filefilter:*.tgz}")
  private String sftpUploadFileFilter;

  /**
   * To setup sftp connection.
   * @return
   */
  @Bean
  public SessionFactory<LsEntry> sftpSessionFactory() {
    DefaultSftpSessionFactory factory = new DefaultSftpSessionFactory(true);

    factory.setHost(getSftpHost());
    factory.setPort(getSftpPort());
    factory.setUser(getSftpUser());
    factory.setPassword(getSftpPasword());

    factory.setAllowUnknownKeys(true);
    return new CachingSessionFactory<>(factory);
  }
  
  /**
   * To setup sftp remote file.
   * @return
   */
  @Bean
  public RemoteFileTemplate<LsEntry> sftpRemoteFileTemplate() {
    SftpRemoteFileTemplate remoteFileTemplate = new SftpRemoteFileTemplate(sftpSessionFactory());
    remoteFileTemplate.setRemoteDirectoryExpression(new LiteralExpression(getSftpRemoteFolder()));

    return remoteFileTemplate;
  }

  /**
   * Message handler.
   * @return
   */
  @Bean
  @ServiceActivator(inputChannel = "toSftpChannel")
  public MessageHandler handler() {

    SftpOutboundGateway gateway =
        new SftpOutboundGateway(sftpRemoteFileTemplate(), Command.PUT.getCommand(), "payload");
    gateway.setFileExistsMode(FileExistsMode.FAIL);
    gateway.setRequiresReply(true);
    return gateway;
  }

  @MessagingGateway
  public interface UploadGateway {

    @Gateway(requestChannel = "toSftpChannel")
    Future<String> uploadToBiteServer(File file);
  }

}
