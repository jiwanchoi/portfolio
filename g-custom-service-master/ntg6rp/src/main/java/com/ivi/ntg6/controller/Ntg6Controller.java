package com.ivi.ntg6.controller;

import java.io.UnsupportedEncodingException;
import java.util.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.ivi.ntg6.controller.model.Ntg6rpParam;
import com.ivi.ntg6.service.*;
import com.ivi.ntg6.service.vo.ResultRequestSplit;
import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.lang.Nullable;
import org.springframework.util.StopWatch;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ivi.ntg6.model.Gclog;
import com.ivi.ntg6.model.Gmlog;
import com.ivi.ntg6.model.LogData;

/**
 * *** [ NTG6 ]
 * *** [ G - Custom ]
 * *** [  Daimer <-> Geo Custom <->  Geo Master ]
 */

/**
 * @author user
 */
@RestController
public class Ntg6Controller {
    private static final Logger logger = LogManager.getLogger(Ntg6Controller.class);

    @Autowired
    private Ntg6Service service;

    @Autowired
    private Auth_Service authService;

    @Autowired
    private RequestRetryConfigService requestRetryConfigService;

    @Autowired
    private LogService logService;

    @Autowired
    private SplitThread SplitThread;

    /**
     * rp 요청을 처리
     *
     * @param req      st, dt, wps, crs 의 파라메터
     * @param reqBodys post 의 경우 경로탐색 조건을 body에 담아서 요청
     * @return
     */
    @RequestMapping("/rp")
    public ResponseEntity processRouting(HttpServletRequest req, @RequestBody @Nullable Map<String, Object> reqBodys) {
        StopWatch stopWatch = new StopWatch(this.getClass().getName());
        stopWatch.start("/rp Request received");
        logger.info("### rp start ## :::  " + stopWatch.currentTaskName());
        logger.info("### authApi Request from " + req.getRemoteAddr());
        logger.info("### authApi  ## :::  " + req.getHeader("gcauth"));

        ////////////////////request 파라미터  확인////////////////////////////////////
        if (logger.isDebugEnabled()) {
            Enumeration enu = req.getHeaderNames();

            String name = null;
            String values = null;

            logger.info("### while start  ## :::  ");
            while (enu.hasMoreElements()) {
                logger.info("### while start gogogo ## :::  ");
                name = (String) enu.nextElement();
                values = req.getHeader(name);
                logger.info("### name  ## :::  " + name + "/[" + values + "]");
            }

            Enumeration enu1 = req.getParameterNames();

            String name1 = null;
            String[] values1 = null;
            int cnt1 = 0;

            logger.info("### while start22  ## :::  ");
            while (enu1.hasMoreElements()) {
                logger.info("### while start22 gogogo ## :::  ");
                name1 = (String) enu1.nextElement();
                values1 = req.getParameterValues(name1);
                cnt1 = values1.length;

                for (int i = 0; i < cnt1; i++) {
                    logger.info("### name  ## :::  " + name1 + "/[" + values1[i] + "]");
                }
            }
        }
        ///////////////////request 파라미터  확인////////////////////////////////////

        //return headers
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));

        //boolean chk = auth_service.chk_auth_api(req.getHeader("gcauth"));
        boolean chk = true;
        logger.info("### chk ## :::  " + chk);
        ResponseEntity resEntity = null;
        String result = "";
        HttpStatus httpStatus = HttpStatus.OK;
        Long GC_REQ = System.currentTimeMillis();
        Map<String, Object> resultMap;
        int retryCount = 0;
        stopWatch.stop();
        if (chk) {
            logger.info("### req.getMethod() ## :::  " + req.getMethod());
            stopWatch.start("> Send request to GM");

            if (req.getMethod().equals("GET")) {
                resultMap = getResponseEntity(req, headers, reqBodys, retryCount, GC_REQ);
                httpStatus = (HttpStatus) resultMap.get("httpStatus");
                result = (String) resultMap.get("result");
            } else {
                int maxRetryCount = requestRetryConfigService.getRequestRetryCount();
                while (true) {
                    if (retryCount >= maxRetryCount) {
                        break;
                    }
                    resultMap = getResponseEntity(req, headers, reqBodys, retryCount, GC_REQ);
                    int result_cnt = (int) resultMap.get("retry");
                    httpStatus = (HttpStatus) resultMap.get("httpStatus");
                    result = (String) resultMap.get("result");
                    logger.info("### result_cnt ## :::  " + result_cnt);
                    if (retryCount == result_cnt) {
                        logger.info("### same ## :::  ");
                        break;
                    } else {
                        retryCount = result_cnt;
                        reqBodys = requestRetryConfigService.removeReqBody(reqBodys, retryCount);    //재시도 함수.
                    }
                }
            }

            stopWatch.stop();
        } else {
            httpStatus = HttpStatus.UNAUTHORIZED;
            result = "{\"code\": 400001,\r\n \"message\": \"Unauthorized\"\r\n}";
            //LogData logData = log_service.setLogData(req, new HashMap<>(), new HashMap<>() , HttpStatus.UNAUTHORIZED.toString(),"401",GM_REQ, retry);
            //log_service.insertLogData(logData);
        }

//        logger.info(stopWatch.prettyPrint());
        resEntity = new ResponseEntity<String>(result, headers, httpStatus);
        return resEntity;
    }

    /**
     * GM 에 RP 요청을 보내고 결과를 처리.
     *
     * @param req
     * @param headers
     * @param reqBodys
     * @param retry
     * @param GC_REQ
     * @return
     */
    public Map<String, Object> getResponseEntity(HttpServletRequest req, HttpHeaders headers,
                                                 @RequestBody @Nullable Map<String, Object> reqBodys, int retry, Long GC_REQ) {
        StopWatch stopWatch = new StopWatch("getResponseEntity");
        stopWatch.start("before request to GM");
        String result = " ";
        HttpStatus httpStatus = HttpStatus.OK;
        String errorCode = "";
        String errorMsg = "";

        Ntg6rpParam rpParam = null;
        try {
            rpParam = new Ntg6rpParam(
                    req.getMethod(),
                    req.getRemoteHost(),
                    req.getHeader("gcauth"),
                    req.getParameter("st"),
                    req.getParameter("dt"),
                    req.getParameter("crs"),
                    req.getParameter("wps"),
                    req.getParameter("single"),
                    reqBodys
            );
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        //GM RP url 생성
//        String url = service.makeUrl(rpParam.getSt(), rpParam.getDt(), rpParam.getCrs(), rpParam.getWps());
        // multi request 를 이용한 split 요청을 처리할지 아닐지를 판단. true 인 경우 단일 리퀘스트로 처리한다.
        boolean isSingleRequest = rpParam.isSingle();

        //경유지 분할 array
        ArrayList<String[]> arrWps = service.chk_wps(rpParam.getSt(), rpParam.getDt(), rpParam.getWps());

        ObjectMapper mapper = new ObjectMapper();
        Map<String, Object> resMap = null;

        Long GM_REQ = System.currentTimeMillis();
        Long GM_RES = System.currentTimeMillis();
        stopWatch.stop();
        // 여기에서 생성할 필요성이 있을까?
        ResultRequestSplit resultRequestSplit = null;
        //GM RP 요청.
        try {
            stopWatch.start("GM response elapsed time. ***");
            ///if로 경유지 있으면 멀티스레드, 아니면 그냥 보내기.
            logger.debug("======> arrWps.size() :: " + arrWps.size());
            if (arrWps.size() > 0 && !isSingleRequest) {
                resultRequestSplit = SplitThread.requestSplit(rpParam.getSt(), rpParam.getDt(), rpParam.getCrs(), rpParam.getWps(), rpParam.getGcAuth(), rpParam.getMethod(), rpParam.getBody());
                result = resultRequestSplit.getRpResult();
            } else {
                resultRequestSplit = doUrlSend(UUID.randomUUID().toString(), req.getMethod(), rpParam.getSt(), rpParam.getDt(), rpParam.getCrs(), rpParam.getWps(), rpParam.getGcAuth(), rpParam.getBody());
                result = resultRequestSplit.getRpResult();
            }
            resMap = mapper.readValue(result, Map.class);
        } catch (HttpClientErrorException hce) {
            //40* error
            logger.error(" [ ERROR MESSAGE ] : " + hce.getLocalizedMessage());
            logger.error(" [ ERROR RESULT ] : " + hce.getResponseBodyAsString());
            result = hce.getResponseBodyAsString();
            httpStatus = HttpStatus.BAD_REQUEST;
            // convert JSON string to Map
            try {
                JsonNode errmap = mapper.readValue(result, JsonNode.class);
                if (!errmap.get("error_result").isNull()) {
                    // error_result 안에 code와 message 값이 있음.
                    errorCode = errmap.at("/error_result/code").textValue();
                    errorMsg = errmap.at("/error_result/message").textValue();
                    logger.error(errorCode);
                    logger.error(errorMsg);
                    if (errorCode.equals("C0002") || errorCode.equals("C0004")) { // 404 : C0002 : no route found ,
                        // C0004 : route faile
                        // avoid_area(회피구간) 제외하고 retry?
                        retry += 1;
                    }
                } else if (!errmap.get("message").isNull()) {
                    // message만 있음.
                    logger.error(errmap.get("message").textValue());
                }
            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        } catch (HttpServerErrorException hse) {
            // 50* error
            logger.error(" [ ERROR MESSAGE ] : " + hse.getLocalizedMessage());
            logger.error(" [ ERROR RESULT ] : " + hse.getResponseBodyAsString());//{"message":"No API key found in request"}
            result = hse.getResponseBodyAsString();
            httpStatus = hse.getStatusCode();
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            logger.error(" [Unknwon Error] : " + e.getLocalizedMessage());
            logger.error(" [Stactrace ] : " + e.getStackTrace());
            httpStatus = HttpStatus.BAD_REQUEST;
        } finally {
            stopWatch.stop(); // RP GM Request 처리 완료.
            // RPLOG INSERT DATA 담기 전에 MONGO DB INSERT 먼저하고 _ID값 MID에 넣기.
            // LOG SET
            GM_RES = System.currentTimeMillis();
            Long GC_RES = System.currentTimeMillis();
            String mid = "";
            ////////////mongoDB에 로그 인서트
            try {
                stopWatch.start("Request & Response logging at mongoDB");
                GM_REQ = resultRequestSplit.getStartTimeMillies();
                GM_RES = resultRequestSplit.getEndTimeMillies();
                Gclog gclog = logService.setGcLog(
                        resultRequestSplit.gCustomTransactionId,
                        rpParam, resMap, httpStatus.toString(), errorCode,
                        GC_REQ, GC_RES, GM_REQ, GM_RES, retry
                );
                mid = logService.insertGclog(gclog);
                for (int i = 0; i < resultRequestSplit.routeCount; i++) {
                    Gmlog gmlog = logService.setGmLog(
                            resultRequestSplit.gCustomTransactionId,
                            resultRequestSplit.responses.get(i).geoMasterTransactionId,
                            resultRequestSplit.responses.get(i).st,
                            resultRequestSplit.responses.get(i).dt,
                            resultRequestSplit.responses.get(i).wps,
                            resultRequestSplit.responses.get(i).crs,
                            resultRequestSplit.responses.get(i).reqParam,
                            resultRequestSplit.responses.get(i).responseJsonMsg,
                            resultRequestSplit.responses.get(i).responseStatus, errorCode,
                            GM_REQ, // GC_REQ == GM_REQ 총 GM 요청 처리하기위해 걸린 시간에 대한 기록.
                            GM_RES, // GC_REQ == GM_REQ 총 GM 요청 처리하기위해 걸린 시간에 대한 기록.
                            resultRequestSplit.responseElapsedTimes.get(i).getStartTimeMillis(),
                            resultRequestSplit.responseElapsedTimes.get(i).getEndTimeMillis(),
                            mid
                    );
                    String rult = logService.insertGmlog(gmlog);
                }

            } catch (Exception e) {
                logger.error("### Gclog insert errer ## :::  " + e);
                logger.error("Exception : " + e.toString());
            } finally {
                stopWatch.stop();
            }

            ///////// postgreSQL에 로그 인서트
            try {
                stopWatch.start("Request & Response logging at postgreSQL");
                LogData logData = logService.setLogData(
                        resultRequestSplit.gCustomTransactionId,
                        rpParam, resMap, new HashMap<>(), httpStatus.toString(), errorCode,
                        GC_REQ, GC_RES, GM_REQ, GM_RES, retry, mid,
                        resultRequestSplit.getElapsedTime());
                logger.info("### [ LOGDATA ] ###################\n" + logData.toString());
                logService.insertLogData(logData);
            } catch (Exception e) {
                logger.error("### rplog insert errer ## :::  " + e);
                logger.trace(e);
            } finally {
                stopWatch.stop();
            }
        }
        logger.info(" [ return BODY ] : " + result.length());
        logger.info("### [ RETURN END ] #############################################");
        logger.info(stopWatch.prettyPrint());

        Map<String, Object> chk_map = new HashMap<String, Object>();
        chk_map.put("result", result);
        chk_map.put("httpStatus", httpStatus);
        chk_map.put("retry", retry);
        return chk_map;
    }

    /**
     * GET, POST 방식의 Thread 를 사용하지 않는 요청 처리.
     *
     *
     * @param gCustomTransactionId
     * @param requestMethod
     * @param st
     * @param dt
     * @param crs
     * @param wps
     * @param gcauth
     * @param reqBodys
     * @return
     */
    private ResultRequestSplit doUrlSend(String gCustomTransactionId, String requestMethod, String st, String dt, String crs, String wps, String gcauth, Map<String, Object> reqBodys) throws Exception {
        String rpResult;
        String url = service.makeUrl(st, dt, crs, wps);
        final long startTimeMillis = System.currentTimeMillis();
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        if (StringUtils.equalsIgnoreCase(requestMethod, "GET")) {
            rpResult = service.urlGetSend(url, gcauth);
        } else {
            rpResult = service.urlPostSend(url, gcauth, reqBodys);
        }
        stopWatch.stop();

//        return new ResultRequestSplit(new ResultElapsedTime(stopWatch.getLastTaskTimeMillis()), rpResult);
        // wps 가 0 개이면 시작에서 종점까지의 route 가 1개 나옴으로
        ResultRequestSplit resultRequestSplit = new ResultRequestSplit(Thread.currentThread().getId(), gCustomTransactionId, 1);
        resultRequestSplit.set(0, st, dt, wps, crs, ((reqBodys != null) ? reqBodys.toString() : null), Integer.toString(HttpServletResponse.SC_OK),rpResult, startTimeMillis, stopWatch.getTotalTimeMillis());
        return resultRequestSplit;
    }
}
