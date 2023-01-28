package com.youneng.troy.template.web.config;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.annotation.JacksonStdImpl;
import com.fasterxml.jackson.databind.ser.std.NumberSerializers;

import java.io.IOException;

/**
 * 长度操作16为的字符串序列化成字符串（兼容前端浏览器，长度超过16的会变成0）
 */
@JacksonStdImpl
public class LongSerializer extends NumberSerializers.Base<Long> {

    public LongSerializer(Class<?> cls) {
        super(cls, JsonParser.NumberType.LONG, "number");
    }

    @Override
    public void serialize(Long value, JsonGenerator gen, SerializerProvider provider) throws IOException {

        if (value.toString().length() > 16){
            gen.writeString(value.toString());
            return;
        }

        gen.writeNumber(value);
    }
}
