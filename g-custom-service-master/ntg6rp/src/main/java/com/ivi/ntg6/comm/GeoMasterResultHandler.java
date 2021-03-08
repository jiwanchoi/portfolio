package com.ivi.ntg6.comm;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ivi.ntg6.service.SplitThread;
import com.ivi.ntg6.service.vo.GeoMasterResponse;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GeoMasterResultHandler {
    private static final Logger logger = LogManager.getLogger(GeoMasterResultHandler.class);

    /**
     * GeoMaster result 들을 하나로 묶어준다.
     *
     * @param geoMasterResponseList
     * @return
     */
    public static String mergeRoutes(List<GeoMasterResponse> geoMasterResponseList) {
        if (geoMasterResponseList == null || geoMasterResponseList.size() < 1) {
            return null;
        }

        String result = "";
        try {

            Map<String, Object> resMap = new HashMap<>();
            ObjectMapper mapper = new ObjectMapper();
            resMap = mapper.readValue(((GeoMasterResponse) geoMasterResponseList.get(0)).responseJsonMsg, Map.class);
            String gCustomTransactionId = "";
            Map<String, Object> gcustom = new HashMap<>();
            gCustomTransactionId = geoMasterResponseList.get(0).gCustomTransactionId;
            gcustom.put("gc_transaction_id", gCustomTransactionId);

            for (int i = 1; i < geoMasterResponseList.size(); i++) {
                Map<String, Object> forMap = new HashMap<>();
                String responseJsonMsg = geoMasterResponseList.get(i).responseJsonMsg;
                // GeoMaster의 response 에 error message 가 존재하면, 응답처리를 종료하고 에러를 반환한다.
                if (responseJsonMsg.contains("error_result")) {
                    resMap = mapper.readValue(responseJsonMsg, Map.class);
                    gcustom.put("st", geoMasterResponseList.get(i).st);
                    gcustom.put("dt", geoMasterResponseList.get(i).dt);
                    gcustom.put("crs", geoMasterResponseList.get(i).crs);
                    break;
                }
                forMap = mapper.readValue(responseJsonMsg, Map.class);


                //머지작업~~
                if (resMap != null) {

                    Long timestamp_s = (Long) resMap.get("timestamp");
                    Long timestamp_e = (Long) forMap.get("timestamp");
                    resMap.put("timestamp", (timestamp_s + timestamp_e)); //timestamp 합치기

                    Map<String, Object> map_s = (Map<String, Object>) resMap.get("result");
                    Map<String, Object> map_e = (Map<String, Object>) forMap.get("result");

                    List<Map<String, Object>> routeMap_s = (List<Map<String, Object>>) map_s.get("routes");
                    List<Map<String, Object>> routeMap_e = (List<Map<String, Object>>) map_e.get("routes");

                    for (int j = 0; j < routeMap_s.size(); j++) { //routetype : 최적,최단,최소 등등.

                        Map<String, Object> routeMap_index_s = routeMap_s.get(j);
                        Map<String, Object> routeMap_index_e = routeMap_e.get(j);
                        List<Map<String, Object>> pathsMap_s = (List<Map<String, Object>>) routeMap_index_s.get("paths");
                        List<Map<String, Object>> pathsMap_e = (List<Map<String, Object>>) routeMap_index_e.get("paths");

                        pathsMap_s.addAll(pathsMap_e); //경로합치기 paths

                        //bounding_box 구하기
                        Map<String, Object> bounding_s = (Map<String, Object>) routeMap_index_s.get("bounding_box");
                        Map<String, Object> bounding_e = (Map<String, Object>) routeMap_index_e.get("bounding_box");
                        Double bounding_s_min_x = (Double) bounding_s.get("min_x");
                        Double bounding_e_min_x = (Double) bounding_e.get("min_x");
                        bounding_s.put("min_x", (bounding_s_min_x > bounding_e_min_x) ? bounding_s_min_x : bounding_e_min_x); //min_x구하기
                        Double bounding_s_min_y = (Double) bounding_s.get("min_y");
                        Double bounding_e_min_y = (Double) bounding_e.get("min_y");
                        bounding_s.put("min_y", (bounding_s_min_y > bounding_e_min_y) ? bounding_s_min_y : bounding_e_min_y); //min_y구하기
                        Double bounding_s_max_y = (Double) bounding_s.get("max_y");
                        Double bounding_e_max_y = (Double) bounding_e.get("max_y");
                        bounding_s.put("max_y", (bounding_s_max_y < bounding_e_max_y) ? bounding_s_max_y : bounding_e_max_y); //max_y구하기
                        Double bounding_s_max_x = (Double) bounding_s.get("max_x");
                        Double bounding_e_max_x = (Double) bounding_e.get("max_x");
                        bounding_s.put("max_x", (bounding_s_max_x < bounding_e_max_x) ? bounding_s_max_x : bounding_e_max_x); //max_x구하기

                        Double total_toll_s = (Double) routeMap_index_s.get("total_toll");
                        Double total_toll_e = (Double) routeMap_index_e.get("total_toll");
                        routeMap_index_s.put("total_toll", (total_toll_s + total_toll_e)); //total_toll 합치기

                        Double total_distance_s = (Double) routeMap_index_s.get("total_distance");
                        Double total_distance_e = (Double) routeMap_index_e.get("total_distance");
                        routeMap_index_s.put("total_distance", (total_distance_s + total_distance_e)); //total_distance 합치기

                        Double total_duration_s = (Double) routeMap_index_s.get("total_duration");
                        Double total_duration_e = (Double) routeMap_index_e.get("total_duration");
                        routeMap_index_s.put("total_duration", (total_duration_s + total_duration_e)); //total_duration 합치기

                        Double total_real_duration_s = (Double) routeMap_index_s.get("total_real_duration");
                        Double total_real_duration_e = (Double) routeMap_index_e.get("total_real_duration");
                        routeMap_index_s.put("total_real_duration", (total_real_duration_s + total_real_duration_e)); //total_real_duration 합치기

                        Double total_pattern_duration_s = (Double) routeMap_index_s.get("total_pattern_duration");
                        Double total_pattern_duration_e = (Double) routeMap_index_e.get("total_pattern_duration");
                        routeMap_index_s.put("total_pattern_duration", (total_pattern_duration_s + total_pattern_duration_e)); //total_pattern_duration 합치기

                    }
                }
            }

            // 모든 response 에 gcustom transaction id 를 기록.
            resMap.put("gcustom", gcustom);
            result = mapper.writeValueAsString(resMap);
            //logger.info("### result ## :::  " + result );

        } catch (Exception ee) {
            logger.info("### merageRp ## :::  " + ee);
        }
        return result;
    }
}
