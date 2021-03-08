package com.ivi.openlr.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.ivi.openlr.client.vo.GeoMasterRequest;
import com.ivi.openlr.service.OpenlrService;
import openlr.OpenLRProcessingException;
import openlr.PhysicalFormatException;
import openlr.binary.ByteArray;
import openlr.binary.OpenLRBinaryDecoder;
import openlr.binary.impl.LocationReferenceBinaryImpl;
import openlr.rawLocRef.RawLineLocRef;
import org.bson.internal.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

import java.io.IOException;
import java.util.Optional;

import static com.ivi.openlr.util.Debug.debugBase64BinaryString;
import static com.ivi.openlr.util.Debug.debugPostLineLocation;

@RestController
public class OpenlrController {
    private final Logger logger = LoggerFactory.getLogger(OpenlrController.class);

    private final OpenlrService openlrService;

    public OpenlrController(OpenlrService openlrService) {
        this.openlrService = openlrService;
    }

    @GetMapping("/main")
    public ResponseEntity mainTemporaryAPI(HttpServletRequest request) {
        logger.debug("### test mainTemporaryAPI start....");
        logger.debug("### test mainTemporaryAPI end....");
        return null;
    }

    /**
     * OpenLR 의 RP 요청 리쿼스트 응답
     *
     * @param request POST 방식만 받는다.
     * @param st      출발지 좌표값 ("st=958388,1941453")
     * @param dt      목적지 좌표값 ("dt=965899,192829")
     * @param crs     좌표계 (기본 "crs=WGS84")
     * @param wps     경유지 ("wps=v965899,1928929+965899,1928929;" or "wps=p1123")
     * @return GM 에서 돌아온 응답을 Binary 형태로 반환한다.
     */
    @PostMapping("/linelocation")
    public ResponseEntity lineLocation(
            HttpServletRequest request,
            @RequestParam(name = "st", required = true) String st,
            @RequestParam(name = "dt", required = true) String dt,
            @RequestParam(name = "crs", required = true, defaultValue = "WGS84") String crs,
            @RequestParam(name = "wps", required = false) String wps,
            @RequestBody(required = false) JsonNode requestBody
    ) {
        ResponseEntity responseEntity = null;
        logger.debug("### linelocation method start...");

        // DEBUG 코드
        String debugPostLineLocation = debugPostLineLocation(logger, st, dt, wps, crs, requestBody);
        responseEntity = new ResponseEntity(debugPostLineLocation, HttpStatus.OK);

        // 요청을 생성한다
        GeoMasterRequest geoMasterRequest = new GeoMasterRequest(st, dt, wps, crs, requestBody);
        try {
            String base64EncodedBinaryStream = openlrService.requestWith(geoMasterRequest);
            if (base64EncodedBinaryStream.length() > 0) {
                ObjectMapper objectMapper = new ObjectMapper();
                ObjectNode openlrNode = objectMapper.createObjectNode();
                openlrNode.put("request", requestBody);
                openlrNode.put("result_openlr", base64EncodedBinaryStream);
                responseEntity = new ResponseEntity(openlrNode.toString(), HttpStatus.OK);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (OpenLRProcessingException e) {
            e.printStackTrace();
        }


        logger.debug("### linelocation method end...");

        return responseEntity;
    }

    @PostMapping("/linelocation/decode")
    public ResponseEntity lineLocationDebug(
            @RequestBody String base64EncodedBinary
    ) throws PhysicalFormatException {
        logger.info("### LineLocationDebug method start...");

        debugBase64BinaryString(logger, base64EncodedBinary);

        byte [] openlrBytes = Base64.decode(base64EncodedBinary);
        OpenLRBinaryDecoder decoder = new OpenLRBinaryDecoder();
        LocationReferenceBinaryImpl locationReferenceBinary = new LocationReferenceBinaryImpl("1", new ByteArray(openlrBytes));
        RawLineLocRef rawLocRef = (RawLineLocRef) decoder.decodeData(locationReferenceBinary);
        StringBuffer result = new StringBuffer();
        for (int i = 0; i < rawLocRef.getLocationReferencePoints().size(); i++) {
            logger.debug(String.format("[%02d] %s", i, rawLocRef.getLocationReferencePoints().get(i).toString()));
            result.append(String.format("[%02d] %s", i, rawLocRef.getLocationReferencePoints().get(i).toString()));
            result.append("\n");
        }

        if (result.length() > 0) {
            return new ResponseEntity(result.toString(), HttpStatus.OK);
        }

        logger.info("### LineLocationDebug method end...");
        return ResponseEntity.of(Optional.empty());
    }
}
