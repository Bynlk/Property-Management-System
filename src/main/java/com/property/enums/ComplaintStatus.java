package com.property.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

/**
 * 投诉状态枚举
 */
public enum ComplaintStatus implements CodeEnum {
    PENDING("待处理"),
    PROCESSING("处理中"),
    RESOLVED("已处理");

    private final String value;

    ComplaintStatus(String value) { this.value = value; }

    @Override
    @JsonValue
    public String getValue() { return value; }

    @JsonCreator
    public static ComplaintStatus fromValue(String value) {
        for (ComplaintStatus s : values()) {
            if (s.value.equals(value)) return s;
        }
        return null;
    }
}
