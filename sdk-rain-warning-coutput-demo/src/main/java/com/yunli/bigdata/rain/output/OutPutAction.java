package com.yunli.bigdata.rain.output;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;
import java.util.concurrent.BlockingDeque;
import java.util.concurrent.LinkedBlockingDeque;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;



/**
 * @author chenxiaogang
 * @date 2021/6/25 14:18
 */
public class OutPutAction  implements Runnable {

  private final Logger LOGGER = LoggerFactory.getLogger(OutPutAction.class);

  private Configration config = null;

  private boolean flag = true;

  private Connection conn = null;

  private PreparedStatement ps = null;

  private ResultSet rs = null;

  private BlockingDeque<String> queue = new LinkedBlockingDeque<>();

  public BlockingDeque<String> getQueue() {
    return queue;
  }

  public void setQueue(BlockingDeque<String> queue) {
    this.queue = queue;
  }

  public OutPutAction(Map<String, String> args){
    config = new Configration(args);
  }

  @Override
  public void run() {
    try {
      open();
    } catch (Exception e) {
      e.printStackTrace();
    }
    String sql = "insert into warn_message(message) values(?)";
    while (flag) {
      /**
       * CREATE TABLE `warn_message` (
       *          `id` bigint(20) NOT NULL AUTO_INCREMENT,
       *          `message` varchar(512) DEFAULT NULL,
       *          `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ,
       *          PRIMARY KEY (`id`)
       *        ) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4;
       */
      try {
        String take = queue.take();
        ps = conn.prepareStatement(sql);
        ps.setString(1, take);
        ps.execute();
        LOGGER.info("插入成功 ");
      } catch (SQLException e) {
        e.printStackTrace();
      }catch (InterruptedException e) {

      }
      try {
        Thread.sleep(50);
      } catch (InterruptedException e) {
        e.printStackTrace();
      }
    }
  }

  //open只执行一次,适合开启资源
  public void open() throws Exception {
    String host = config.getHost();
    Integer port = config.getPort();
    String password = config.getPassword();
    String user = config.getUser();
    Class.forName(com.mysql.jdbc.Driver.class.getName());  //注册数据库驱动
    conn = DriverManager
        .getConnection("jdbc:mysql://"+host+":"+port+"/test_db", user,password);
  }

  //接收到cancel命令时取消数据生成
  public void cancel() {
    flag = false;
  }

  //close里面关闭资源
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
