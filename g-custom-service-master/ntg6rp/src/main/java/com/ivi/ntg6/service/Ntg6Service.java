package com.ivi.ntg6.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
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
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.StopWatch;
import org.springframework.web.client.RestTemplate;

@Service
public class Ntg6Service {
	private static final Logger logger = LogManager.getLogger(Ntg6Service.class);

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
	public String urlPostSend(String url, String Authorization, Map<String, Object> bodys) {
		final StopWatch stopWatch = new StopWatch("urlPostSend : Request to GM");
		stopWatch.start("prepare to send");

		RestTemplate restTemplate = restTemplateBuilder.build();

		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		headers.add("Authorization", config.getGmApiKey());
		headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));

		HttpEntity<String> param = new HttpEntity(bodys, headers);
		String geoResult = "";
		stopWatch.stop();
		try {
			stopWatch.start("Send POST request");
			geoResult = restTemplate.postForObject(url, param, String.class);
			logger.info(" [ GM URL ] : " + url);
			logger.info(" [ GM Auth ] : " + config.getGmApiKey());
			stopWatch.stop();
		} catch (HTTPException he) {
			logger.error("### SP[ STATUS CODE ] ## :::  " + he.getStatusCode());
		}
		logger.info(stopWatch.prettyPrint());

		return geoResult;
	}

	/**
	 * 
	 * @param url
	 * @param Authorization
	 * @return
	 * @throws HTTPException
	 */
	public String urlGetSend(String url, String Authorization) {
		final StopWatch stopWatch = new StopWatch("urlGetSend, Send reqeust to GM");
		stopWatch.start("prepare to send request");
		RestTemplate restTemplate = restTemplateBuilder.build();

		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		headers.add("Authorization", config.getGmApiKey());
		headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));

		HttpEntity<String> param = new HttpEntity(headers);
//		if (logger.isDebugEnabled()) {
//		if ( true ) {
//			logger.debug("URL : " + url);
//			HttpHeaders dheaders = param.getHeaders();
//			for (Iterator it = dheaders.keySet().iterator(); it.hasNext() == true;) {
//				String header = (String) it.next();
//				StringBuffer values = new StringBuffer();
//				ArrayList valueList = (ArrayList) dheaders.get(header);
//				for (int i = valueList.size(); i != 0; i--) {
//					valueList.add("["+ valueList.get(i) + "]");
//				}
//				logger.debug(header + " = " + values.toString());
//			}
//
//			logger.debug("==== body ====");
//			logger.debug("[" + param.getBody() + "]");
//		}
		stopWatch.stop();
		stopWatch.start("send request to gm");
		logger.info(" [ GM URL ] : " + url);
		logger.info(" [ GM Auth ] : " + config.getGmApiKey());
		ResponseEntity<String> resEntity = restTemplate.exchange(url, HttpMethod.GET, param, String.class) ;
		stopWatch.stop();
		stopWatch.start("after send request to gm");
		String geoResult = resEntity.getBody();
		stopWatch.stop();
//		logger.info(stopWatch.prettyPrint());
		return geoResult;
	}

	public String makeUrl(String st, String dt, String crs, String wps) {

		/**
		 * 운영기 ; https://gis.kt.com/lbs/rp/v1.42 개발기 ;
		 * https://dev.gis.kt.com:10001/lbs/rp/v1.42
		 * 
		 * +운영기 가능 API Key "Authorization", "Bearer
		 * b6deabf9bc298c4a6d1cddec5f9ce234409501e5ef98ed3d99fa0525d39dac065381101c"
		 * 
		 */
		String geoUrl = config.getProdUrl(); //"https://gis.kt.com/lbs/rp/v1.42";
		geoUrl += "?st=" + st + "&dt=" + dt + "&crs=" + crs;

		if (wps != null)
			if (wps.length() > 0)
				geoUrl += "&wps=" + wps;

		return geoUrl;
	}	
	//wps를 분할하기.
	public ArrayList<String[]> chk_wps(String st,String dt,String wps){
		ArrayList<String[]> arrWps = new ArrayList<String[]>();
		if(wps != null){
			if (wps.length() > 0 && !wps.contains("P") && !wps.contains("p")){
				String[] temp_list = wps.split(";"); //첫번째 구분은 세미콜론.
				for(int i=0;i<temp_list.length;i++){
					//System.out.println("temp_list : "+temp_list[i]);
					if(!temp_list[i].contains("P") && !temp_list[i].contains("p")){	//포인트가 아닌구역아이디로 오는건 P 이건 그냥 통과해서 wps로 기본동작으로보냄.																
						if(i == 0){
							String ep = temp_list[i];
							ep = ep.replace("V", ""); //포인트중에 앞에 v 있는거 제거하고 시작점 만듬.
							ep = ep.replace("v", ""); //포인트중에 앞에 v 있는거 제거하고 끝점 만듬.
							String[] pp = {st,ep};
							arrWps.add(pp);							
						}else{
							String sp_ = temp_list[i-1];
							String[] sp_list = sp_.split("\\+");
							String sp = sp_list[sp_list.length-1].replace("v", ""); //포인트중에 앞에 v 있는거 제거하고 시작점 만듬.
							sp = sp.replace("V", "");	
							String ep = temp_list[i];
							ep = ep.replace("V", ""); //포인트중에 앞에 v 있는거 제거하고 시작점 만듬.
							ep = ep.replace("v", ""); //포인트중에 앞에 v 있는거 제거하고 끝점 만듬.
							String[] pp = {sp,ep};
							arrWps.add(pp);
						}					
					}
				}

				//마지막 도착지점 넣기.
				String sp_ = temp_list[temp_list.length-1];
				String[] sp_list = sp_.split("\\+");
				String sp = sp_list[sp_list.length-1].replace("v", ""); //포인트중에 앞에 v 있는거 제거하고 시작점 만듬.
				sp = sp.replace("V", "");	
				String[] pp = {sp,dt};
				arrWps.add(pp);
			}				
		}
		return arrWps;
	}
}
