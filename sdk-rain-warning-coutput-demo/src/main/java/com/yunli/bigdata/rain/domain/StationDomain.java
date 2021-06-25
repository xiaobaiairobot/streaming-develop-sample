package com.yunli.bigdata.rain.domain;

import java.io.Serializable;

/**
 * @author david
 * @date 2021/3/31 3:26 下午
 */
public class StationDomain implements Serializable {
  private String stcd;

  private String stnm;

  private String sttp;

  private Double lgtd;

  private Double lttd;

  public StationDomain() {
  }

  public StationDomain(String stcd, String stnm, String sttp, Double lgtd, Double lttd) {
    this.stcd = stcd;
    this.stnm = stnm;
    this.sttp = sttp;
    this.lgtd = lgtd;
    this.lttd = lttd;
  }

  public String getStcd() {
    return stcd;
  }

  public void setStcd(String stcd) {
    this.stcd = stcd;
  }

  public String getStnm() {
    return stnm;
  }

  public void setStnm(String stnm) {
    this.stnm = stnm;
  }

  public String getSttp() {
    return sttp;
  }

  public void setSttp(String sttp) {
    this.sttp = sttp;
  }

  public Double getLgtd() {
    return lgtd;
  }

  public void setLgtd(Double lgtd) {
    this.lgtd = lgtd;
  }

  public Double getLttd() {
    return lttd;
  }

  public void setLttd(Double lttd) {
    this.lttd = lttd;
  }

  @Override
  public String toString() {
    return "StationDomain{" +
        "stcd='" + stcd + '\'' +
        ", stnm='" + stnm + '\'' +
        ", sttp='" + sttp + '\'' +
        ", lgtd=" + lgtd +
        ", lttd=" + lttd +
        '}';
  }
}
