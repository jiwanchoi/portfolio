package com.ivi.ntg6.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;


@RestController
public class TestGetController {

	
//	@Autowired
//	private TestGetService service;
    
    @GetMapping("/eureka/test")
    public String eurekaClient(  ) {
        //HttpRequest req
        String result = "test";
        
        System.out.println("++++++++++++[getHeaders]++++++++++++++++");
//        System.out.println(req.getHeaders());
        System.out.println("++++++++++++++++++++++++++++");
//        System.out.println(service.getTestText(result));       
        System.out.println("++++++++++++++++++++++++++++");
        
        return result;
    }
    
    @PostMapping("/eureka/postt1")
    public String recvPost(String type , @RequestBody Map<String, Object> params) {
    	
    	String result = "TRY";
    	
    	try {
    		System.out.println("++++++++++++[POST]++++++++++++++++");
        	
        	System.out.println("++++++++++++[  "+type+"  ]++++++++++++++++");
        	
        	System.out.println("++++++++++++[  "+params+"  ]++++++++++++++++");
        	
        	System.out.println(" :::::::  type  ::::::  "+params.get("type")  );
        		
        	result = "SUCC";
		} catch (Exception e) {
			
			
			result = "ERROR";
		}

    	
    	return result;
    }
    
    
    
    @PostMapping("/eureka/postt2")
    public Map<String, Object> recvPost2(String type , @RequestBody Map<String, Object> params) {
    	
    	String result = "TRY";
    	
    	Map<String, Object> resultMap = new HashMap<>();
    	
    	try {
    		System.out.println("++++++++++++[POST]++++++++++++++++");
        	
        	System.out.println("++++++++++++[  "+type+"  ]++++++++++++++++");
        	
        	System.out.println("++++++++++++[  "+params+"  ]++++++++++++++++");
        	
        	System.out.println(" :::::::  type  ::::::  "+params.get("type")  );
        		
        	result = "SUCC";
		} catch (Exception e) {
			
			
			result = "ERROR";
		}

    	resultMap.put("result", result);
    	resultMap.put("code", "200");
    	resultMap.put("params", params);
    	
    	
    	return resultMap;
    }

}
