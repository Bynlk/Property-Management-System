package com.property.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

/**
 * 停车位状态枚举
 */
public enum ParkingStatus implements CodeEnum {
    IN_USE("使用中"),
    IDLE("空闲");

    private final String value;

    ParkingStatus(String value) { this.value = value; }

    @Override
    @JsonValue
    public String getValue() { return value; }

    @JsonCreator
    public static ParkingStatus fromValue(String value) {
        for (ParkingStatus s : values()) {
            if (s.value.equals(value)) return s;
        }
        return null;
    }
}
