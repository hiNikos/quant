/**
 * @(#)Json.java, 2021/12/13.
 */
package com.sauron.eye.util;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.Optional;

public abstract class Json {

    private static final ObjectMapper mapper = createObjectMapper();

    public static byte[] toByteArray(Object object) {
        try {
            return mapper.writeValueAsBytes(object);
        } catch (Exception e) {
            throw new RuntimeException("to byte array error", e);
        }
    }

    public static String toString(Object object) {
        try {
            return mapper.writeValueAsString(object);
        } catch (Exception e) {
            throw new RuntimeException("toJsonString error", e);
        }
    }

    public static <T> T as(byte[] json, Class<T> clazz) {
        try {
            return mapper.readValue(json, clazz);
        } catch (Exception e) {
            throw new RuntimeException("as error", e);
        }
    }

    public static <T> T as(String json, Class<T> clazz) {
        try {
            return mapper.readValue(json, clazz);
        } catch (Exception e) {
            throw new RuntimeException("as error", e);
        }
    }

    public static <T> T as(String json, TypeReference<T> typeReference) {
        try {
            return mapper.readValue(json, typeReference);
        } catch (IOException e) {
            throw new RuntimeException("as error", e);
        }
    }

    public static String prettyPrint(Object object) {
        try {
            return mapper.writerWithDefaultPrettyPrinter().writeValueAsString(object);
        } catch (IOException e) {
            throw new RuntimeException("pretty print error", e);
        }
    }

    public static <T> Optional<T> asOpt(String json, Class<T> clazz) {
        try {
            return Optional.ofNullable(mapper.readValue(json, clazz));
        } catch (Exception e) {
            e.printStackTrace();
            return Optional.empty();
        }
    }

    public static <T> Optional<T> asOpt(String json, TypeReference<T> typeReference) {
        try {
            return Optional.ofNullable(mapper.readValue(json, typeReference));
        } catch (Exception e) {
            e.printStackTrace();
            return Optional.empty();
        }
    }

    public static JsonNode asJsonNode(String json) {
        try {
            return mapper.readTree(json);
        } catch (IOException e) {
            throw new RuntimeException("invalid json string", e);
        }
    }

    public static Map<String, String> asMap(String json) {
        try {
            return mapper.readValue(json, new TypeReference<Map<String, String>>() {
            });
        } catch (Exception e) {
            return Collections.emptyMap();
        }
    }

    public static Map<String, Object> toMap(Object object) {
        return mapper.convertValue(object, Map.class);
    }

    private static ObjectMapper createObjectMapper() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        return mapper;
    }

    /**
     * 解析json string 为指定collection对象
     *
     * @param json            json string
     * @param collectionClass collection class
     * @param eleClass        element class
     * @return 指定collection对象
     */
    public static <T> T asList(String json, Class<? extends Collection> collectionClass, Class eleClass) {
        return getByType(json, mapper.getTypeFactory().constructCollectionType(collectionClass, eleClass));
    }

    /**
     * 解析json byte array为指定collection对象
     *
     * @param json            json byte array
     * @param collectionClass collection class
     * @param eleClass        element class
     * @return 指定collection对象
     */
    public static <T> T asList(byte[] json, Class<? extends Collection> collectionClass, Class eleClass) {
        return getByType(json, mapper.getTypeFactory().constructCollectionType(collectionClass, eleClass));
    }

    /**
     * 解析json string为指定map对象
     *
     * @param json       json string
     * @param mapClass   map class
     * @param keyClass   key class
     * @param valueClass value class
     * @return 指定map对象
     */
    public static <T> T asMap(String json, Class<? extends Map> mapClass, Class keyClass, Class valueClass) {
        return getByType(json, mapper.getTypeFactory().constructMapType(mapClass, keyClass, valueClass));
    }

    /**
     * 解析json byte array为指定map对象
     *
     * @param json       json byte array
     * @param mapClass   map class
     * @param keyClass   key class
     * @param valueClass value class
     * @return 指定map对象
     */
    public static <T> T asMap(byte[] json, Class<? extends Map> mapClass, Class keyClass, Class valueClass) {
        return getByType(json, mapper.getTypeFactory().constructMapType(mapClass, keyClass, valueClass));
    }

    public static boolean isArray(byte[] json) {
        try {
            JsonNode jsonNode = mapper.readTree(json);
            return jsonNode.isArray();
        } catch (IOException e) {
            throw new RuntimeException("invalid json bytes", e);
        }
    }

    public static boolean isArray(String json) {
        try {
            JsonNode jsonNode = mapper.readTree(json);
            return jsonNode.isArray();
        } catch (IOException e) {
            throw new RuntimeException("invalid json string", e);
        }
    }

    private static <T> T getByType(byte[] json, JavaType type) {
        try {
            return mapper.readValue(json, type);
        } catch (Exception e) {
            throw new RuntimeException("get type error", e);
        }
    }

    private static <T> T getByType(String json, JavaType type) {
        try {
            return mapper.readValue(json, type);
        } catch (Exception e) {
            throw new RuntimeException("get type error", e);
        }
    }
}
