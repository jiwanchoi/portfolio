package com.ivi.ntg6.mapper;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;

import com.ivi.ntg6.model.LogData;

@Mapper
public interface Ntg6Mapper {

	void putData(Map map);
	
	List<Map<String, String>> getList();
	
	void updateData(Map param);
	
	/////
	
	void insertLogData(LogData data);
	
}
