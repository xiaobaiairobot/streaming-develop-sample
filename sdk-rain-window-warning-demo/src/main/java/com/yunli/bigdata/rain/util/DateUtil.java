package com.yunli.bigdata.rain.util;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author david
 * @date 2021/5/20 9:43 上午
 */
public class DateUtil {
  public static final String STANDARD = "yyyy-MM-dd HH:mm:ss";

  public static String toStandardString(Date date) {
    return new SimpleDateFormat(STANDARD).format(date);
  }
}
