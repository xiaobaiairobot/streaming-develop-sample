package com.yunli.bigdata.rain.domain;

import java.io.Serializable;

public class WarnDomain implements Serializable {
  private static final long serialVersionUID = 1L;

  private String message;

  public String getMessage() {
    return message;
  }

  public void setMessage(String message) {
    this.message = message;
  }

  public WarnDomain() {
  }

  public WarnDomain(String message) {
    this.message = message;
  }

  @Override
  public String toString() {
    return "WarnDomain{" +
        "message='" + message + '\'' +
        '}';
  }
}
