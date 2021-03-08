package com.ivi.ntg6.service.vo;

import com.ivi.ntg6.comm.GeoMasterResultHandler;

import java.util.ArrayList;
import java.util.List;

/**
 * requestSplist 의 Result를 담는 class
 */
public class ResultRequestSplit {
    public final long parentThreadId;
    public final String gCustomTransactionId;
    public final int routeCount;
    public final List<GeoMasterResponse> responses;
    public final List<ResponseElapsedTime> responseElapsedTimes;
    private ResultElapsedTime elapsedTime;
    private String mergedResults;

    public ResultRequestSplit(long parentThreadId, String gCustomTransactionId, int routeCount) {
        this.parentThreadId = parentThreadId;
        this.gCustomTransactionId = gCustomTransactionId;
        this.routeCount = routeCount;
        this.responses = new ArrayList<GeoMasterResponse>(routeCount);
        for (int i = 0; i < routeCount; i++) {
            responses.add(null);
        }
        this.responseElapsedTimes = new ArrayList<ResponseElapsedTime>(routeCount);
        for (int i = 0; i < routeCount; i++) {
            responseElapsedTimes.add(null);
        }
    }

    public synchronized String getRpResult() {
        if (this.mergedResults != null) {
            return this.mergedResults;
        }
        mergedResults = GeoMasterResultHandler.mergeRoutes(responses);
        return mergedResults;
    }

    public void set(int routeIndex, String st, String dt, String wps, String crs, String reqParam, String resStatus,  String gmResult, long startTime, long elapsedTime) {
        this.setResponse(routeIndex, st, dt, wps, crs, reqParam, resStatus, gmResult);
        this.setTimeStamp(routeIndex, startTime, elapsedTime);
    }

    public void setResponse(int routeIndex, String st, String dt, String wps, String crs, String reqParam, String resStatus, String gmResult) {
        synchronized (responses) {
            responses.set(routeIndex, new GeoMasterResponse(st, dt, wps, crs, reqParam, resStatus, gmResult, gCustomTransactionId));
        }
    }

    public void setTimeStamp(int routeIndex, long startTime, long elapsedTime) {
        synchronized (responseElapsedTimes) {
            responseElapsedTimes.set(routeIndex, new ResponseElapsedTime(routeIndex, startTime, elapsedTime));
        }

    }

    public synchronized ResultElapsedTime getElapsedTime() {
        if (responseElapsedTimes.size() != this.routeCount) {
            return null;
        }

        if (this.elapsedTime != null) {
            return elapsedTime;
        }

        long max = 0L;
        long min = Long.MAX_VALUE;
        long avg = 0L;
        long total = 0L;
        long count = 0L;

        for (ResponseElapsedTime elapsedTime : responseElapsedTimes) {
            count++;
            if (max < elapsedTime.getElapsedTimeMillis()) {
                max = elapsedTime.getElapsedTimeMillis();
            }

            if (elapsedTime.getElapsedTimeMillis() < min) {
                min = elapsedTime.getElapsedTimeMillis();
            }

            total += elapsedTime.getElapsedTimeMillis();
        }

        avg = total / count;
        this.elapsedTime = new ResultElapsedTime(Math.toIntExact(count), min, avg, max);

        return this.elapsedTime;
    }

    public Long getStartTimeMillies() {
        long startTimeMillis = System.currentTimeMillis();
        for (ResponseElapsedTime elapsedTime : responseElapsedTimes) {
            if (elapsedTime.getStartTimeMillis() < startTimeMillis) {
                startTimeMillis = elapsedTime.getStartTimeMillis();
            }
        }
        return startTimeMillis;
    }


    public Long getEndTimeMillies() {
        long endTimeMillis = 0L;
        for (ResponseElapsedTime elapsedTime : responseElapsedTimes) {
            if (endTimeMillis < elapsedTime.getEndTimeMillis()) {
                endTimeMillis = elapsedTime.getEndTimeMillis();
            }
        }
        return endTimeMillis;
    }
}
