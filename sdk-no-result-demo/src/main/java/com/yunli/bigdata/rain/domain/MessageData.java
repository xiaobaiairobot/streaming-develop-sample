package com.yunli.bigdata.rain.domain;

import java.io.Serializable;
import java.util.List;

/**
 * @author david
 * @date 2021/4/14 2:11 下午
 */
public class MessageData implements Serializable {
  private String type;

  private List<RainDataDomain> data;

  public MessageData() {
  }

  public MessageData(String type, List<RainDataDomain> data) {
    this.type = type;
    this.data = data;
  }

  public String getType() {
    return type;
  }

  public void setType(String type) {
    this.type = type;
  }

  public List<RainDataDomain> getData() {
    return data;
  }

  public void setData(List<RainDataDomain> data) {
    this.data = data;
  }
}
