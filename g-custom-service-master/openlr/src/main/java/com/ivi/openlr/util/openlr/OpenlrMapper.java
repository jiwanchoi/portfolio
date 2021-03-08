package com.ivi.openlr.util.openlr;

import com.fasterxml.jackson.databind.JsonNode;
import com.ivi.openlr.client.vo.GeoMasterResponse;
import com.ivi.openlr.util.LinkColumn;
import com.ivi.openlr.util.TBTColumn;
import openlr.LocationReference;
import openlr.binary.OpenLRBinaryEncoder;
import openlr.binary.impl.LocationReferencePointBinaryImpl;
import openlr.binary.impl.OffsetsBinaryImpl;
import openlr.rawLocRef.RawLineLocRef;
import openlr.rawLocRef.RawLocationReference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class OpenlrMapper {
    private static final Logger logger = LoggerFactory.getLogger(OpenlrMapper.class);

    public LocationReference encodeData(GeoMasterResponse geoMasterResponse) {
        JsonNode tbts = geoMasterResponse.getResult().at("/result/routes/0/paths/0/guides/0/tbts");
        JsonNode links = geoMasterResponse.getResult().at("/result/routes/0/paths/0/links");

        // tbts null check
        if (tbts.isNull() || tbts.size() < 0) {
            return null;
        }
        // links null check
        if (links.isNull() || links.size() < 0) {
            return null;
        }

        // target binary define
        List<LocationReferencePointBinaryImpl> lineLocRefList= new ArrayList<LocationReferencePointBinaryImpl>();

        for (int i = 0; i < tbts.size(); i++) {

            JsonNode tbt = tbts.get(i);
            double lon = tbt.at("/coord/x").doubleValue();
            double lat = tbt.at("/coord/y").doubleValue();
            // null point exception occurring
            JsonNode link = OpenlrMapperUtils.searchLink(links, lat, lon);
            logger.debug(String.format("cur %s, %s, [%s,%s]",
                    tbt.get("type").numberValue(),
                    tbt.get("name").textValue(),
                    tbt.at("/coord/x").doubleValue(),
                    tbt.at("/coord/y").doubleValue()
                    )
            );
            // 마지막 경로 인지 확인
            boolean isLast = false;
            if ((i+1) == tbts.size()) {
                isLast = true;
            }
            LocationReferencePointBinaryImpl lrp = new LocationReferencePointBinaryImpl(
                    i+1,
                    OpenlrMapperUtils.getFunctionalRoadClass(link.get(LinkColumn.ROAD_CLASS.getField()).numberValue()),
                    OpenlrMapperUtils.getFormOfWay(tbt.get(TBTColumn.TYPE.getField()).intValue()),
                    lat,
                    lon,
                    OpenlrMapperUtils.getBearing(tbt.get(TBTColumn.TYPE.getField()).intValue()),
                    tbt.get(TBTColumn.NEXT_DISTANCE.getField()).intValue(),
                    OpenlrMapperUtils.getFunctionalRoadClass(link.get(LinkColumn.ROAD_CLASS.getField()).numberValue()),
                    isLast
            );
            logger.debug(String.format("[%02d] build LocationReferencePointBinaryImpl : %s", i, lrp.toString()));
            // LineLocation 목록을 작성.
            lineLocRefList.add(lrp);
        }
        // dummy offset
        OffsetsBinaryImpl offsetsBinary = new OffsetsBinaryImpl(0, 0);
        RawLineLocRef rawLineLocRef = new RawLineLocRef("1", lineLocRefList, offsetsBinary);

        OpenLRBinaryEncoder openLRBinaryEncoder = new OpenLRBinaryEncoder();
        LocationReference lr = openLRBinaryEncoder.encodeData(rawLineLocRef);
        return lr;
    }
}
