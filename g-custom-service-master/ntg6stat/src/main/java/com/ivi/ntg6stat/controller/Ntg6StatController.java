package com.ivi.ntg6stat.controller;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.ivi.ntg6stat.service.Ntg6StatService;
import com.ivi.ntg6stat.service.Auth_Service;

@RestController
public class Ntg6StatController {

    private static final Logger LOGGER = LogManager.getLogger(Ntg6StatController.class);

    @Autowired
    private Ntg6StatService ntg6StatService;

    @Autowired
    private Auth_Service auth_service;


    // 월 , 일 , 시 , 분
    @RequestMapping("/stat")
    public ResponseEntity get_auth_api(HttpServletRequest req) {
        System.out.println("### stat start ## :::  ");
        //return headers
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));

        //boolean chk = auth_service.chk_auth_api(req.getHeader("Authorization"));
        boolean chk = true;
        System.out.println("### chk ## :::  " + chk);
        ResponseEntity resEntity = null;
        if (chk) {
            resEntity = getStat(req, headers);
        } else {
            String result = "{\"code\": 401,\r\n \"message\": \"Unauthorized\"\r\n}";
            resEntity = new ResponseEntity<String>(result, headers, HttpStatus.UNAUTHORIZED);
        }
        return resEntity;
    }


    public ResponseEntity<String> getStat(HttpServletRequest req, HttpHeaders headers) {

        String res_result = "";
        Boolean chkError = false;
        String errorCode = "";
        String errorMessage = "";

        LOGGER.info("#######  [ NTG6 stat ] - ");

        String sdate = req.getParameter("sdate"); //시작일
        String edate = req.getParameter("edate");  //종료일
        String dtype = req.getParameter("dtype");  //일자타입 구분

		/*
		 *  [dtype]  기간항목
			● day - 일별 (default)
			● month - 월별
			● hour - 시간별
			● minute - 분별
			getTimeDate
		 */

        //default
        if (dtype == null || dtype.trim().length() < 1) {
            dtype = "day";
        }

        dtype = dtype.trim().toLowerCase();

        // HOUR YYYYMMDD
//		if(dtype.equals("hour") && sdate.length() == 8 ) {
//			sdate += "0000";
//		}
//		if(dtype.equals("hour") && edate.length() == 8 ) {
//			edate += "0000";
//		}

        int validSize = 8;
        int padSize = 12 - validSize;
        final int maxDateLength = 12;
        switch (dtype.toLowerCase()) {
            case "month": // YYYYMM
                validSize = 6;
                break;
            case "day":  // YYYYMMDD
                validSize = 8;
                break;
            case "hour":  // YYYYMMDDHH
                validSize = 10;
                break;
            case "minute":  // YYYYMMDDHHMM
                validSize = 12;
                break;
        }
        padSize = maxDateLength - validSize;
        sdate = StringUtils.rightPad(sdate.substring(0, validSize), padSize, '0');
        edate = StringUtils.rightPad(edate.substring(0, validSize), padSize, '0');

        // validation check
        String paramsName = ntg6StatService.checkParams(sdate, edate, dtype);

        if (paramsName.length() > 1) {
            chkError = true;
            errorCode = "E0002";
            errorMessage = paramsName + " is invalid:xxx";
        } else if (paramsName.equals("3")) {
            chkError = true;
            errorCode = "E0003";
            errorMessage = "It is over time check sdate, edate";
        } else if (paramsName.equals("4")) {
            chkError = true;
            errorCode = "E0004";
            errorMessage = " stdate is over the edate";
        } else {
            chkError = false;
        }

        HashMap<String, String> param = new HashMap<>();

        param.put("dtype", dtype);
        param.put("sdate", sdate);
        param.put("edate", edate);

        ArrayList<HashMap<String, Object>> list_result = new ArrayList<>();

        List<Map<String, String>> statList = new ArrayList<>();

        if (!chkError && dtype.equals("day")) {
            statList = ntg6StatService.getDayStatics(param);
        } else if (!chkError && dtype.equals("month")) {
            statList = ntg6StatService.getMonthStatics(param);
        } else if (!chkError && dtype.equals("hour")) {
            statList = ntg6StatService.getHourStatics(param);
        } else if (!chkError && dtype.equals("minute")) {
            statList = ntg6StatService.getMinStatics(param);
        }

        // ArrayList to json
        LOGGER.info(statList.toString());

        JsonArray jsonArray = new JsonArray(statList.size());
        for (Map<String, String> tmp : statList) {

            JsonObject obj_res_time = new JsonObject();
            obj_res_time.addProperty("avg", String.valueOf(tmp.get("gcresavg")));
            obj_res_time.addProperty("min", String.valueOf(tmp.get("gcresmin")));
            obj_res_time.addProperty("max", String.valueOf(tmp.get("gcresmax")));

            JsonObject obj_count = new JsonObject();/**/
            obj_count.addProperty("cnt_total", String.valueOf(tmp.get("gctcnt")));
            obj_count.addProperty("cnt_success", String.valueOf(tmp.get("gctscnt")));
            obj_count.addProperty("cnt_fail", String.valueOf(tmp.get("gctfcnt")));
            obj_count.addProperty("cnt_avr_try", String.valueOf(tmp.get("gcavgtry")));
            obj_count.addProperty("cnt_total_try", String.valueOf(tmp.get("gccnttry")));

            // thread 처리한 gm 요청에 대한 통계
            JsonObject objElapsedTime = new JsonObject();
            objElapsedTime.addProperty("max", String.valueOf(tmp.get("max_elapsed_time")));
            objElapsedTime.addProperty("avg", String.valueOf(tmp.get("avg_elapsed_time")));
            objElapsedTime.addProperty("min", String.valueOf(tmp.get("min_elapsed_time")));

            JsonObject obj_result = new JsonObject();
            obj_result.addProperty("date", tmp.get("dno"));
            obj_result.addProperty("dtype", dtype);
            obj_result.add("res_time", obj_res_time);
            obj_result.add("count", obj_count);
            obj_result.add("elapsed_time", objElapsedTime);

            jsonArray.add(obj_result);

        }

        if (!chkError && statList.size() < 1) {
            errorCode = "E0001";
            errorMessage = "no result";
            chkError = true;
        }

        JsonObject json_result = new JsonObject();
        if (chkError) {
            JsonObject error_result = new JsonObject();
            error_result.addProperty("code", errorCode);
            error_result.addProperty("message", errorMessage);
            json_result.add("error_result", error_result);

        } else {

            json_result.add("result", jsonArray);
        }

        LOGGER.info("### [ RESPONSE BODY ] ### \n " + json_result.toString()
                + " \n ###########");

        return new ResponseEntity<String>(json_result.toString(), headers, HttpStatus.OK);
    }
}
