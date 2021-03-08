package com.ivi.ntg6.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

//MONGO DB 
@Document(collection = "gclog")
public class Gclog {

    @Id
    private String id;

    private String gc_tid;
    private String gckey;
    private String cip;
    private String gcsvc;
    private String gcreq;
    private String gcres;
    private String st;
    private String dt;
    private String wps;
    private String crs;
    private String gcreqdata;
    private String gcresdata;
    private String gcercode;
    private int gctrycnt;
    private long gc_elapse;

    public long getGc_elapse() {
        return gc_elapse;
    }

    public void setGc_elapse(long gc_elapse) {
        this.gc_elapse = gc_elapse;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getGc_tid() {
        return gc_tid;
    }

    public void setGc_tid(String gc_tid) {
        this.gc_tid = gc_tid;
    }

    public String getGckey() {
        return gckey;
    }

    public void setGckey(String gckey) {
        this.gckey = gckey;
    }

    public String getCip() {
        return cip;
    }

    public void setCip(String cip) {
        this.cip = cip;
    }

    public String getGcsvc() {
        return gcsvc;
    }

    public void setGcsvc(String gcsvc) {
        this.gcsvc = gcsvc;
    }

    public String getGcreq() {
        return gcreq;
    }

    public void setGcreq(String gcreq) {
        this.gcreq = gcreq;
    }

    public String getGcres() {
        return gcres;
    }

    public void setGcres(String gcres) {
        this.gcres = gcres;
    }

    public String getSt() {
        return st;
    }

    public void setSt(String st) {
        this.st = st;
    }

    public String getDt() {
        return dt;
    }

    public void setDt(String dt) {
        this.dt = dt;
    }

    public String getWps() {
        return wps;
    }

    public void setWps(String wps) {
        this.wps = wps;
    }

    public String getCrs() {
        return crs;
    }

    public void setCrs(String crs) {
        this.crs = crs;
    }

    public String getGcreqdata() {
        return gcreqdata;
    }

    public void setGcreqdata(String gcreqdata) {
        this.gcreqdata = gcreqdata;
    }

    public String getGcresdata() {
        return gcresdata;
    }

    public void setGcresdata(String gcresdata) {
        this.gcresdata = gcresdata;
    }

    public String getGcercode() {
        return gcercode;
    }

    public void setGcercode(String gcercode) {
        this.gcercode = gcercode;
    }

    public int getGctrycnt() {
        return gctrycnt;
    }

    public void setGctrycnt(int gctrycnt) {
        this.gctrycnt = gctrycnt;
    }
}
