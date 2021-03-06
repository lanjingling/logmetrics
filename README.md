# logmetrics
在java代码中，将一些业务指标、性能指标通过logmetrics发送到graphite.

一、介绍：</br>
1.log-metrics是在metrics基础上封装了一层通用逻辑，通过maven引入log-metrics框架后，在你的工程中就可以轻松的收集业务代码耗时、业务逻辑调用次数等多项指标到graphite，最后通过grafana图标展示。</br>
2.对于一些无法聚合或者自定义的metric，log-metrics支持将这些信息发送到kafka，业务方可以编写kafka的consumer解析对应的数据，然后保存到相关数据库中，并且展示成图表。
![logmetrics](https://raw.githubusercontent.com/lanjingling/logmetrics/master/arc.png)

二、使用：</br>
1.mvn deploy该项目到本地私服（没有发布到maven中央仓库）；使用的时候通过如下方式引入：</br>
![logmetrics-graphite](https://raw.githubusercontent.com/lanjingling/logmetrics/master/mvn.png)

2.通过spring初始化：</br>
![logmetrics-graphite](https://raw.githubusercontent.com/lanjingling/logmetrics/master/spr.png)

3.在代码中如下方式：</br>
1）记录耗时</br>
//设置timer context</br>
Context time = GraphiteMetricFactory.buildCostMetrics("rec_cost").time();</br>
//业务逻辑</br>
time.stop();</br>

2）统计：</br>
GraphiteMetricFactory.buildCountMetrics("recall_error").inc();</br>

4.接下来通过配置graphite+grafana可以查看到：</br>
![logmetrics-graphite](https://raw.githubusercontent.com/lanjingling/logmetrics/master/cost.png)