package com.ivi.ntg6.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Configuration
@PropertySource("classpath:config_prod.properties")
public class PropertiesWithJavaConfig {

	// 운영 2020.05.07 변경 : https://gis.kt.com/d-lbs/rp/v1.43
	// 운영 원본 : ${geomaster.ntg6.url}
	@Value("${geomaster.ntg6.url}")
	private String prod_url ;
	
	@Value("${geomaster.auth.key}")
	private String GM_AUTH_KEY;
	
	@Value("${gcustom_auth.url}")
	private String GCSM_AUTH_URL;
	
	@Value("${gcustom.apino}")
	private String GCSM_APINO;
	
	@Value("${gcustom.rp.cnt}")
	private int GCSM_RETRY;
	
	public String getGCSM_AUTH_URL() {
		return GCSM_AUTH_URL;
	}

	public String getGCSM_APINO() {
		return GCSM_APINO;
	}

	public String getProdUrl() {
		return this.prod_url;
	}
	
	
	public String getGmApiKey() {
		return this.GM_AUTH_KEY;
	}
	
	public int getGCSM_RETRY() {
		return this.GCSM_RETRY;
	}
	
}
