/*
 * Copyright 2018-2019 Thales and/or its affiliates. All rights reserved.
 *  
 *   NOTICE - THE INFORMATION CONTAINED HEREIN IS PROPRIETARY AND CONFIDENTIAL
 *   TO THALES AVIONICS, INC. (THALES) IN WHOLE OR IN PART AND SHALL NOT BE
 *   USED OR DISCLOSED IN WHOLE OR IN PART WITHOUT FIRST OBTAINING THE WRITTEN
 *   PERMISSION OF THALES.
 */

package com.thales.ifec.service.ingestion;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
/*import org.springframework.security.access.expression.method.MethodSecurityExpressionHandler;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.method.configuration.GlobalMethodSecurityConfiguration;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.security.oauth2.provider.expression.OAuth2MethodSecurityExpressionHandler;*/

//@EnableResourceServer
@SpringBootApplication
public class OffloadIngestionApplication {
  public static void main(String[] args) {
    SpringApplication.run(OffloadIngestionApplication.class, args);
  }


	/*
	 * @EnableGlobalMethodSecurity(prePostEnabled = true) protected static class
	 * GlobalSecurityConfiguration extends GlobalMethodSecurityConfiguration {
	 * 
	 * @Override protected MethodSecurityExpressionHandler createExpressionHandler()
	 * { return new OAuth2MethodSecurityExpressionHandler(); } }
	 */

}
