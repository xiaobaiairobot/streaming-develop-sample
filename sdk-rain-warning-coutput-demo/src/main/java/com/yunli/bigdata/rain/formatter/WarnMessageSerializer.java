package com.yunli.bigdata.rain.formatter;

import java.io.Serializable;
import java.nio.charset.StandardCharsets;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.yunli.bigdata.streaming.Serializer;
import com.yunli.bigdata.util.JsonUtil;

public class WarnMessageSerializer implements Serializer {
  @Override
  public byte[] serialize(Serializable obj) throws Exception {
    try {
      return JsonUtil.writeValueAsString(obj).getBytes(StandardCharsets.UTF_8);
    } catch (JsonProcessingException e) {
      return null;
    }
  }
}
