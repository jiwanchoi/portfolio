package com.ivi.ntg6.service;

import java.util.*;

import com.ivi.ntg6.service.vo.ResultRequestSplit;
import com.ivi.ntg6.service.vo.ResultElapsedTime;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.omg.CORBA.CurrentHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StopWatch;

import java.util.concurrent.*;

import org.springframework.web.client.HttpClientErrorException;

import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class SplitThread {

    private static final Logger logger = LogManager.getLogger(SplitThread.class);

    @Autowired
    private Ntg6Service service;
//    private ArrayList<HashMap> arrResult;
    private ArrayList<RPTime> arrTimestamp;
    private ConcurrentHashMap<Long, ResultRequestSplit> threadResults = new ConcurrentHashMap<>();

//    private synchronized void setThreadResults(long parentThreadId, int wpsNumber, String gmResult, long startTime, long elapsedTime) {
//        logger.debug("[PRE] =====================> " + parentThreadId + " :::: " + wpsNumber);
//        synchronized (threadResults) {
//            threadResults.get(parentThreadId).set(wpsNumber, gmResult, startTime, elapsedTime);
//        }
//        logger.debug("[POST] =====================> " + parentThreadId + " :::: " + wpsNumber);
//    }


    private synchronized void addThreadResults(long threadId, ResultRequestSplit resultRequestSplit) {
        threadResults.put(threadId, resultRequestSplit);
        logger.debug("[created ThreadResult @ " + threadId + " / "+ threadResults.size() +" total ]");
    }

    public synchronized ResultRequestSplit getThreadResult(long parentThreadId) {
        logger.debug("[return ThreadResult @ " + parentThreadId + " / "+ threadResults.size() +" total ]");
        return threadResults.get(parentThreadId);
    }

    public synchronized void removeThreadResult(long threadId) {
        threadResults.remove(threadId);
        logger.debug("[removed ThreadResult @ " + threadId + " / "+ threadResults.size() +" left ]");
    }

    class ntg6rpThread implements Callable<String> {

        private final long parentThreadId;
        private final int ntgi;
//        private String ntgrt;
        private String[] ntgtt;
        private String crs;
        private String auth;
        private String type;
        private Map<String, Object> bodys;
        private final CountDownLatch latch;

        @Override
        public String call() throws Exception {
            ResultRequestSplit rrs = getThreadResult(parentThreadId);
            final long startTimeMillis = System.currentTimeMillis();
            StopWatch stopWatch = new StopWatch(this.getClass().getName() + " id:" + ntgi);

            String r1 = "";
            try {
                stopWatch.start();
                String url = service.makeUrl(ntgtt[0], ntgtt[1], crs, null);

                if (this.type.equals("POST")) {
                    r1 = service.urlPostSend(url, this.auth, bodys);
                } else {
                    r1 = service.urlGetSend(url, this.auth);
                }
                rrs.setResponse(ntgi, ntgtt[0], ntgtt[1], null, crs, ((bodys != null) ? bodys.toString() : null), "200 OK",  r1);
            } catch (HttpClientErrorException e) {
                e.printStackTrace();
                logger.error(" [ ERROR MESSAGE ] : " + e.getLocalizedMessage());
                logger.error(" [ ERROR RESULT ] : " + e.getResponseBodyAsString());
//                map.put("rt", e.getResponseBodyAsString());
//                map.put("error", e.getLocalizedMessage());
//                arrResult.add(map);
                throw new RuntimeException();
            }
            finally {
                if (stopWatch.isRunning()) {
                    stopWatch.stop();
                }
                // timestamp 계산을 위한 현재 요청 쓰레드의 소요 시간을 기록
                logger.info("MultiThread Request ("+ parentThreadId +" #"+ ntgi +") is done. Elapsed Time : " + stopWatch.getTotalTimeMillis() + " ms");
                arrTimestamp.add(new RPTime(parentThreadId, ntgi, startTimeMillis, stopWatch.getTotalTimeMillis()));
                rrs.setTimeStamp(ntgi, startTimeMillis, stopWatch.getTotalTimeMillis());
                latch.countDown();
            }
            return r1; //값을 반환
        }

        public ntg6rpThread(final long parentThreadId, int i, String crs, String auth, String[] ntgtt, String type, Map<String, Object> bodys, final CountDownLatch latch) {
            this.parentThreadId = parentThreadId;
            this.ntgi = i;
            this.crs = crs;
            this.auth = auth;
            this.ntgtt = ntgtt;
            this.type = type;
            this.bodys = bodys;
            this.latch = latch;
        }
    }

    //get,post 일때 요청.

    /**
     * 하나의 rp 요청을 다중 쓰레드를 이용하여 요청을 진행한다.
     * @param st 시점 좌표
     * @param dt 종점 좌표
     * @param crs 좌표계
     * @param wps 경유지
     * @param Authorization GeoMaster 인증 코드
     * @param type
     * @param bodys POST 의 경우, body 내용
     * @return
     */
    public ResultRequestSplit requestSplit(String st, String dt, String crs, String wps, String Authorization, String type, Map<String, Object> bodys) throws Exception {
        StopWatch stopWatch = new StopWatch(this.getClass().getName());
        stopWatch.start("Before multi request send.");
        String result = " ";
//        ResultElapsedTime timeStampResult = new ResultElapsedTime(0,0L, 0L, 0L);
        arrTimestamp = new ArrayList<RPTime>();
        // 나누어 요청할 way point 들을 분리함.
        final ArrayList<String[]> arrWps = service.chk_wps(st, dt, wps);

        logger.info("Main thread starts here...  " + arrWps.size());
        final long currentThreadId = Thread.currentThread().getId();
        // create current thread's request result holder.
        addThreadResults(currentThreadId, new ResultRequestSplit(currentThreadId, UUID.randomUUID().toString(), arrWps.size()));
//        this.threadResults.put(currentThreadId, new ResultRequestSplit(currentThreadId, UUID.randomUUID().toString(), arrWps.size()));

        stopWatch.stop();
        if (arrWps.size() > 0) {
            // new thread pool
            ExecutorService execService = Executors.newCachedThreadPool();
            // 쓰레드를 이용한 다중 경유기 분리 요청 처리
            stopWatch.start("processing multi request");
            try {
                CountDownLatch latch = new CountDownLatch(arrWps.size());
                for (int i = 0; i < arrWps.size(); i++) {
                    execService.submit(new ntg6rpThread(currentThreadId, i, crs, Authorization, arrWps.get(i), type, bodys, latch));
                }

                latch.await();
                logger.debug("latch count ------------> " + latch.getCount());

            } catch (Exception ee) {
                logger.info("### Split_rp ## :::  " + ee);
                ee.printStackTrace();
            }
            finally {
                stopWatch.stop();
                if (execService != null) execService.shutdown();
            }
        } else {
            // 단일 요청을 통한 분기 요청 처리
            //GM RP url 생성
            long startTimeMillis = System.currentTimeMillis();
            stopWatch.start("processing single request");
            String url = service.makeUrl(st, dt, crs, wps);
            if (type.equals("POST")) {
                result = service.urlPostSend(url, Authorization, bodys);
            } else {
                result = service.urlGetSend(url, Authorization);
            }
            stopWatch.stop();
            long resultElapsedTime = stopWatch.getLastTaskTimeMillis();
            this.getThreadResult(currentThreadId).setResponse(0, st, dt, wps, crs, bodys.toString(), "", result);
            this.getThreadResult(currentThreadId).setTimeStamp(0, startTimeMillis, stopWatch.getTotalTimeMillis());
        }

//        logger.info(stopWatch.prettyPrint());
        logger.info("MultiThread Request ("+ currentThreadId +"-Total) is done. Total Elapsed Time : " + stopWatch.getTotalTimeMillis() + " ms");
        ResultRequestSplit resultRRS = threadResults.get(currentThreadId);
//        threadResults.remove(currentThreadId);
        removeThreadResult(currentThreadId);

        return resultRRS;
    }


    /**
     * 분리요청한 결과값을 하나로 합치기 전에, thread 에서 반환한 오류가 있는지 확인한다.
     * thread 에서 처리한 요청들은 arrResult 에 Key Value 로 들어가 있다.
     * 이를 결과값을 합쳐서 하나의 RP 응답으로 조립해서 리턴한다.
     * @param arrResult
     * @return String
     */
    private String rtResultMap(ArrayList<HashMap> arrResult) {
        ArrayList<String> final_rt = new ArrayList<String>();
        //RP JSON sort
        Boolean chk_error = false;
        String result = "";

        //error 체크 -- 해당 코드는 동작 않함. thread 에서 error 발생시 throw Exception 을 수행하기 때문.
        for (HashMap map : arrResult) {
            if (map.get("error") != null) {
                chk_error = true;
                result = map.get("rt").toString();
                break;
            }
        }

        //정상동작시
        if (!chk_error) {
            for (int i = 0; i < arrResult.size(); i++) {
                for (HashMap map : arrResult) {
                    if (map.get("index").equals(i)) {
                        final_rt.add(map.get("rt").toString());
                    }
                }
            }
            result = merageRp(final_rt);
        }

        return result;
    }

    /**
     * Thread 의 timestamp result 값에서 최대/최소/평균 값을 작성하여 해당 객체로 반환.
     * @return
     */
    private ResultElapsedTime rtTimeStamp() {
        long elapsedTimeTotal = 0L;
        long min = Long.MAX_VALUE;
        long max = Long.MIN_VALUE;

        int cnt = 0;
        for (RPTime curItem : arrTimestamp) {
            elapsedTimeTotal += curItem.elapsedTime;
            // search max
            if (max < curItem.elapsedTime) {
                max = curItem.elapsedTime;
            }
            // search min
            if (min > curItem.elapsedTime) {
                min = curItem.elapsedTime;
            }
            cnt++;
        }
        long average = elapsedTimeTotal / arrTimestamp.size();

        return new ResultElapsedTime(cnt, min, average, max);
    }

//    @Deprecated
//    public String rtResult(int cnt) {
//        ArrayList<String> final_rt = new ArrayList<String>();
//        //RP JSON sort
//        Boolean chk_error = false;
//        String result = "";
//
//        //error 체크
//        for (HashMap map : arrResult) {
//            if (map.get("error") != null) {
//                chk_error = true;
//                result = map.get("rt").toString();
//                break;
//            }
//        }
//
//        //정상동작시
//        if (!chk_error) {
//            for (int i = 0; i < cnt; i++) {
//                for (HashMap map : arrResult) {
//                    if (map.get("index").equals(i)) {
//                        final_rt.add(map.get("rt").toString());
//                    }
//                }
//            }
//            result = merageRp(final_rt);
//        }
//
//        return result;
//    }


    //머지작업
    public String merageRp(ArrayList<String> final_rt) {
        String result = "";
        try {

            Map<String, Object> resMap = new HashMap<>();
            ObjectMapper mapper = new ObjectMapper();
            resMap = mapper.readValue(final_rt.get(0), Map.class);

            for (int i = 1; i < final_rt.size(); i++) {
                Map<String, Object> forMap = new HashMap<>();
                forMap = mapper.readValue(final_rt.get(i), Map.class);


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

            result = mapper.writeValueAsString(resMap);
            //logger.info("### result ## :::  " + result );

        } catch (Exception ee) {
            logger.info("### merageRp ## :::  " + ee);
        }
        return result;
    }

    /**
     * 분리 요청하는 thread 의 처리 소요시간을 기록한다.
     */
    private class RPTime {
        final long parentThreadId;
        final int wpsNumber;
        final long startTime;
        final long elapsedTime;

        RPTime(long parentThreadId, int wpsNumber, long startTime, long elapsedTime) {
            this.parentThreadId = parentThreadId;
            this.wpsNumber = wpsNumber;
            this.startTime = startTime;
            this.elapsedTime = elapsedTime;
        }
    }
}