package com.property.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

/**
 * 报修状态枚举
 */
public enum RepairStatus implements CodeEnum {
    PENDING("待维修"),
    IN_PROGRESS("维修中"),
    COMPLETED("已完成");

    private final String value;

    RepairStatus(String value) { this.value = value; }

    @Override
    @JsonValue
    public String getValue() { return value; }

    @JsonCreator
    public static RepairStatus fromValue(String value) {
        for (RepairStatus s : values()) {
            if (s.value.equals(value)) return s;
        }
        return null;
    }
}
