package com.yunli.bigdata.rain.process;

import java.util.Collections;
import java.util.Date;
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
    LOGGER.info("the data size is : {}", inputs.size());
    Double sumRain = 0D;
    Date dtMin = new Date(0);
    Date dtMax = new Date(0);
    for (InputMessage input : inputs) {
      MessageData messageData = (MessageData) input.getBody();
      for (RainDataDomain rainData : messageData.getData()) {
        if (rainData.getTm().getTime() > dtMax.getTime()) {
          dtMax = rainData.getTm();
        }
        if (dtMin.getTime() == new Date(0).getTime()) {
          dtMin = rainData.getTm();
        }
        if (rainData.getTm().getTime() < dtMin.getTime()) {
          dtMin = rainData.getTm();
        }
        sumRain += rainData.getDrp();
        if (sumRain >= maxRainThreshold) {
          // 超过阈值
          return Collections.singletonList(
              generateWarnMessage(rainData, sumRain, dtMin, dtMax)
          );
        } else {
          LOGGER.info("the normal rain sum:{}, data is : {}", sumRain, rainData);
        }
      }
    }
    return null;
  }

  private OutputMessage generateWarnMessage(RainDataDomain rainData, Double sumRain, Date dtStart, Date dtEnd) {
    String warnMessage = String
        .format("%s站点在 %s ~ %s 区间，累积降雨量为%smm，超过告警阈值%s，请重点关注。", rainData.getStcd(), dtStart, dtEnd, sumRain,
            this.maxRainThreshold);
    LOGGER.info("the warn message is : {}", warnMessage);
    return new OutputMessage(UUID.randomUUID().toString(), new WarnDomain(warnMessage));
  }
}
