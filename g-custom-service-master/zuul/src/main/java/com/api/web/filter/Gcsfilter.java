package com.api.web.filter;

import java.util.Collections;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;

@Component
public class Gcsfilter extends ZuulFilter {
	private static final Logger logger = LoggerFactory.getLogger(Gcsfilter.class);
    
	@Autowired
    private RestTemplateBuilder restTemplateBuilder;
	
	//@Autowired
	//private RestTemplate restTemplate;
	
	@Value("${gcustom.auth.url}")
	private String GC_AUTH_URL;
	
	@Value("${gcustom.domain.url}")
	private String GC_API_URL;
	
    @Override
    public String filterType() {
        return "pre";
    }
 
    @Override
    public int filterOrder() {
        return 20;
    }
 
    @Override
    public boolean shouldFilter() {
       // return true;.
    	
        RequestContext ctx = RequestContext.getCurrentContext();
        HttpServletRequest request = ctx.getRequest();
        logger.debug("ctx : " + ctx.toString());        
		String requestURL = request.getRequestURL().toString();		
		
		logger.debug("requestURL shouldFilter : " + requestURL);
		logger.debug("GC_API_URL: " + GC_API_URL);
		logger.debug("33333: " + requestURL.indexOf(GC_API_URL));
		
		
		boolean bRun = true;
		
		// black list
		if(requestURL.indexOf(GC_API_URL) != -1){
			bRun = true;
		}
		logger.debug("bRun: " + bRun);
		return bRun;		
    }
 
    @Override
    public Object run() {
    	//ResponseEntity<Map> resHttpEntity = null;
    	//resHttpEntity = restTemplate.exchange(GC_AUTH_URL, HttpMethod.POST, entity, Map.class);
    	
    	
        RequestContext ctx = RequestContext.getCurrentContext();
        HttpServletRequest request = ctx.getRequest();        
        
        logger.debug("Request Method : " + request.getMethod());
        logger.debug("Request URL : " + request.getRequestURL().toString());
        

//        원본코드
        String authorizationHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
//		String authorizationHeader = request.getHeader("gcauth");
        logger.debug("AUTHORIZATION : " + authorizationHeader);
        
        String url = "http://"+StringUtils.upperCase("gcustomauth")+"/api/session";
        if(GC_AUTH_URL != "none") {
        	url = "http://"+GC_AUTH_URL+"/api/session";
        }
        logger.debug("AUTH URL  : " + url);
        
        if (!validateToken(url, authorizationHeader)) {
            ctx.setSendZuulResponse(false);
            ctx.setResponseBody("API key not authorized");
            ctx.getResponse().setHeader("Content-Type", "text/plain;charset=UTF-8");
            ctx.setResponseStatusCode(HttpStatus.UNAUTHORIZED.value());
        }
        return null;
    }
    
    private boolean validateToken(String url, String tokenHeader) {
        // do something to validate the token
    	logger.debug("validateToken is validateToken");
    	boolean vchk = false;
    	RestTemplate restTemplate = restTemplateBuilder.build();

        HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		headers.add("Authorization", tokenHeader );
		headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
		
		HttpEntity<String> param = new HttpEntity(headers);
		
		String geoResult = restTemplate.exchange(url, HttpMethod.GET, param, String.class).getBody();
		logger.debug("geoResult : " + geoResult);		
		
		ObjectMapper mapper = new ObjectMapper();
		// convert JSON string to Map
		try {
			Map<String, Object> map = mapper.readValue(geoResult, Map.class);
			
			if(map.get("authenticated") != null) {
				// error_result 안에 code와 message 값이 있음. 
				String stat = map.get("authenticated").toString();
				logger.debug("stat : " + stat);
				if(stat.equals("true")) {
					vchk = true;
				}
			}else {
				logger.debug("authenticated is null");
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			
			return vchk;
		}		
		
		logger.debug("authenticated is vchk : "+vchk);
        return vchk;
    }
}
