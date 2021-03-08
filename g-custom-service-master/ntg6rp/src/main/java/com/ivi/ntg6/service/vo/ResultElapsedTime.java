package com.ivi.ntg6.service.vo;


/**
 * 분리 요청의 경우 처리한 시간은 최대/최소/평균 값을 결과로 보낸다.
 * 분리 요청이 아닌경우 최대/최소/평균 값이 모두 동일하다.
 */
public class ResultElapsedTime {
    public final int count;
    public final long min;
    public final long average;
    public final long max;

    public ResultElapsedTime(int count, long min, long average, long max) {
        this.count = count;
        this.min = min;
        this.average = average;
        this.max = max;
    }

    public ResultElapsedTime(long taskTimeMillis) {
        this.count = 0;
        this.min = taskTimeMillis;
        this.average = taskTimeMillis;
        this.max = taskTimeMillis;
    }

    public long getMin() {
        return min;
    }

    public long getAverage() {
        return average;
    }

    public long getMax() {
        return max;
    }
}
