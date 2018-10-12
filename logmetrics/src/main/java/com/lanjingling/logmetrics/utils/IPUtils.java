package com.lanjingling.logmetrics.utils;

import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class IPUtils {
	private Logger logger = LoggerFactory.getLogger(getClass());

	//缓存
	private static String serverIp;
	
	public static String getServerIp() {
		if (serverIp != null) {
			return serverIp;
		}
		// 一个主机有多个网络接口
		try {
			Enumeration<NetworkInterface> netInterfaces = NetworkInterface.getNetworkInterfaces();
			while (netInterfaces.hasMoreElements()) {
				NetworkInterface netInterface = netInterfaces.nextElement();
				// 每个网络接口,都会有多个"网络地址",比如一定会有lookback地址,会有siteLocal地址等.以及IPV4或者IPV6 .
				Enumeration<InetAddress> addresses = netInterface.getInetAddresses();
				while (addresses.hasMoreElements()) {
					InetAddress address = addresses.nextElement();
					if(address instanceof Inet6Address){
						continue;
					}
					if (address.isSiteLocalAddress() && !address.isLoopbackAddress()) {
						serverIp = address.getHostAddress();
						continue;
					}
				}
			}
		} catch (SocketException e) {
			e.printStackTrace();
		}
		if (serverIp!=null) {
			serverIp = serverIp.replaceAll("\\.","_");
		}
		return serverIp;
	}

	public void reset() {
		serverIp = null;
	}
	
	public static void main(String...strings) {
		System.out.println(getServerIp());
	}
}
