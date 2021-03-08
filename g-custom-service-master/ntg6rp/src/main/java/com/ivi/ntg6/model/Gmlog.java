package com.ivi.ntg6.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

//MONGO DB 
@Document(collection = "gmlog")
public class Gmlog {

    @Id
    private String id;

    private String mid; // gclog id (parent key)
    private String gc_tid;
    private String gm_tid;
    private String gmkey;
    private String gmreq;
    private String gmres;
    private String st;
    private String dt;
    private String wps;
    private String crs;
    private String gmreqdata;
    private String gmresdata;
    private String gmstat;
    private String status;
    private Long gm_elapse;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Long getGm_elapse() {
        return gm_elapse;
    }

    public void setGm_elapse(Long gm_elapse) {
        this.gm_elapse = gm_elapse;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getMid() {
        return mid;
    }

    public void setMid(String mid) {
        this.mid = mid;
    }

    public String getGc_tid() {
        return gc_tid;
    }

    public void setGc_tid(String gc_tid) {
        this.gc_tid = gc_tid;
    }

    public String getGm_tid() {
        return gm_tid;
    }

    public void setGm_tid(String gm_tid) {
        this.gm_tid = gm_tid;
    }

    public String getGmkey() {
        return gmkey;
    }

    public void setGmkey(String gmkey) {
        this.gmkey = gmkey;
    }

    public String getGmreq() {
        return gmreq;
    }

    public void setGmreq(String gmreq) {
        this.gmreq = gmreq;
    }

    public String getGmres() {
        return gmres;
    }

    public void setGmres(String gmres) {
        this.gmres = gmres;
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

    public String getGmreqdata() {
        return gmreqdata;
    }

    public void setGmreqdata(String gmreqdata) {
        this.gmreqdata = gmreqdata;
    }

    public String getGmresdata() {
        return gmresdata;
    }

    public void setGmresdata(String gmresdata) {
        this.gmresdata = gmresdata;
    }

    public String getGmstat() {
        return gmstat;
    }

    public void setGmstat(String gmstat) {
        this.gmstat = gmstat;
    }
}
