package com.ivi.openlr.util.openlr;

import com.fasterxml.jackson.databind.JsonNode;
import openlr.map.FormOfWay;
import openlr.map.FunctionalRoadClass;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import static java.lang.Math.round;

public class OpenlrMapperUtils {
    public static final List<Integer> UNDEFINED = Arrays.asList(
            9001
    );
    /**
     * 자동차 전용도로
     */
    public static final List<Integer> MOTORWAY = Arrays.asList(
            1701, 1702, 1703, 1704, 1705
    );
    /**
     * 복선
     */
    public static final List<Integer> MULTIPLE_CARRIAGEWAY = Arrays.asList(
            1301, 1302, 1303, 1304, 1305,
            1401, 1042, 1043,
            1501, 1502);
    /**
     * 단선
     */
    public static final List<Integer> SINGLE_CARRIAGEWAY = Arrays.asList(
            201, 202, 203, 204, 1001, 1002
    );
    /**
     * 회전교차로
     */
    public static final List<Integer> ROUNDABOUT = Arrays.asList(
            2001, 2002, 2003, 2004, 2005, 2006, 2007,
            2008, 2009, 2010, 2012
    );
    /**
     * 사거리
     */
    public static final List<Integer> TRAFFICSQUARE = Arrays.asList(
            1101, 1102, 1103, 1104, 1105,
            1201, 1202, 1203, 1204, 1205
    );
    /**
     * 고속도로
     */
    public static final List<Integer> SLIPROAD = Arrays.asList(
            1601, 1602, 1603, 1604, 1605,
            1801, 1802, 1803
    );
    public static final List<Integer> OTHER = Arrays.asList(
            1901
    );

    public static JsonNode searchLink(JsonNode links, double latitude, double longitude) {

        Iterator<JsonNode> linksIterator = links.iterator();
        while(linksIterator.hasNext()) {
            JsonNode item = linksIterator.next();

            // 위도 latitude, 경도 longitude 에서 값을 찾음.
            JsonNode vertices = item.get("vertices");
            Iterator<JsonNode> vertice = vertices.iterator();
            while(vertice.hasNext()) {
                JsonNode coordinate = vertice.next();
                if ((coordinate.get("x").doubleValue() == longitude)
                        & (coordinate.get("y").doubleValue() == latitude)) {
                    return item;
                }
            }
        }
        return null;
    }

    /**
     * FunctionalRoadClass 를 OpenLR에 맞춰 변환, OpenLR 은 0~7 까지 임으로 road_class -1 한 값을 반환한다.
     * @param road_class
     * @return
     */
    public static FunctionalRoadClass getFunctionalRoadClass(Number road_class) {
        assert road_class != null;

        int frc = road_class.intValue() -1;
        switch (frc) {
            case 0:
                return FunctionalRoadClass.FRC_0;
            case 1:
                return FunctionalRoadClass.FRC_1;
            case 2:
                return FunctionalRoadClass.FRC_2;
            case 3:
                return FunctionalRoadClass.FRC_3;
            case 4:
                return FunctionalRoadClass.FRC_4;
            case 5:
                return FunctionalRoadClass.FRC_5;
            case 6:
                return FunctionalRoadClass.FRC_6;
            case 7:
                return FunctionalRoadClass.FRC_7;
        }

        return null;
    }

    /**
     * road_class 를 기반으로 FOW 를 산출 (적절하게?)
     * @param tbt_type
     * @return
     */
    public static FormOfWay getFormOfWay(int tbt_type) {
        if (UNDEFINED.contains(tbt_type)) {
            return FormOfWay.UNDEFINED;
        }
        if (OTHER.contains(tbt_type)) {
            return FormOfWay.OTHER;
        }
        if (MOTORWAY.contains(tbt_type)) {
            return FormOfWay.MOTORWAY;
        }
        if (MULTIPLE_CARRIAGEWAY.contains(tbt_type)) {
            return FormOfWay.MULTIPLE_CARRIAGEWAY;
        }
        if (SINGLE_CARRIAGEWAY.contains(tbt_type)) {
            return FormOfWay.SINGLE_CARRIAGEWAY;
        }
        if (ROUNDABOUT.contains(tbt_type)) {
            return FormOfWay.ROUNDABOUT;
        }
        if (TRAFFICSQUARE.contains(tbt_type)) {
            return FormOfWay.TRAFFIC_SQUARE;
        }
        if (SLIPROAD.contains(tbt_type)) {
            return FormOfWay.SLIPROAD;
        }

        return FormOfWay.UNDEFINED;
    }

    /**
     * Bearing GM to OpenLR
     * GM 1-12 까지 30도 단위로 나뉘어짐.
     * OpenLR 은 11.25도 단위로 총 32개로 나뉨.
     * @param tbt_type
     * @return 0 ~ 32 사이의 값 (360 / 11.25)
     */
    public static int getBearing(int tbt_type) {
        final double radius = 11.25;
        int result = 0;
        if (tbt_type < 1000) {
            result = 0;
        }
        if (tbt_type > 1000 && tbt_type < 1100) {
            result =  0;
        }
        if (tbt_type > 1100 && tbt_type < 1200) {
            result = (tbt_type % 10) * 30;
        }
        if (tbt_type >1200 && tbt_type < 1300) {
            result = ((tbt_type % 10) + 6) * 30;
        }
        if (tbt_type > 1300 && tbt_type < 1400) {
            switch (tbt_type % 10) {
                case 1: // straight
                    result = 0;
                    break;
                case 2: // right
                case 4: // right
                    result = 3 * 30;
                    break;
                case 3: // left
                case 5: // left
                    result = 9 * 30;
                    break;
            }
        }
        if (tbt_type > 1400 && tbt_type < 1500) {
            switch (tbt_type % 10) {
                case 1: // enter underpass in the straght direction
                    result = 0;
                    break;
                case 2: // side road of underpass in the right side
                    result = 90;
                    break;
                case 3: // side road of underpass in the left side
                    result = 270;
                    break;
            }
        }
        if (tbt_type > 1500 && tbt_type < 1600) {
            switch (tbt_type % 10) {
                case 1: // right road
                    result = 90;
                    break;
                case 2: // left road
                    result = 270;
                    break;
            }
        }
        if (tbt_type > 1600 && tbt_type < 1800) {
            switch ((int)tbt_type % 10) {
                case 1:
                    result = 0;
                    break;
                case 2: // right
                case 4:
                    result = 90;
                    break;
                case 3: // left
                case 5:
                    result = 270;
                    break;
            }
        }
        if (tbt_type == 1901) {
            result = 180;
        }
        if (tbt_type > 2000 && tbt_type < 3000) {
            result = (tbt_type % 10) * 30;
        }

        if (result > 0) {
            result = (int) (result / radius);
        }
        return result;
    }
}
