package com.yunli.bigdata.rain.domain;

import java.io.Serializable;
import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;

/**
 * @author david
 * @date 2021/3/31 3:24 下午
 */
public class RainDataDomain implements Serializable {
  // 站码
  private String stcd;

  // 时间
  @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
  private Date tm;

  // 时段降雨量
  private Double drp;

  // 日雨量
  private Double dyp;

  public RainDataDomain() {
  }

  public RainDataDomain(String stcd, Date tm, Double drp, Double dyp) {
    this.stcd = stcd;
    this.tm = tm;
    this.drp = drp;
    this.dyp = dyp;
  }

  public String getStcd() {
    return stcd;
  }

  public void setStcd(String stcd) {
    this.stcd = stcd;
  }

  public Date getTm() {
    return tm;
  }

  public void setTm(Date tm) {
    this.tm = tm;
  }

  public Double getDrp() {
    return drp;
  }

  public void setDrp(Double drp) {
    this.drp = drp;
  }

  public Double getDyp() {
    return dyp;
  }

  public void setDyp(Double dyp) {
    this.dyp = dyp;
  }

  @Override
  public String toString() {
    return "RainDataDomain{" +
        "stcd='" + stcd + '\'' +
        ", tm=" + tm +
        ", drp=" + drp +
        ", dyp=" + dyp +
        '}';
  }
}
