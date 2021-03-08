package com.ivi.openlr.client.vo;

import com.fasterxml.jackson.databind.JsonNode;

public class GeoMasterResponse {
    private final GeoMasterRequest geoMasterRequest;
    private final JsonNode result;

    public GeoMasterResponse(GeoMasterRequest geoMasterRequest, JsonNode responseObj) {
        this.geoMasterRequest = geoMasterRequest;
        this.result = responseObj;
    }

    public GeoMasterRequest getGeoMasterRequest() {
        return geoMasterRequest;
    }

    public JsonNode getResult() {
        return result;
    }
}
