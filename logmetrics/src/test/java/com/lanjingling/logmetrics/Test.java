package com.lanjingling.logmetrics;

import com.codahale.metrics.Timer.Context;

public class Test {

	public static void main(String[] args) {
		//count统计
		GraphiteMetricFactory.buildCountMetrics("recall_error").inc();
		
		//time计时
		Context time = GraphiteMetricFactory.buildCostMetrics("api_rt").time();
		//业务。。。
		time.stop();
	}
}
