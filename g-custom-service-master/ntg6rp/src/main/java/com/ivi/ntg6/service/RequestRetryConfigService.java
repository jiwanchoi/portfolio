package com.ivi.ntg6.service;

import java.util.Map;

import javax.xml.ws.http.HTTPException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class RequestRetryConfigService {
	private static final Logger logger = LoggerFactory.getLogger(RequestRetryConfigService.class);
	
	@Autowired
	private PropertiesWithJavaConfig config;
	/**
	 * 
	 * @param reqBodys
	 * @param retry
	 * @return
	 * @throws HTTPException
	 */
	public Map<String, Object> removeReqBody(Map<String, Object> reqBodys, int retry){
		logger.info("### reqBodys ## :::  " + reqBodys.toString());
		if(retry == 1) {
			Object t = reqBodys.get("avoid_area");
			if(t != null) {
				reqBodys.remove("avoid_area");
			}
		}else if(retry ==2) {
			Object t = reqBodys.get("constraints");
			if(t != null) {
				reqBodys.remove("constraints");
			}
		}else if(retry ==3) {
			reqBodys.clear();
		}else {
			reqBodys.clear();
		}
		
		
		
		logger.info("### reqBodys ## :::  " + reqBodys.toString());
		return reqBodys;
	}	
	
	
	public int getRequestRetryCount() {
		return config.getGCSM_RETRY();
	}
}
