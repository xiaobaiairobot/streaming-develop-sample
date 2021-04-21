# 数据中台流计算SDK开发范例

本工程由云粒智慧大数据团队提供，面向合作单位和项目交付部门提供实时数据开发的SDK使用范例


## 范例说明

共包含3个子工程，分别为：

### sdk-no-result-demo
范例中最简单的demo，根据降雨量阈值产生告警信息，并在控制台打印

### sdk-rain-warning-demo
实时流数据与数据库中的维度表关联的范例，根据降雨数据超过阈值则产生预警，预警中包含测站名称。其中测站名称来源于MySQL数据库

### sdk-rain-window-warning-demo
带滑动窗口的计算范例，根据N分钟降雨总量计算是否超过阈值，如果超过则产生预警。

## 使用方式

基础SDK包在项目lib目录下，如果需要完全本地开发，请在lib目录下使用mvn命令安装到本地：

```
#streaming-sdk-2.1.0-SNAPSHOT.jar
mvn install:install-file -Dfile=streaming-sdk-2.1.0-SNAPSHOT.jar -DgroupId=com.yunli.bigdata.streaming -DartifactId=streaming-sdk -Dversion=2.1.0-RELEASE -Dpackaging=jar

```
然后再运行本范例