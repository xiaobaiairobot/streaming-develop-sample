package com.yunli.bigdata.rain.output;

import java.util.Map;

/**
 * @author chenxiaogang
 * @date 2021/6/25 14:42
 */
public class Configration {

  private String host = null;
  private Integer port = null;
  private String password = null;
  private String user = null;

  public Configration(Map<String, String> args) {
    // use args
    this.host = args.get("omysqlhost");
    this.port = Integer.valueOf(args.get("omysqlport"));
    this.password = args.get("omysqlpassword");
    this.user = args.get("omysqluser");
  }

  public String getHost() {
    return host;
  }

  public void setHost(String host) {
    this.host = host;
  }

  public Integer getPort() {
    return port;
  }

  public void setPort(Integer port) {
    this.port = port;
  }

  public String getPassword() {
    return password;
  }

  public void setPassword(String password) {
    this.password = password;
  }

  public String getUser() {
    return user;
  }

  public void setUser(String user) {
    this.user = user;
  }
}
