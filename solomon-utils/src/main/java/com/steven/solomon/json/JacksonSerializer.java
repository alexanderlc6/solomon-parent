package com.steven.solomon.json;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.BeanProperty;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.ContextualSerializer;
import com.steven.solomon.annotation.EnumSerialize;
import com.steven.solomon.enums.EnumUtils;
import com.steven.solomon.utils.logger.LoggerUtils;
import com.steven.solomon.verification.ValidateUtils;

import java.io.IOException;
import java.util.Objects;

import org.slf4j.Logger;

public class JacksonSerializer extends JsonSerializer<String> implements ContextualSerializer {

    private final Logger logger = LoggerUtils.logger(getClass());

    private Class<? extends Enum<?>> enumClass;

    private String prefix;

    private String methodName;

    private String fieldName;

    public JacksonSerializer() {
        super();
    }

    public JacksonSerializer(Class<? extends Enum<?>> enumClass, String prefix, String methodName, String fieldName) {
        this.enumClass = enumClass;
        this.prefix = prefix;
        this.methodName = methodName;
        this.fieldName = fieldName;
    }

    @Override
    public void serialize(String o, JsonGenerator jsonGenerator, SerializerProvider serializerProvider)
            throws IOException {

        jsonGenerator.writeString(o);
        try {
            Enum<?> enums = EnumUtils.codeOf(enumClass, o);
            if (ValidateUtils.isEmpty(enums)) {
                logger.error("EnumSerializer 转换枚举为空,值:{},类名:{}", o, enumClass.getName());
                return;
            }
            String value = (String) enumClass.getMethod(methodName).invoke(enums);
            if (ValidateUtils.isEmpty(fieldName)) {
                jsonGenerator.writeStringField(prefix + methodName, value);
            } else {
                jsonGenerator.writeStringField(fieldName, value);
            }
        } catch (Throwable e) {
            logger.error("EnumSerializer 转换失败,值:{},枚举类为:{},调用方法名为:{}报错异常为 e:", o, enumClass.getName(), methodName, e);
            return;
        }
    }

    @Override
    public JsonSerializer<?> createContextual(SerializerProvider serializerProvider, BeanProperty beanProperty)
            throws JsonMappingException {
        if (ValidateUtils.isEmpty(beanProperty)) {
            return serializerProvider.findNullValueSerializer(beanProperty);
        }

        if (!Objects.equals(beanProperty.getType().getRawClass(), String.class)) {
            return serializerProvider.findValueSerializer(beanProperty.getType(), beanProperty);
        }

        EnumSerialize enumLabel = beanProperty.getAnnotation(EnumSerialize.class);
        if (ValidateUtils.isEmpty(enumLabel) || !enumLabel.ignore()) {
            return serializerProvider.findValueSerializer(beanProperty.getType(), beanProperty);
        }
        return new JacksonSerializer(enumLabel.enumClass(), beanProperty.getName(), enumLabel.methodName(), enumLabel.fieldName());
    }
}