package com.property.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

/**
 * 用户角色枚举
 */
public enum UserRole implements CodeEnum {
    ADMIN("admin"),
    USER("user");

    private final String value;

    UserRole(String value) { this.value = value; }

    @Override
    @JsonValue
    public String getValue() { return value; }

    @JsonCreator
    public static UserRole fromValue(String value) {
        for (UserRole r : values()) {
            if (r.value.equals(value)) return r;
        }
        return null;
    }
}
