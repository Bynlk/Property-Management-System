package com.property.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

/**
 * 费用类型枚举
 */
public enum FeeType implements CodeEnum {
    PROPERTY_FEE("物业费"),
    WATER_FEE("水费"),
    ELECTRICITY_FEE("电费"),
    GAS_FEE("燃气费");

    private final String value;

    FeeType(String value) { this.value = value; }

    @Override
    @JsonValue
    public String getValue() { return value; }

    @JsonCreator
    public static FeeType fromValue(String value) {
        for (FeeType t : values()) {
            if (t.value.equals(value)) return t;
        }
        return null;
    }
}
