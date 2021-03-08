package com.ivi.openlr.util;

public enum LinkColumn {
    ID("id"),
    X("vertices/x"),
    Y("vertices/y"),
    ROAD_CLASS("road_class"),
    MOVING_TYPE("moving_type");

    private final String field;

    LinkColumn(String field) {
        this.field = field;
    }

    public String getField() {
        return field;
    }
}
