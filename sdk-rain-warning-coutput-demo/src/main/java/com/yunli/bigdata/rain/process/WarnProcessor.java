package com.yunli.bigdata.rain.process;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.yunli.bigdata.rain.context.RainWarningContext;
import com.yunli.bigdata.rain.domain.MessageData;
import com.yunli.bigdata.rain.domain.RainDataDomain;
import com.yunli.bigdata.rain.domain.StationDomain;
import com.yunli.bigdata.rain.domain.WarnDomain;
import com.yunli.bigdata.rain.loader.StationLoader;
import com.yunli.bigdata.rain.output.OutPutAction;
import com.yunli.bigdata.streaming.InputMessage;
import com.yunli.bigdata.streaming.OutputMessage;
import com.yunli.bigdata.streaming.Processor;

/**
 * 瞬时降雨量预警
 * @author david
 * @date 2021/3/31 3:25 下午
 */
public class WarnProcessor implements Processor {

  private final Logger LOGGER = LoggerFactory.getLogger(WarnProcessor.class);

  private transient ExecutorService fixedExecutor;

  private Double maxRainThreshold = null;

  private transient StationLoader stationLoader = null;

  private OutPutAction outPutAction = null;

  @Override
  public List<OutputMessage> process(List<InputMessage> inputs, Map<String, String> args) {
    if( fixedExecutor == null){
      fixedExecutor = new ThreadPoolExecutor(3, 3, 0L, TimeUnit.MILLISECONDS,
          new LinkedBlockingQueue<Runnable>(1024), new ThreadFactoryBuilder()
          .setNameFormat("datasource_pool").build(), new ThreadPoolExecutor.AbortPolicy());
    }

    if (stationLoader == null) {
      stationLoader = new StationLoader(args);
      fixedExecutor.execute(stationLoader);
    }

    if(outPutAction == null){
      //输出数据操作
      outPutAction = new OutPutAction(args);
      fixedExecutor.execute(outPutAction);
    }

    if (maxRainThreshold == null) {
      String strRainThreshold = args.get("maxRainThreshold");
      if (!StringUtils.isBlank(strRainThreshold)) {
        maxRainThreshold = Double.parseDouble(strRainThreshold);
      }
    }
    boolean hasStation = false;
    while (!hasStation) {
      if (RainWarningContext.stations != null && RainWarningContext.stations.size() > 0) {
        hasStation = true;
      } else {
        try {
          Thread.sleep(500);
        } catch (InterruptedException e) {
          e.printStackTrace();
        }
      }
    }
    for (InputMessage input : inputs) {
      MessageData messageData = (MessageData) input.getBody();
      List<RainDataDomain> listRain = messageData.getData();
      for (RainDataDomain rainData : listRain) {
        if (rainData != null && rainData.getDrp() != null && rainData.getDrp() >= maxRainThreshold) {
          String outputMessages = generateWarnMessage(rainData);
          outPutAction.getQueue().add(outputMessages);
          // 超过阈值
          return Collections.singletonList( new OutputMessage(UUID.randomUUID().toString(), new WarnDomain(outputMessages)));
        }
      }
    }
    return null;
  }

  private String generateWarnMessage(RainDataDomain rainData) {
    if (RainWarningContext.stations == null || RainWarningContext.stations.size() == 0) {
      LOGGER.error("测站基础信息初始化失败!");
    }
    StationDomain stationDomain = RainWarningContext.stations.stream()
        .filter(p -> p.getStcd().equalsIgnoreCase(rainData.getStcd()))
        .findAny().orElse(null);
    if (stationDomain == null) {
      LOGGER.error("测站{}信息不存在!", rainData.getStcd());
      return null;
    }
    String warnMessage = String
        .format("%s站点在%s降雨量为%smm，超过告警阈值%s，请重点关注。", stationDomain.getStnm(), rainData.getTm(), rainData.getDrp(),
            this.maxRainThreshold);
    LOGGER.info("the warn message is : {}", warnMessage);
    return warnMessage;
  }


}
