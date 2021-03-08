package com.api.web.controller;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.RequestDispatcher;
import javax.servlet.http.HttpServletRequest;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.osgeo.proj4j.ProjCoordinate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.api.web.model.Ndslog;
import com.api.web.model.NdslogtRepository;
import com.api.web.service.Auth_Service;
import com.api.web.service.NdsService;


/**
 * 
 * *** [ NdsTrans ] 
 * *** [ G - Custom ]  
 *  
 */

/**
 * 
 * @author Nds
 *
 */
@RestController
public class NdsController {

	
	private static final Logger LOGGER = LogManager.getLogger(NdsController.class);
	
	@Autowired
    private NdslogtRepository ndslogtRepository;
	
	@Autowired
	private Auth_Service auth_service;
	
	private final SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
	
	/**
     * 
     * @param req
     * @return
     */
	@RequestMapping("/get")
    public ResponseEntity get_nds_api(  HttpServletRequest req  ) {		
    	 //return headers
        HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
		
		
		
    	//boolean chk = auth_service.chk_auth_api(req.getHeader("gcauth"));
    	boolean chk = true;
    	System.out.println("### chk ## :::  " + chk);    		
    	ResponseEntity<Map> resEntity = null;  	    	
    	if(chk) {
    		try {
				resEntity = transfer_nds(req);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
    	}else {           
    		
    		//String result = "{\"code\": 400001,\r\n \"message\": \"Unauthorized\"\r\n}";   
    		Map<String, Object> resMap = new HashMap<>();
    		resMap.put("code", "error");
        	resMap.put("message", "something is invalid");
            resEntity = ResponseEntity.ok().body(resMap);
            
    	}    	
    	return resEntity;
    }
    
        
    public ResponseEntity<Map> transfer_nds(HttpServletRequest req) throws Exception{    	
    	String lat = req.getParameter("lat");
    	String lon = req.getParameter("lon");
    	String lev = req.getParameter("lev");
    	String crs = req.getParameter("crs");
    	
    	Map<String, Object> resMap = new HashMap<>();    	
    	int tileY=-1;
    	int tileX=-1;
    	//double lat1 = 35.235531;
    	//double lon1 = 128.887401;    	
    	// 965899, 1928929 UTMK		
    	try {
    		double lat1 = Double.valueOf(lat);
        	double lon1 = Double.valueOf(lon);        	
        	
        	int lev1 = Integer.parseInt(lev);    	
        	double tile_size = 180/Math.pow(2,lev1);
        	System.out.println("size : " + tile_size);	
        	
        	NdsService g = new NdsService(crs);
        	ProjCoordinate p2 = g.transform(lon1, lat1);        
        	tileY = (int) Math.floor((p2.y+90)/tile_size);
        	tileX = (int) Math.floor((p2.x+180)/tile_size);       
        	resMap.put("tileY", tileY);
        	resMap.put("tileX", tileX);          
            
    	}catch(Exception e){
    		resMap.put("error", "parameter is invalid");
    	}
    	
    	try {
    		//로그 쌓기.
        	Ndslog ndslog = new Ndslog();
        	ndslog.setLat(lat);		//입력 lat
        	ndslog.setLon(lon);		//입력 lon
        	ndslog.setLev(lev);						//입력 lev
        	ndslog.setCrs(crs);					//입력 crs
        	ndslog.setTilex(String.valueOf(tileX));	//출력 tilex
        	ndslog.setTiley(String.valueOf(tileY));	//출력 tiley
        	String regdate = sdf.format(new Timestamp(System.currentTimeMillis()));
        	ndslog.setRegdate(regdate);
        	System.out.println("ndslog : "+ndslog.toString());
        	ndslogtRepository.save(ndslog);
    	}catch(Exception e) {
    		System.out.println("ndslog insert error : "+e.toString());
    	}    	
    	return ResponseEntity.ok().body(resMap);
    	
    }    
    
}
