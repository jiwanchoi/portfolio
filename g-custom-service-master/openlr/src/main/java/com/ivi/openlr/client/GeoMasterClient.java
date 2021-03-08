package com.ivi.openlr.client;

import com.fasterxml.jackson.databind.JsonNode;
import com.ivi.openlr.client.vo.GeoMasterRequest;
import com.ivi.openlr.client.vo.GeoMasterResponse;
import com.ivi.openlr.config.GeoMasterConfiguration;
import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import javax.annotation.Resource;
import java.util.Collections;

@Component
public class GeoMasterClient {
    private final Logger logger = LoggerFactory.getLogger(GeoMasterClient.class);

    @Resource
    GeoMasterConfiguration geoMasterConfiguration;

    HttpComponentsClientHttpRequestFactory httpRequestFactory;

    public GeoMasterClient(GeoMasterConfiguration geoMasterConfiguration) {
        this.geoMasterConfiguration = geoMasterConfiguration;

        // RestTemplate 을 사용하기 위한 HttpRequestFacotory 생성.
        httpRequestFactory = new HttpComponentsClientHttpRequestFactory();
        httpRequestFactory.setReadTimeout(5000); // ms
        httpRequestFactory.setConnectTimeout(3000); // ms
        HttpClient httpClient = (HttpClient) HttpClientBuilder.create()
                .setMaxConnTotal(100)
                .setMaxConnPerRoute(10)
                .build();
        httpRequestFactory.setHttpClient(httpClient);
    }


    /**
     * GeoMaster 에 Route 를 요청하고 그 응답을 가져옴.
     * @param geoMasterRequest
     * @return
     */
    public GeoMasterResponse requestRoute(GeoMasterRequest geoMasterRequest) {
        RestTemplate restTemplate = new RestTemplate(httpRequestFactory);

        // build request header
        HttpHeaders requestHeaders = new HttpHeaders();
        requestHeaders.setContentType(MediaType.APPLICATION_JSON);
        requestHeaders.add("Authorization", geoMasterConfiguration.getAuthKey());
        requestHeaders.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        HttpEntity requestEntity = new HttpEntity(geoMasterRequest.getBody(), requestHeaders);

        // build request URL
        String geoMasterUrl = geoMasterConfiguration.getUrl(
                geoMasterRequest.getSt(),
                geoMasterRequest.getDt(),
                geoMasterRequest.getWps(),
                geoMasterRequest.getCrs()
        );

        // Send Request
        JsonNode responseObj = null;
        try {
            responseObj = restTemplate.postForObject(geoMasterUrl, requestEntity, JsonNode.class);

        } catch (RestClientException e) {
            e.printStackTrace();
            logger.error("GeoMaster Request occured Exception : ", e);
        }

        GeoMasterResponse geoMasterResponse = new GeoMasterResponse(geoMasterRequest, responseObj);

        logger.debug("reponseObj : " + responseObj);

        return geoMasterResponse;
    }
}
