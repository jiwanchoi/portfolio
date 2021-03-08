package com.ivi.ntg6.service.vo;

import org.springframework.boot.json.JsonParser;
import org.springframework.boot.json.JsonParserFactory;

public class GeoMasterResponse {
    public final String gCustomTransactionId;
    public final String geoMasterTransactionId;
    public final String responseJsonMsg;
    public String st;
    public String dt;
    public String wps;
    public String crs;
    public String reqParam;
    public String responseStatus;

    public GeoMasterResponse(String st, String dt, String wps, String crs, String reqParam, String responseStatus, String responseJsonMsg, String gCustomTransactionId) {
        this.st = st;
        this.dt = dt;
        this.wps = wps;
        this.crs = crs;
        this.reqParam = reqParam;
        this.responseStatus = responseStatus;
        this.responseJsonMsg = responseJsonMsg;
        this.gCustomTransactionId = gCustomTransactionId;
        JsonParser jsonParser = JsonParserFactory.getJsonParser();
        this.geoMasterTransactionId = jsonParser.parseMap(responseJsonMsg).get("transaction_id").toString();
    }
}
