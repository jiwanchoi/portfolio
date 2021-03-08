package com.ivi.ntg6stat.mapper;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface Ntg6StatMapper {
 
	List<Map<String, String>> getDayStatics(Map<String, String> param);
	
	List<Map<String, String>> getMonthStatics(Map<String, String> param);
	
	List<Map<String, String>> getHourStatics(Map<String, String> param);	
	
	List<Map<String, String>> getMinStatics(Map<String, String> param);
}
