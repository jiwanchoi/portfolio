package com.ivi.openlr.util;

public enum TBTColumn {
    TYPE("type"),
    NAME("name"),
    X("coord/x"),
    Y("coord/y"),
    SHORT_DIRECTION("short_direction"),
    MID_DIRECTION("mid_direction"),
    LONG_DIRECTION("long_direction"),
    SCENIC_ROAD_INFO("scenic_road_info"),
    NEXT_DISTANCE("next_distance");

    private final String field;

    TBTColumn(String field) {
        this.field = field;
    }

    public String getField() {
        return field;
    }


    @Override
    public String toString() {
        return field;
    }
}
