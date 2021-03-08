package com.ivi.openlr.service;

import com.ivi.openlr.client.GeoMasterClient;
import com.ivi.openlr.client.vo.GeoMasterRequest;
import com.ivi.openlr.client.vo.GeoMasterResponse;
import com.ivi.openlr.util.openlr.OpenlrMapper;
import openlr.LocationReference;
import openlr.OpenLRProcessingException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import sun.misc.BASE64Encoder;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Base64;

@Service
public class OpenlrService {
    private final Logger logger = LoggerFactory.getLogger(OpenlrService.class);

    final
    GeoMasterClient geoMasterClient;

    final
    OpenlrMapper openlrMapper;

    public OpenlrService(GeoMasterClient geoMasterClient, OpenlrMapper openlrMapper) {
        this.geoMasterClient = geoMasterClient;
        this.openlrMapper = openlrMapper;
    }


    /**
     * GeoMaster 에 데이터를 요청해서 OpenLR Binary 로 Response 한다.
     * @param geoMasterRequest
     * @return
     */
    public String requestWith(GeoMasterRequest geoMasterRequest) throws IOException, OpenLRProcessingException {
        logger.debug("requestWith DEBUG......");

        // GM 에 결과를 가져온다
        GeoMasterResponse geoMasterResponse = geoMasterClient.requestRoute(geoMasterRequest);
        // OpenLR Binary 로 변환
        LocationReference lr = openlrMapper.encodeData(geoMasterResponse);

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        lr.toStream(outputStream);

        Base64.Encoder encoder = Base64.getEncoder();
        logger.debug(String.format("response length : %s", outputStream.size()));
        logger.debug("output stream : " + encoder.encodeToString(outputStream.toByteArray()));

        logger.debug("requestWith DEBUG end......");
        return encoder.encodeToString(outputStream.toByteArray());
    }
}
