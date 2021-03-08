package com.ivi.ntg6.service.vo;

public class ResponseElapsedTime {
    // wps number
    private final long wpsNumber;
    // start time millis
    private final long startTime;
    // elapsedTime millis
    private final long elapsedTime;
    private final long endTimeMillis;

    public ResponseElapsedTime(long wpsNumber, long startTime, long elapsedTime) {
        this.wpsNumber = wpsNumber;
        this.startTime = startTime;
        this.elapsedTime = elapsedTime;
        this.endTimeMillis = System.currentTimeMillis();
    }

    public long getWpsNumber() {
        return wpsNumber;
    }

    public long getStartTimeMillis() {
        return startTime;
    }

    public long getElapsedTimeMillis() {
        return elapsedTime;
    }

    public long getEndTimeMillis() {
        return endTimeMillis;
    }
}
