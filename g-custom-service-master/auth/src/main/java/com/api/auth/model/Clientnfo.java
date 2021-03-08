package com.api.auth.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "tb_rel_client_api")
public class Clientnfo {

    @Id @GeneratedValue
    private String idn_rel;

    @Column(name = "idn_c")
    private String clientid;

    @Column(name = "idn_api")
    private String apino;
    
    private String sdate;
    
    private String edate;
    
    private String udate;
    
    private String useyn;
    
    private String aid;

	public String getIdn_rel() {
		return idn_rel;
	}

	public void setIdn_rel(String idn_rel) {
		this.idn_rel = idn_rel;
	}

	public String getClientid() {
		return clientid;
	}

	public void setClientid(String clientid) {
		this.clientid = clientid;
	}

	public String getApino() {
		return apino;
	}

	public void setApino(String apino) {
		this.apino = apino;
	}

	public String getSdate() {
		return sdate;
	}

	public void setSdate(String sdate) {
		this.sdate = sdate;
	}

	public String getEdate() {
		return edate;
	}

	public void setEdate(String edate) {
		this.edate = edate;
	}

	public String getUdate() {
		return udate;
	}

	public void setUdate(String udate) {
		this.udate = udate;
	}

	public String getUseyn() {
		return useyn;
	}

	public void setUseyn(String useyn) {
		this.useyn = useyn;
	}

	public String getAid() {
		return aid;
	}

	public void setAid(String aid) {
		this.aid = aid;
	}

	  
}