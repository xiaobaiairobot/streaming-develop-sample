package com.yunli.bigdata.rain.filter;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.yunli.bigdata.rain.domain.MessageData;
import com.yunli.bigdata.rain.domain.RainDataDomain;
import com.yunli.bigdata.streaming.Filter;
import com.yunli.bigdata.streaming.InputMessage;

/**
 * @author david
 * @date 2021/4/7 4:53 下午
 */
public class OutlierFilter implements Filter {
  private final Logger LOGGER = LoggerFactory.getLogger(OutlierFilter.class);

  @Override
  public boolean filter(InputMessage input, Map<String, String> args) throws Exception {
    MessageData rainData = (MessageData) input.getBody();
    if (rainData == null) {
      return false;
    }
    if (!"rain".equals(rainData.getType())) {
      LOGGER.info("skip the data which type is not matched , type : {}, data: {}", rainData.getType(),
          rainData.getData());
      return false;
    }
    return true;
  }
}
