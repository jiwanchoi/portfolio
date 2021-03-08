package com.ivi.ntg6.service;

import java.util.Collections;
import java.util.Map;

import javax.xml.ws.http.HTTPException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ivi.ntg6.controller.Ntg6Controller;

@Service
public class Auth_Service {

	private static final Logger LOGGER = LogManager.getLogger(Ntg6Controller.class);
	
	@Autowired
	private RestTemplateBuilder restTemplateBuilder;

	@Autowired
	private PropertiesWithJavaConfig config;
	
	/**
	 * 
	 * @param url
	 * @param Authorization
	 * @param bodys
	 * @return
	 * @throws HTTPException
	 */
	public boolean chk_auth_api(String Authorization) {
		boolean vchk = false;
		//System.out.println("chk_auth_api start");
		LOGGER.info("### chk_auth_api start ## :::  ");
		String url = config.getGCSM_AUTH_URL()+config.getGCSM_APINO()+"/";		
		//LOGGER.info("### url ## :::  "+url);
		RestTemplate restTemplate = restTemplateBuilder.build();
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		headers.add("Authorization", Authorization);
		headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
		//LOGGER.info("### Authorization ## :::  "+Authorization);
		HttpEntity<String> param = new HttpEntity(headers);
		String geoResult = restTemplate.exchange(url, HttpMethod.GET, param, String.class).getBody();
		LOGGER.info("### geoResult ## :::  "+geoResult.toString());
				
		//HttpEntity<String> param = new HttpEntity(bodys, headers);
		ObjectMapper mapper = new ObjectMapper();
		// convert JSON string to Map
		try {
			Map<String, Object> map = mapper.readValue(geoResult, Map.class);
			
			if(map.get("useyn") != null) {
				// error_result 안에 code와 message 값이 있음. 
				String useyn = map.get("useyn").toString();
				System.out.println("useyn : " + useyn);
				if(useyn.equals("Y")) {
					vchk = true;
				}
			}else {
				System.out.println("authenticated is null");
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			
			return vchk;
		}
		LOGGER.info("### authenticated is vchk ## :::  "+vchk);		
		return vchk;
	}	
}
