package com.ivi.ntg6stat.service;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ivi.ntg6stat.mapper.Ntg6StatMapper;

@Service
public class Ntg6StatService {

	@Autowired
	private Ntg6StatMapper mapper;
	
	//일자별
	public List<Map<String, String>> getDayStatics(Map<String, String> param){
		
		return mapper.getDayStatics(param);
	}
	
	//월별
	public List<Map<String, String>> getMonthStatics(Map<String, String> param){
		
		return mapper.getMonthStatics(param);
	}
	
	//시간별
	public List<Map<String, String>> getHourStatics(Map<String, String> param){
		
		return mapper.getHourStatics(param);
	}
	
	//분별
	public List<Map<String, String>> getMinStatics(Map<String, String> param){
		
		return mapper.getMinStatics(param);
	}
	
	//stdate , edate validation
	public String checkParams(String stdate , String edate , String dtype) {
		String result = "";
		int dateTimeValueLength = 8;
		
		if(dtype.equals("month")) {
			dateTimeValueLength = 6;
		} else if (dtype.equalsIgnoreCase("day")) {
			dateTimeValueLength = 8;
		} else if (dtype.equalsIgnoreCase("hour")) {
			dateTimeValueLength = 10;
		} else if (dtype.equalsIgnoreCase("minute")) {
			dateTimeValueLength = 12;
		}
		
		if(stdate == null || !(stdate.length() >= dateTimeValueLength) ) {  // null
			result = "sdate";
		}else if( edate == null || !(edate.length() >= dateTimeValueLength)) {  // yymmdd
			result = "edate";
		}
		
		if( result.length() < 1 &&( dtype.equals("hour") || dtype.equals("minute") ) ) {
			String st = stdate.substring(0, 8);
			String dt = edate.substring(0, 8);
			
			System.out.println("###  {"+dtype+"}  ########"+ (Integer.parseInt(dt) - Integer.parseInt(st) ) );
			int dateCnt = Integer.parseInt(dt) - Integer.parseInt(st) ; 
			
			if( dateCnt > 3 ) {
				return "3";
			}else if(dateCnt < 0) {
				return "4";
			}
		}		
		return result;
	}	
}
