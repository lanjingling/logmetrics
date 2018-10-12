package com.lanjingling.logmetrics.utils.kafka;

/**
 * not thread safe.
 * @author kevinliu
 * @time 2018年3月27日
 *
 */
public interface MessageListener {

	/**
	 * receive metrics message, not threadsafe.
	 * 
	 * @param jsonStringMessage
	 */
	public void onMessage(String jsonStringMessage);
}
