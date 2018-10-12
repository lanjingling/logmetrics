package com.lanjingling.logmetrics;

import java.io.Closeable;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.InitializingBean;

import com.codahale.metrics.Counter;
import com.codahale.metrics.MetricAttribute;
import com.codahale.metrics.MetricFilter;
import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.Timer;
import com.codahale.metrics.graphite.Graphite;
import com.lanjingling.logmetrics.utils.GraphiteReport;
import com.lanjingling.logmetrics.utils.IPUtils;

/**
 * 发送metric到graphite
 * @author kevinliu
 * @time 2018年3月26日
 *
 */
public class GraphiteMetricFactory implements InitializingBean,Closeable {
	
	private String projectName="ttengine";
	private String graphiteHost="10.153.168.114";
	private String graphitePort="2003";
	private String timers = "";
	private String counters = "";
	
	private static final MetricRegistry metrics = new MetricRegistry();
	private GraphiteReport graphiteReport;
	
	private static ConcurrentHashMap<String,Counter> counterMap = new ConcurrentHashMap<>();
	private static ConcurrentHashMap<String,Timer> timerMap = new ConcurrentHashMap<>();

	@Override
	public void close() throws IOException {
		if (graphiteReport!=null) {
			graphiteReport.close();
		}
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		
		String ip = IPUtils.getServerIp();
		
		Set<MetricAttribute> set = new HashSet<>();
		set.add(MetricAttribute.M1_RATE);
		set.add(MetricAttribute.M5_RATE);
		set.add(MetricAttribute.M15_RATE);
		set.add(MetricAttribute.MEAN_RATE);
		set.add(MetricAttribute.P999);
		set.add(MetricAttribute.STDDEV);
		set.add(MetricAttribute.P99);
		set.add(MetricAttribute.P50);
		set.add(MetricAttribute.P98);
		set.add(MetricAttribute.COUNT);
		
		Graphite graphite = new Graphite(new InetSocketAddress(graphiteHost, Integer.parseInt(graphitePort)));
		graphiteReport = GraphiteReport.forRegistry(metrics)
                .prefixedWith(projectName+"."+ip)
                .convertRatesTo(TimeUnit.SECONDS)
                .convertDurationsTo(TimeUnit.MILLISECONDS)
                .disabledMetricAttributes(set)
                .filter(MetricFilter.ALL)
                .build(graphite);
		
		graphiteReport.start(1, TimeUnit.MINUTES);
		
		if (!"".equals(timers)) {
			String[] split = timers.split(",");
			for (String str:split) {
				timerMap.put(str, metrics.timer(str+".cost"));
			}
		}
		if (!"".equals(counters)) {
			String[] split = counters.split(",");
			for (String str:split) {
				counterMap.put(str, metrics.counter(str));
			}
		}
		
	}

	public static Counter buildCountMetrics(String countName) {
		Counter counter = counterMap.get(countName);
		if (counter == null) {
			counter = metrics.counter(countName);
			Counter tmp = counterMap.putIfAbsent(countName, counter);
			if (tmp != null) {  
				counter = tmp;  
            }  
		}
		
		return counter;
	}

	public static Timer buildCostMetrics(String timerName) {
		Timer timer = timerMap.get(timerName);
		if(timer == null) {
			timer = metrics.timer(timerName+".cost");
			Timer tmp = timerMap.putIfAbsent(timerName, timer);
			if (tmp != null) {
				timer = tmp;
			}
		}
		return timer;
	}
	
	
	
	
	
	public static MetricRegistry getMetrics() {
		return metrics;
	}

	public String getTimers() {
		return timers;
	}
	public void setTimers(String timers) {
		this.timers = timers;
	}

	public String getCounters() {
		return counters;
	}
	public void setCounters(String counters) {
		this.counters = counters;
	}

	public String getGraphiteHost() {
		return graphiteHost;
	}
	public void setGraphiteHost(String graphiteHost) {
		this.graphiteHost = graphiteHost;
	}

	public String getGraphitePort() {
		return graphitePort;
	}
	public void setGraphitePort(String graphitePort) {
		this.graphitePort = graphitePort;
	}

	public GraphiteReport getGraphiteReport() {
		return graphiteReport;
	}
	public void setProjectName(String projectName) {
		this.projectName = projectName;
	}

	public String getProjectName() {
		return projectName;
	}
	public void setGraphiteReport(GraphiteReport graphiteReport) {
		this.graphiteReport = graphiteReport;
	}
}
