package com.yunli.bigdata.rain.loader;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.flink.configuration.Configuration;
import org.apache.flink.streaming.api.functions.source.RichParallelSourceFunction;
import org.apache.flink.streaming.api.functions.source.SourceFunction.SourceContext;
import org.apache.flink.streaming.api.operators.Output;
import org.apache.flink.streaming.api.watermark.Watermark;
import org.apache.flink.streaming.runtime.streamrecord.LatencyMarker;
import org.apache.flink.streaming.runtime.streamrecord.StreamRecord;
import org.apache.flink.util.OutputTag;
import org.apache.flink.util.Preconditions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.yunli.bigdata.rain.context.RainWarningContext;
import com.yunli.bigdata.rain.domain.StationDomain;

/**
 * @author david
 * @date 2021/4/1 9:29 上午
 */
public class StationLoader implements Runnable {

  private final Logger LOGGER = LoggerFactory.getLogger(StationLoader.class);

  public StationLoader(Map<String, String> args) {
    // use args
  }

  @Override
  public void run() {
    RainWarningContext.stations = getStations();
  }


  private List<StationDomain> getStations() {
    MySQLSource mysqlsource = new MySQLSource();
    List<StationDomain> list = new ArrayList<>();
    try {
      Object lock = new Object();
      Output<StreamRecord<StationDomain>> output = new Output<StreamRecord<StationDomain>>() {
        @Override
        public void emitWatermark(Watermark watermark) {

        }

        @Override
        public <X> void collect(OutputTag<X> outputTag, StreamRecord<X> streamRecord) {
          list.add((StationDomain) streamRecord.getValue());
        }

        @Override
        public void emitLatencyMarker(LatencyMarker latencyMarker) {

        }

        @Override
        public void collect(StreamRecord<StationDomain> rainDataDomainStreamRecord) {
          list.add(rainDataDomainStreamRecord.getValue());
          if (RainWarningContext.stations.stream()
              .noneMatch(p -> p.getStcd().equals(rainDataDomainStreamRecord.getValue().getStcd()))) {
            RainWarningContext.stations.add(rainDataDomainStreamRecord.getValue());
          }
          LOGGER.info("add item {}", rainDataDomainStreamRecord.getValue().toString());
        }

        @Override
        public void close() {

        }
      };
      mysqlsource.open(null);
      mysqlsource.run(new NonTimestampContext(lock, output));
    } catch (Exception e) {
      e.printStackTrace();
    } finally {
      try {
        mysqlsource.close();
      } catch (Exception e) {
        e.printStackTrace();
      }
    }
    return list;
  }

  public static class MySQLSource extends RichParallelSourceFunction<StationDomain> {
    private boolean flag = true;

    private Connection conn = null;

    private PreparedStatement ps = null;

    private ResultSet rs = null;

    //open只执行一次,适合开启资源
    @Override
    public void open(Configuration parameters) throws Exception {
      conn = DriverManager.getConnection("jdbc:mysql://172.30.13.179:3306/test_db", "root", "yunli2@AUG");
      String sql = "select stcd,stnm,sttp,lgtd,lttd from st_stbprp_b";
      ps = conn.prepareStatement(sql);
    }

    @Override
    public void run(SourceContext<StationDomain> ctx) throws Exception {
      while (flag) {
        rs = ps.executeQuery();//执行查询
        while (rs.next()) {
          String stcd = rs.getString("stcd");
          String stnm = rs.getString("stnm");
          String sttp = rs.getString("sttp");
          Double lgtd = rs.getDouble("lgtd");
          Double lttd = rs.getDouble("lttd");
          ctx.collect(new StationDomain(stcd, stnm, sttp, lgtd, lttd));
        }
        // 指定更新时间
        Thread.sleep(5000);
      }
    }

    //接收到cancel命令时取消数据生成
    @Override
    public void cancel() {
      flag = false;
    }

    //close里面关闭资源
    @Override
    public void close() throws Exception {
      if (conn != null) {
        conn.close();
      }
      if (ps != null) {
        ps.close();
      }
      if (rs != null) {
        rs.close();
      }
    }
  }

  private static class NonTimestampContext<T> implements SourceContext<T> {
    private final Object lock;

    private final Output<StreamRecord<T>> output;

    private final StreamRecord<T> reuse;

    private NonTimestampContext(Object checkpointLock, Output<StreamRecord<T>> output) {
      this.lock = Preconditions.checkNotNull(checkpointLock, "The checkpoint lock cannot be null.");
      this.output = (Output) Preconditions.checkNotNull(output, "The output cannot be null.");
      this.reuse = new StreamRecord((Object) null);
    }

    @Override
    public void collect(T element) {
      synchronized (this.lock) {
        this.output.collect(this.reuse.replace(element));
      }
    }

    @Override
    public void collectWithTimestamp(T element, long timestamp) {
      this.collect(element);
    }

    @Override
    public void emitWatermark(Watermark mark) {
    }

    @Override
    public void markAsTemporarilyIdle() {
    }

    @Override
    public Object getCheckpointLock() {
      return this.lock;
    }

    @Override
    public void close() {
    }
  }
}
