package com.main;

import java.net.InetAddress;
import java.net.UnknownHostException;

public class Info {
	
	public static int PORT = 80;
	public static int BROADCAST_PORT = 4445;

	public static String BROADCAST_IP = "255.255.255.255";

	public static String PUBLIC_NET = "49.147.224.188";
	public static String LOCAL_NET = "192.168.96.105";
	public static String ETHER_NET = "169.254.233.8";
	
	public static String NETWORK = GetSelfAddress();
	public static int SERVER_TIMEOUT = 0;
	public static int BUFFER_SIZE = 256;
	
	public static String MSG_CLIENT_RECEIVED = "msgClientReceived";
	public static String MSG_SERVER_ARRAY = "msgServerArray";
	

	public static String HDR_SPLIT = "_";
	public static String HDR_SERVER = "hdrServer";
	public static String HDR_CLIENT = "hdrClient";
	public static String HDR_SERVER_INDICES = "hdrIndices";
	public static String HDR_CLIENT_END = "hdrClientEnd";
	
	public static int CLIENT_SIZE = 2;
	
	public static String GetSelfAddress() {
		try {
			return InetAddress.getLocalHost().toString();
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}
		return "";
	}

	public static int getBUFFER_SIZE() {
		return BUFFER_SIZE;
	}

	public static void setBUFFER_SIZE(int bUFFER_SIZE) {
		BUFFER_SIZE = bUFFER_SIZE;
	}

	public static long TIMEOUT_DELAY = 10000;
}
