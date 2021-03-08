/*
 * Copyright(C) 2017 KT Hitel Co., Ltd. all rights reserved.
 *
 * This is a proprietary software of KTH corp, and you may not use this file except in
 * compliance with license with license agreement with KTH corp. Any redistribution or use of this
 * software, with or without modification shall be strictly prohibited without prior written
 * approval of KTH corp, and the copyright notice above does not evidence any actual or
 * intended publication of such software.
 */

package com.api.web;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.netflix.zuul.filters.route.FallbackProvider;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.client.ClientHttpResponse;

/**
 * Zuul Fallback Provider
 *
 * @author <a href="mailto:ky.son@kt.com"><b>손근양</b></a>
 * @version 1.0.0
 * @see FallbackProvider
 * @since 7.0
 */
public class ZuulFallbackProvider implements FallbackProvider {

	private static final Logger log = LoggerFactory.getLogger(ZuulFallbackProvider.class);

	private String route = "route";

	private HttpHeaders headers;

	private HttpStatus statusCode = HttpStatus.SERVICE_UNAVAILABLE;

	private int rawStatusCode = HttpStatus.SERVICE_UNAVAILABLE.value();

	private String statusText = HttpStatus.SERVICE_UNAVAILABLE.getReasonPhrase();

	private String responseBody = "{\"result_code\":" + statusCode.value() + ",\"message\":\""
			+ statusCode.getReasonPhrase() + "\"}";

	@Override
	public String getRoute() {
		return route;
	}

	public void setRoute(String route) {		
		this.route = route;		
	}

	@Override
	public ClientHttpResponse fallbackResponse(String route, Throwable cause) {
		if (log.isErrorEnabled()) {
			log.error("Error - Route : " + route + ", Response : " + responseBody, cause);
		}

		return new ClientHttpResponse() {

			@Override
			public HttpHeaders getHeaders() {
				if (headers == null) {
					headers = new HttpHeaders();
					headers.setContentType(MediaType.APPLICATION_JSON);
				}
				return headers;
			}

			@Override
			public HttpStatus getStatusCode() throws IOException {
				return statusCode;
			}

			@Override
			public int getRawStatusCode() throws IOException {
				return rawStatusCode;
			}

			@Override
			public String getStatusText() throws IOException {
				return statusText;
			}

			@Override
			public InputStream getBody() throws IOException {
				return new ByteArrayInputStream(responseBody.getBytes());
			}

			@Override
			public void close() {
				// ignore...
			}

		};
	}

	public void setStatusCode(HttpStatus statusCode) {
		if (statusCode != null) {
			this.statusCode = statusCode;
		}
	}

	public void setStatusText(String statusText) {
		this.statusText = statusText;
	}

	public void setResponseBody(String responseBody) {
		this.responseBody = responseBody;
	}

}
