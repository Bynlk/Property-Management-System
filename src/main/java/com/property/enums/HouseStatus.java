package com.property.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

/**
 * 房屋状态枚举
 */
public enum HouseStatus implements CodeEnum {
    OCCUPIED("已入住"),
    VACANT("空置"),
    RENOVATING("装修中");

    private final String value;

    HouseStatus(String value) { this.value = value; }

    @Override
    @JsonValue
    public String getValue() { return value; }

    @JsonCreator
    public static HouseStatus fromValue(String value) {
        for (HouseStatus s : values()) {
            if (s.value.equals(value)) return s;
        }
        return null;
    }
}
