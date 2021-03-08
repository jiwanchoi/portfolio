package com.ivi.ntg6.controller.model;

import org.apache.commons.lang.BooleanUtils;
import org.springframework.data.mongodb.core.aggregation.ConvertOperators;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.Map;

public class Ntg6rpParam {
    private final String method;
    private final String remoteHost;
    private final String gcAuth;
    private final String st;
    private final String dt;
    private final String crs;
    private final String wps;
    private final boolean single;
    private final Map<String, Object> body;

    public Ntg6rpParam(String method, String remoteHost, String headerGcAuth, String st, String dt, String crs, String wps, String single, Map<String, Object> reqBody) throws UnsupportedEncodingException {
        this.method = method;
        this.remoteHost = remoteHost;
        if (headerGcAuth != null)
            this.gcAuth = headerGcAuth;
        else
            this.gcAuth = null;
        this.st = URLDecoder.decode(st, StandardCharsets.UTF_8.displayName());
        this.dt = URLDecoder.decode(dt, StandardCharsets.UTF_8.displayName());
        this.crs = URLDecoder.decode(crs, StandardCharsets.UTF_8.displayName());
        if (wps != null)
            this.wps = URLDecoder.decode(wps, StandardCharsets.UTF_8.displayName());
        else
            this.wps = null;
        if (single != null)
            this.single = toBool(single);
        else
            this.single = false;
        if (reqBody != null)
            this.body = reqBody;
        else
            this.body = null;
    }

    private boolean toBool(String single) {
        return BooleanUtils.toBoolean(single);
    }

    public String getMethod() {
        return method;
    }

    public String getRemoteHost() {
        return remoteHost;
    }

    public String getGcAuth() {
        return gcAuth;
    }

    public String getSt() {
        return st;
    }

    public String getDt() {
        return dt;
    }

    public String getCrs() {
        return crs;
    }

    public String getWps() {
        return wps;
    }

    /**
     * wps 가 여러개일 경우에 단일 request 로 GM에 발송하고 싶은 경우 옵션.
     * true : 단일 리퀘스트로 처리
     * false : 복수 리퀘스트로 처리
     * @return
     */
    public boolean isSingle() {
        return single;
    }

    public Map<String, Object> getBody() {
        return body;
    }

    /**
     * wps count 를 String 형태로 반환
     * @return
     */
    public String getWpsCount() {
        return String.valueOf(countWps());
    }

    /**
     * WPS (경유지) 의 수를 계산하여 반환.
     * @return
     */
    public int countWps() {
        return (wps != null) ? wps.split("\\;+").length : 0;
    }
}
