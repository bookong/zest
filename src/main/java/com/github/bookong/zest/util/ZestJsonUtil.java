/**
 * Copyright 2014-2020 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.github.bookong.zest.util;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.github.bookong.zest.exception.ZestException;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.List;

/**
 * Serialization/deserialization JSON related functions.
 *
 * @author Jiang Xu
 */
public class ZestJsonUtil {

    public static final Logger                     logger              = LoggerFactory.getLogger(ZestJsonUtil.class);
    private static final ThreadLocal<ObjectMapper> OBJECT_MAPPER_CACHE = new ThreadLocal<>();

    /**
     * Serialize the object into a JSON string.
     *
     * @param obj
     *          An object.
     * @return a JSON string.
     */
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

    /**
     * Serialize the object into a pretty JSON string.
     *
     * @param obj
     *          An object.
     * @return a JSON string.
     */
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

    /**
     * Deserialize from a JSON array object into a list.
     *
     * @param content
     *          JSON content.
     * @param valueType
     *          Value class object.
     * @param <T>
     *          Value class.
     * @return object list.
     */
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

    /**
     * Deserialize from a JSON string into an object.
     *
     * @param content
     *          JSON content.
     * @param valueType
     *          Value class object.
     * @param <T>
     *          Value class.
     * @return object.
     */
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
