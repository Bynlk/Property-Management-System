package com.property.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

/**
 * 费用状态枚举
 */
public enum FeeStatus implements CodeEnum {
    UNPAID("未缴"),
    PAID("已缴");

    private final String value;

    FeeStatus(String value) { this.value = value; }

    @Override
    @JsonValue
    public String getValue() { return value; }

    @JsonCreator
    public static FeeStatus fromValue(String value) {
        for (FeeStatus s : values()) {
            if (s.value.equals(value)) return s;
        }
        return null;
    }
}
