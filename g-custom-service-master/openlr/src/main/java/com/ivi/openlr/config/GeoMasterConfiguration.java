package com.ivi.openlr.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.web.util.UriComponentsBuilder;

@Configuration
@PropertySource("classpath:geomaster.properties")
public class GeoMasterConfiguration {
    @Value("${geomaster.ntg6.url}")
    String url;

    @Value("${geomaster.auth.key}")
    String authKey;

    public String getUrl() {
        return url;
    }

    public String getAuthKey() {
        return authKey;
    }

    public String getUrl(String st, String dt, String wps, String crs) {
        UriComponentsBuilder uribuilder = UriComponentsBuilder.fromHttpUrl(url);
        uribuilder.queryParam("st", st);
        uribuilder.queryParam("dt", dt);
        if (wps != null)
            uribuilder.queryParam("wps", wps);
        uribuilder.queryParam("crs", crs);
        return uribuilder.toUriString();
    }
}
