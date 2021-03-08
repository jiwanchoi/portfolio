package com.ivi.ntg6.service;

import com.ivi.ntg6.controller.model.Ntg6rpParam;
import com.ivi.ntg6.mapper.Ntg6Mapper;
import com.ivi.ntg6.model.Gclog;
import com.ivi.ntg6.model.Gmlog;
import com.ivi.ntg6.model.LogData;
import com.ivi.ntg6.service.vo.ResultElapsedTime;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
@Transactional
public class LogService {

    private static final Logger logger = LogManager.getLogger(LogService.class);

    @Autowired
    private PropertiesWithJavaConfig config;

    @Autowired
    private Ntg6Mapper mapper;

    private final SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmssSSS");

    //RPLOG DATA
    public LogData setLogData(String gCustomTransactionId, Ntg6rpParam params,
                              Map<String, Object> resMap, Map<String, Object> reqBodys,
                              String httpStatus, String errorCode,
                              Long GC_REQ, Long GC_RES, Long GM_REQ, Long GM_RES, int retry, String mid,
                              ResultElapsedTime elapsedTime) {
        LogData logData = new LogData();

        // LOG SET
        logData.setMID(mid);//mongdb ID
        logData.setGCKEY(params.getGcAuth());
        logData.setCIP(params.getRemoteHost());

//		logData.setGC_TID("트랜잭션ID ZIPKIN");
//        logData.setGC_TID((String) resMap.get("transaction_id"));
        logData.setGC_TID(gCustomTransactionId);
        logData.setGCSVC("NTG6");

        logData.setGM_TID((String) resMap.get("transaction_id"));
        logData.setGMKEY(config.getGmApiKey());

        String gmreq = sdf.format(new Timestamp(GM_REQ));
        logData.setGMREQ(gmreq);

        if (resMap.get("timestamp") != null) {
            String gmres = sdf.format(new Timestamp(GM_RES));
            logData.setGMRES(gmres);
        }
        if (GM_REQ != null && GM_RES != null) {
            logData.setGMDELAY(GM_RES - GM_REQ);
        }

        logData.setGMSTAT(httpStatus);

        logData.setREQST(params.getSt());
        logData.setREQDT(params.getDt());

        // wps 의 구분자는 ';'
        logData.setREQWPCNT(params.getWpsCount());

        if (reqBodys != null) {
            ArrayList<Map> arvid_area = (ArrayList<Map>) reqBodys.get("avoid_area");
            int req_avid_cnt = arvid_area == null ? 0 : arvid_area.size();
            logData.setREQAVCNT(req_avid_cnt + "");
        }

        if (resMap != null) {
            Map<String, Object> resultMap = (Map<String, Object>) resMap.get("result");

            if (resultMap != null) {

                List<Map<String, Object>> routeMap = (List<Map<String, Object>>) resultMap.get("routes");

//        		System.out.println(routeMap.toString());

                logData.setRES_TDIS((Double) routeMap.get(0).get("total_distance"));
                logData.setRES_TDUR((Double) routeMap.get(0).get("total_duration"));
                logData.setRES_TRDUR((Double) routeMap.get(0).get("total_real_duration"));
                logData.setRES_TTOL((Double) routeMap.get(0).get("total_pattern_duration"));

                logData.setRES_ROUTE_TYPE(routeMap.get(0).get("route_type") + "");
                logData.setRES_CNT(routeMap.size());
            }
        }

        logData.setGCERCODE(errorCode);
        logData.setGCTRYCNT(retry);

        String gcreq = sdf.format(new Timestamp(GC_REQ));
        logData.setGCREQ(gcreq);
        String gcres = sdf.format(new Timestamp(GC_RES));
        logData.setGCRES(gcres);

        if (GC_REQ != null && GC_RES != null) {
            logData.setGCDELAY(GC_RES - GC_REQ);
        }

        logData.maxElapsedTime = elapsedTime.getMax();
        logData.avgElapsedTime = elapsedTime.getAverage();
        logData.minElapsedTime = elapsedTime.getMin();

        return logData;
    }


    public void insertLogData(Map data) {
        mapper.putData(data);
    }

    public List<Map<String, String>> getList() {
        return mapper.getList();
    }

    public void updateData(Map data) {
        mapper.updateData(data);
    }

    public void insertLogData(LogData data) {
        mapper.insertLogData(data);
    }

    //////////////////////////////////////////////Monggo DB ///////////////////////////////////////
    @Autowired
    private MongoTemplate mongoTemplete;

    public Gclog setGcLog(String gCustomTransactionId, Ntg6rpParam rpParam, Map<String, Object> resMap, String httpStatus, String errorCode,
                          Long GC_REQ, Long GC_RES, Long GM_REQ, Long GM_RES, int retry) {
        Gclog gclog = new Gclog();

//		gclog.setGc_tid("트랜잭션ID ZIPKIN");
        gclog.setGc_tid(gCustomTransactionId);
        gclog.setGckey(rpParam.getGcAuth());
        gclog.setCip(rpParam.getRemoteHost());
        gclog.setGcsvc("NTG6");
        String gcreq = sdf.format(new Timestamp(GC_REQ));
        gclog.setGcreq(gcreq);
        String gcres = sdf.format(new Timestamp(GC_RES));
        gclog.setGcres(gcres);
        gclog.setGc_elapse(GC_RES-GC_REQ);
        gclog.setSt(rpParam.getSt());
        gclog.setDt(rpParam.getDt());
        gclog.setWps(rpParam.getWps());
        gclog.setCrs(rpParam.getCrs());
        String gcreqdata = "";
        if (rpParam.getBody() != null) {
            gcreqdata = rpParam.getBody().toString();
            //System.out.println("##gcreqdata]## : "+gcreqdata);
        }
        gclog.setGcreqdata(gcreqdata);

        String gcresdata = "";
        if (resMap != null) {
            gcresdata = resMap.toString();
            //System.out.println("##gcresdata]## : "+gcresdata);
        }
        gclog.setGcresdata(gcresdata);

        gclog.setGcercode(errorCode);
        gclog.setGctrycnt(retry);

        return gclog;
    }

    //MoNGO Gclog
    public String insertGclog(Gclog gclog) {
        String result = "";
        mongoTemplete.insert(gclog);
        //insert data return id
        logger.debug("##[gclog getID] :: " + gclog.getId());
        result = gclog.getId();
        return result;
    }

    //GmLOG
    public Gmlog setGmLog(String gCustomTransactionId, String geoMasterTransactionID,
                          String st, String dt, String wps, String crs,
                          String requestBody, String responseData, String httpStatus,
                          String errorCode, Long GC_REQ, Long GC_RES, Long GM_REQ, Long GM_RES, String mid) {
        Gmlog gmlog = new Gmlog();

        gmlog.setGc_tid(gCustomTransactionId);
        gmlog.setGm_tid(geoMasterTransactionID);
        gmlog.setMid(mid);
        gmlog.setGmkey(config.getGmApiKey());
        String gmreq = sdf.format(new Timestamp(GM_REQ));
        gmlog.setGmreq(gmreq);
        String gmres = sdf.format(new Timestamp(GM_RES));
        gmlog.setGmres(gmres);
        gmlog.setGm_elapse(GM_RES-GM_REQ);
        gmlog.setSt(st);
        gmlog.setDt(dt);
        gmlog.setWps(wps);
        gmlog.setCrs(crs);
        gmlog.setGmreqdata(requestBody);
        gmlog.setGmresdata(responseData);
        gmlog.setGmstat(httpStatus);

        return gmlog;
    }

    //MoNGO Gmlog
    public String insertGmlog(Gmlog gmlog) {
        String result = "";
        mongoTemplete.insert(gmlog);
        //insert data return id
        //System.out.println("##[gmlog getID] :: "+gmlog.getId());
        result = gmlog.getId();
        return result;
    }
}
