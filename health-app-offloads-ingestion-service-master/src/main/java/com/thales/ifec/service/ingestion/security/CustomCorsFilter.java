/*
 * Copyright 2018-2019 Thales and/or its affiliates. All rights reserved.
 *  
 *   NOTICE - THE INFORMATION CONTAINED HEREIN IS PROPRIETARY AND CONFIDENTIAL
 *   TO THALES AVIONICS, INC. (THALES) IN WHOLE OR IN PART AND SHALL NOT BE
 *   USED OR DISCLOSED IN WHOLE OR IN PART WITHOUT FIRST OBTAINING THE WRITTEN
 *   PERMISSION OF THALES.
 */

package com.thales.ifec.service.ingestion.security;

import java.io.IOException;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class CustomCorsFilter implements Filter {

	@Override
	public void init(FilterConfig filterConfig) {
		log.info("For Init method*********************");
	}

	/*
	 * @Override public void doFilter(ServletRequest req, ServletResponse res,
	 * FilterChain chain) throws IOException, ServletException {
	 * 
	 * HttpServletResponse response = (HttpServletResponse) res; HttpServletRequest
	 * request = (HttpServletRequest) req;
	 * 
	 * String originHeader = request.getHeader("Origin");
	 * 
	 * response.setHeader("Access-Control-Allow-Origin", originHeader);
	 * response.setHeader("Access-Control-Allow-Methods",
	 * "GET, OPTIONS, HEAD, PUT, POST");
	 * response.setHeader("Access-Control-Allow-Headers",
	 * "Origin, X-Requested-With, Content-Type, Accept, Authorization");
	 * 
	 * if (request.getMethod().equals("OPTIONS")) {
	 * response.setStatus(HttpServletResponse.SC_OK);
	 * log.info("For OPTIONS header*********************"); return; }
	 * 
	 * chain.doFilter(req, res); }
	 */

	@Override
  public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain)
          throws IOException, ServletException {
      HttpServletResponse response = (HttpServletResponse) res;
      HttpServletRequest request = (HttpServletRequest) req;
      response.setHeader("Access-Control-Allow-Origin", "*");
      response.setHeader("Access-Control-Allow-Methods", "GET, OPTIONS, HEAD, PUT, POST");
      response.setHeader("Access-Control-Allow-Headers",
              "Origin, X-Requested-With, Content-Type, Accept, Authorization, X-CSRF-TOKEN");
      response.setHeader("Access-Control-Allow-Credentials", "true");
      
      if (request.getMethod().equals("OPTIONS")) {
          response.setStatus(HttpServletResponse.SC_OK);
          log.info("For OPTIONS header*********************");
          return;
      }      chain.doFilter(req, res);
  }

	@Override
	public void destroy() {
		log.info("For destroy method*********************");
	}

}
