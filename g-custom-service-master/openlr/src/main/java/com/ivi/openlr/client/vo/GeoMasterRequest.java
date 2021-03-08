package com.ivi.openlr.client.vo;

import com.fasterxml.jackson.databind.JsonNode;

public class GeoMasterRequest {
    private final String st;
    private final String dt;
    private final String wps;
    private final String crs;
    private final JsonNode body;

    public GeoMasterRequest(String st, String dt, String wps, String crs, JsonNode requestBody) {
        this.st = st;
        this.dt = dt;
        this.wps = wps;
        this.crs = crs;
        this.body = requestBody;
    }

    public String getSt() {
        return st;
    }

    public String getDt() {
        return dt;
    }

    public String getWps() {
        return wps;
    }

    public String getCrs() {
        return crs;
    }

    public JsonNode getBody() {
        return body;
    }
}
