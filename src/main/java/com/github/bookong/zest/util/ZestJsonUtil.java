package com.github.bookong.zest.util;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.github.bookong.zest.exception.ZestException;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * @author Jiang Xu
 */
public class ZestJsonUtil {

    public static final Logger                     logger              = LoggerFactory.getLogger(ZestJsonUtil.class);

    private static final ThreadLocal<ObjectMapper> OBJECT_MAPPER_CACHE = new ThreadLocal<>();

    public static String toJson(Object obj) {
        try {
            if (obj == null) {
                return null;
            }
            return getObjectMapper().writeValueAsString(obj);
        } catch (Exception e) {
            throw new ZestException(e);
        }
    }

    public static String toPrettyJson(Object obj) {
        try {
            if (obj == null) {
                return null;
            }
            return getObjectMapper().writerWithDefaultPrettyPrinter().writeValueAsString(obj);
        } catch (Exception e) {
            throw new ZestException(e);
        }
    }

    public static <T> List<T> fromJsonArray(String content, Class<T> valueType) {
        try {
            if (StringUtils.isBlank(content)) {
                return Collections.emptyList();
            }
            JavaType javaType = getObjectMapper().getTypeFactory().constructParametricType(List.class, valueType);
            return getObjectMapper().readValue(content, javaType);
        } catch (Exception e) {
            throw new ZestException(e);
        }
    }

    public static <T> T fromJson(String content, Class<T> valueType) {
        try {
            if (StringUtils.isBlank(content)) {
                return null;
            }
            return getObjectMapper().readValue(content, valueType);
        } catch (Exception e) {
            throw new ZestException(e);
        }
    }

    private static ObjectMapper getObjectMapper() {
        ObjectMapper objectMapper = OBJECT_MAPPER_CACHE.get();
        if (objectMapper == null) {
            objectMapper = new ObjectMapper();
            objectMapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
            objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
            OBJECT_MAPPER_CACHE.set(objectMapper);
        }
        return objectMapper;
    }
}
