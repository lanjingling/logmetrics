package com.lanjingling.logmetrics;

import java.io.Closeable;
import java.io.IOException;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

import kafka.producer.ProducerConfig;

import org.springframework.beans.factory.InitializingBean;

import com.codahale.metrics.Counter;
import com.codahale.metrics.MetricFilter;
import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.Timer;
import com.lanjingling.logmetrics.utils.IPUtils;
import com.lanjingling.logmetrics.utils.kafka.KafkaReporter;

/**
 * 发送metric到kafka
 * @author kevinliu
 * @time 2018年3月27日
 *
 */
public class KafkaMetricFactory implements InitializingBean,Closeable {
	
	private String projectName="ttengine";
	private String kafkaHost="10.153.168.114";
	private String topic="";
	
	private String timers = "";
	private String counters = "";
	
	private static final MetricRegistry metrics = new MetricRegistry();
	private KafkaReporter kafkaReport;
	
	private ConcurrentHashMap<String,Counter> counterMap = new ConcurrentHashMap<>();
	private ConcurrentHashMap<String,Timer> timerMap = new ConcurrentHashMap<>();

	@Override
	public void close() throws IOException {
		if (kafkaReport!=null) {
			kafkaReport.close();
		}
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		
		String ip = IPUtils.getServerIp();
		
		Properties props = new Properties();
		props.put("metadata.broker.list", kafkaHost);
		props.put("serializer.class", "kafka.serializer.StringEncoder");
		props.put("partitioner.class", "kafka.producer.DefaultPartitioner");
		props.put("request.required.acks", "1");
		
		ProducerConfig config = new ProducerConfig(props);
		
		kafkaReport = KafkaReporter.forRegistry(metrics)
				.config(config)
				.topic(topic)
				.prefix(projectName+"."+ip)
                .convertRatesTo(TimeUnit.SECONDS)
                .convertDurationsTo(TimeUnit.MILLISECONDS)
                .filter(MetricFilter.ALL)
                .build();
		
		kafkaReport.start(1, TimeUnit.MINUTES);
		
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

	public Counter buildCountMetrics(String countName) {
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

	public Timer buildCostMetrics(String timerName) {
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
	
	
	
	
	
	public String getKafkaHost() {
		return kafkaHost;
	}
	public void setKafkaHost(String kafkaHost) {
		this.kafkaHost = kafkaHost;
	}

	public String getTopic() {
		return topic;
	}
	public void setTopic(String topic) {
		this.topic = topic;
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


	public void setProjectName(String projectName) {
		this.projectName = projectName;
	}
	public String getProjectName() {
		return projectName;
	}
}
