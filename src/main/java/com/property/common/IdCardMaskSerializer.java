package com.property.common;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

import java.io.IOException;

/**
 * 身份证号脱敏序列化器
 * 将 110101199001011234 显示为 1101**********1234
 */
public class IdCardMaskSerializer extends JsonSerializer<String> {

    @Override
    public void serialize(String value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
        if (value == null || value.length() < 8) {
            gen.writeString(value);
            return;
        }
        String masked = value.substring(0, 4) + "**********" + value.substring(value.length() - 4);
        gen.writeString(masked);
    }
}
