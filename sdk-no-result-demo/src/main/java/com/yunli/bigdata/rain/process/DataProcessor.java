package com.yunli.bigdata.rain.process;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.yunli.bigdata.rain.domain.MessageData;
import com.yunli.bigdata.rain.domain.RainDataDomain;
import com.yunli.bigdata.rain.domain.WarnDomain;
import com.yunli.bigdata.streaming.InputMessage;
import com.yunli.bigdata.streaming.OutputMessage;
import com.yunli.bigdata.streaming.Processor;

/**
 * 瞬时降雨量预警
 * @author david
 * @date 2021/3/31 3:25 下午
 */
public class DataProcessor implements Processor {

  private final Logger LOGGER = LoggerFactory.getLogger(DataProcessor.class);

  private Double maxRainThreshold = null;

  @Override
  public List<OutputMessage> process(List<InputMessage> inputs, Map<String, String> args) {
    if (maxRainThreshold == null) {
      String strRainThreshold = args.get("maxRainThreshold");
      if (!StringUtils.isBlank(strRainThreshold)) {
        maxRainThreshold = Double.parseDouble(strRainThreshold);
      }
    }
    List<OutputMessage> list = new ArrayList<>();
    for (InputMessage input : inputs) {
      MessageData messageData = (MessageData) input.getBody();
      if(messageData == null){
        continue;
      }
      for (RainDataDomain rainData : messageData.getData()) {
        if (rainData != null && rainData.getDrp() != null && rainData.getDrp() >= maxRainThreshold) {
          // 超过阈值
          list.add(generateWarnMessage(rainData));
        }
      }
    }
    if (list.size() > 0) {
      return list;
    }
    return null;
  }

  private OutputMessage generateWarnMessage(RainDataDomain rainData) {
    String warnMessage = String
        .format("%s站点在%s降雨量为%s（mm），超过告警阈值%s，请重点关注。", rainData.getStcd(), rainData.getTm(), rainData.getDrp(),
            this.maxRainThreshold);
    LOGGER.info("the warn message is : {}", warnMessage);
    return new OutputMessage(UUID.randomUUID().toString(), new WarnDomain(warnMessage));
  }
}
