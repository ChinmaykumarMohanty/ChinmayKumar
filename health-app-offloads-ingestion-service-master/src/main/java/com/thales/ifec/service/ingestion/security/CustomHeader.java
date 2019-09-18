/*
 * Copyright 2018-2019 Thales and/or its affiliates. All rights reserved.
 * 
 * NOTICE - THE INFORMATION CONTAINED HEREIN IS PROPRIETARY AND CONFIDENTIAL TO
 * THALES AVIONICS, INC. (THALES) IN WHOLE OR IN PART AND SHALL NOT BE USED OR
 * DISCLOSED IN WHOLE OR IN PART WITHOUT FIRST OBTAINING THE WRITTEN PERMISSION
 * OF THALES.
 * 
 * 
 * package com.thales.ifec.service.ingestion.security;
 * 
 * import java.util.Collections; import
 * org.springframework.boot.web.servlet.FilterRegistrationBean; import
 * org.springframework.context.annotation.Bean; import
 * org.springframework.core.Ordered; import
 * org.springframework.security.config.annotation.web.builders.HttpSecurity;
 * import
 * org.springframework.security.oauth2.config.annotation.web.configuration.
 * ResourceServerConfigurerAdapter; import
 * org.springframework.stereotype.Component; import
 * org.springframework.web.cors.CorsConfiguration; import
 * org.springframework.web.cors.UrlBasedCorsConfigurationSource; import
 * org.springframework.web.filter.CorsFilter;
 * 
 * @Component public class CustomHeader extends ResourceServerConfigurerAdapter
 * {
 * 
 *//**
	 * Below code is needed because Spring’s @CrossOrigin doesn’t work well with
	 * Spring Security.
	 * 
	 * @return
	 *//*
		 * @Bean public FilterRegistrationBean simpleCorsFilter() { final
		 * UrlBasedCorsConfigurationSource source = new
		 * UrlBasedCorsConfigurationSource(); CorsConfiguration config = new
		 * CorsConfiguration(); config.setAllowCredentials(true);
		 * config.setAllowedOrigins(Collections.singletonList("*"));
		 * config.setAllowedMethods(Collections.singletonList("*"));
		 * config.setAllowedHeaders(Collections.singletonList("*"));
		 * source.registerCorsConfiguration("/api/**", config); FilterRegistrationBean
		 * bean = new FilterRegistrationBean(new CorsFilter(source));
		 * bean.setOrder(Ordered.HIGHEST_PRECEDENCE); return bean; }
		 * 
		 * @Override public void configure(HttpSecurity http) throws Exception {
		 * http.requiresChannel().requestMatchers(r -> r.getHeader("X-Forwarded-Proto")
		 * != null) .requiresSecure(); http.headers().contentSecurityPolicy(
		 * "default-src 'none'; img-src 'self'; script-src 'self'; style-src 'self'"); }
		 * }
		 */