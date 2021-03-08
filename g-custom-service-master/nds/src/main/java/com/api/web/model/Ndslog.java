package com.api.web.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;

@Entity
@SequenceGenerator(
	name="NDSLOG_SEQ_GENERATOR",
	sequenceName="NDSLOG_SEQ",
	initialValue=1,
	allocationSize = 1
)	
public class Ndslog {
	
	@Id 
	@Column(name = "idn")
	@GeneratedValue(strategy = GenerationType.SEQUENCE,
					generator = "NDSLOG_SEQ_GENERATOR")
    private Long ID;

    private String lat;

    private String lon;
    
    private String lev;

    private String crs;
    
    private String tiley;

    private String tilex;
    
    private String regdate;

	

	public String getRegdate() {
		return regdate;
	}

	public void setRegdate(String regdate) {
		this.regdate = regdate;
	}

	public Long getID() {
		return ID;
	}

	public void setID(Long iD) {
		ID = iD;
	}

	public String getLat() {
		return lat;
	}

	public void setLat(String lat) {
		this.lat = lat;
	}

	public String getLon() {
		return lon;
	}

	public void setLon(String lon) {
		this.lon = lon;
	}

	public String getLev() {
		return lev;
	}

	public void setLev(String lev) {
		this.lev = lev;
	}

	public String getCrs() {
		return crs;
	}

	public void setCrs(String crs) {
		this.crs = crs;
	}

	public String getTiley() {
		return tiley;
	}

	public void setTiley(String tiley) {
		this.tiley = tiley;
	}

	public String getTilex() {
		return tilex;
	}

	public void setTilex(String tilex) {
		this.tilex = tilex;
	}	
}
