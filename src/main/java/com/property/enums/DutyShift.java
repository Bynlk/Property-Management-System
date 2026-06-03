package com.property.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

/**
 * 值班班次枚举
 */
public enum DutyShift implements CodeEnum {
    MORNING("早班"),
    AFTERNOON("中班"),
    NIGHT("晚班");

    private final String value;

    DutyShift(String value) { this.value = value; }

    @Override
    @JsonValue
    public String getValue() { return value; }

    @JsonCreator
    public static DutyShift fromValue(String value) {
        for (DutyShift s : values()) {
            if (s.value.equals(value)) return s;
        }
        return null;
    }
}
