package com.yunli.bigdata.rain.context;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.yunli.bigdata.rain.domain.StationDomain;

/**
 * @author david
 * @date 2021/4/1 9:46 上午
 */
public class RainWarningContext implements Serializable {
  public static List<StationDomain> stations = new ArrayList<>();
}
