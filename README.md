# logmetrics
在java代码中，将一些业务指标、性能指标通过logmetrics发送到graphite.

一、介绍：
1.log-metrics是在metrics基础上封装了一层通用逻辑，通过maven引入log-metrics框架后，在你的工程中就可以轻松的收集业务代码耗时、业务逻辑调用次数等多项指标到graphite，最后通过grafana图标展示。
2.对于一些无法聚合或者自定义的metric，log-metrics支持将这些信息发送到kafka，业务方可以编写kafka的consumer解析对应的数据，然后保存到相关数据库中，并且展示成图表。

二、使用：
1.mvn deploy该项目到本地私服（没有发布到maven中央仓库）；使用的时候通过如下方式引入
<dependency>
	 <groupId>com.lanjingling</groupId>
	 <artifactId>logmetrics</artifactId>
	 <version>0.0.1-SNAPSHOT</version>
</dependency>

2.通过spring初始化：
<!-- log-metrics   --> 
<bean id="graphiteMetricFactory" class="com.lanjingling.logmetrics.GraphiteMetricFactory"  destroy-method="close"> 
	<property name="projectName" value="h5_engine_server"/> 
	<property name="graphiteHost" value="10.135.16.14" />
	<property name="graphitePort" value="2003" />
	<property name="timers" value="rec_cost" />
	<property name="counters" value="default_rec,recall_error,predict_error,select_error" />
</bean>

3.在代码中如下方式：
1）记录耗时
//设置timer context
Context time = GraphiteMetricFactory.buildCostMetrics("rec_cost").time();
//业务逻辑
time.stop();

2）统计：
GraphiteMetricFactory.buildCountMetrics("recall_error").inc();