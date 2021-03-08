package com.api.web.service;

import org.osgeo.proj4j.CRSFactory;
import org.osgeo.proj4j.CoordinateReferenceSystem;
import org.osgeo.proj4j.CoordinateTransform;
import org.osgeo.proj4j.CoordinateTransformFactory;
import org.osgeo.proj4j.ProjCoordinate;


public class NdsService {
	
	private String S_UTMK = "+proj=tmerc +lat_0=38 +lon_0=127.5 +k=0.9996 +x_0=1000000 +y_0=2000000 +ellps=GRS80 +units=m +no_defs";
	private String S_KATEC = "+proj=tmerc +lat_0=38 +lon_0=128 +k=0.9999 +x_0=400000 +y_0=600000 +ellps=bessel +units=m +no_defs +towgs84=-115.80,474.99,674.11,1.16,-2.31,-1.63,6.43";
	private String S_WGS84 = "+proj=longlat +datum=WGS84 +no_defs";
	private String S_CRS = "";	 
	  
	public NdsService(String sourceCRS) throws Exception {
		System.out.println("sourceCRS"+sourceCRS);
		try {
			if(sourceCRS.equals("UTMK")) {
				this.S_CRS = this.S_UTMK;
			}else if(sourceCRS.equals("KATEC")) {
				this.S_CRS = this.S_KATEC;
			}else {
				this.S_CRS = this.S_WGS84;
			}	
	    } catch (Exception e) {
	    	this.S_CRS = this.S_WGS84;
	    } finally {
	    	
		}				
		System.out.println("S_CRS"+this.S_CRS );
	}
	
	public ProjCoordinate transform(double x, double y) throws IllegalStateException {
		ProjCoordinate p2 = new ProjCoordinate();
		try {
			CoordinateTransformFactory ctFactory = new CoordinateTransformFactory();
	        CRSFactory csFactory = new CRSFactory();	        
	        CoordinateReferenceSystem source_CRS = csFactory.createFromParameters("S_CRS",this.S_CRS);
	        CoordinateReferenceSystem WGS84 = csFactory.createFromParameters("WGS84",this.S_WGS84);
	        
	        CoordinateTransform trans = ctFactory.createTransform(source_CRS, WGS84);
	        ProjCoordinate p = new ProjCoordinate();
	        	            	
	        p.x = x;
	        p.y = y;
	        trans.transform(p, p2);	        
	        System.out.println("p2 : "+p2.toString());
	        System.out.println("p2x : "+p2.x);
	        System.out.println("p2y : "+p2.y);	       
	    } catch (IllegalStateException e) {
	    	//p2.x = 0;
	    	//p2.y = 0;
	    } finally {
	    	
		}
		
		return p2;
	}
}
