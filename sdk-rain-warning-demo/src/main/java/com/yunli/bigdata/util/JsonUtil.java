package com.yunli.bigdata.util;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

/**
 * @author : zhengyangyong
 */
public class JsonUtil {
  private static final ObjectMapper MAPPER = new ObjectMapper();

  private static final ObjectMapper PRETTY_MAPPER = new ObjectMapper();

  static {
    PRETTY_MAPPER.enable(SerializationFeature.INDENT_OUTPUT);
    MAPPER.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    PRETTY_MAPPER.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
  }

  public static String writeValueAsString(Object obj) throws JsonProcessingException {
    return MAPPER.writeValueAsString(obj);
  }


  public static String writeValueAsPrettyString(Object obj) throws JsonProcessingException {
    return PRETTY_MAPPER.writeValueAsString(obj);
  }

  public static <T> T readValue(String json, Class<T> type) throws IOException {
    return MAPPER.readValue(json, type);
  }

  public static <T> T readValue(String json, TypeReference<T> type) throws IOException {
    return MAPPER.readValue(json, type);
  }


  @SuppressWarnings("unchecked")
  public static <T> List<T> readListValue(String json, Class<T> type) throws IOException {
    JavaType javaType = getCollectionType(ArrayList.class, type);
    return (List<T>) MAPPER.readValue(json, javaType);
  }

  public static JavaType getCollectionType(Class<?> collectionClass, Class<?>... elementClasses) {
    return MAPPER.getTypeFactory().constructParametricType(collectionClass, elementClasses);
  }
}
