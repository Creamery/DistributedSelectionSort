package com.main;

import java.net.InetAddress;
import java.net.UnknownHostException;

public class Info {

	public class FileNames{
		public static final String generated_name_prefix = "unsorted_generated_";
		public static final int _100 = 100;
		public static final int _10000 = 10000;
		public static final int _100000 = 100000;
		public static final int _200000 = 200000;
		public static final String preset_filename = generated_name_prefix+_100+".csv";

	}
	public static int UDP_PACKET_SIZE = 64;
	public static int PORT = 80;
	/**
	 * Used by the clients when sending a request to the server.
	 */
	public static int REQUEST_PORT = 90;
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
	

	public static String HDR_REQUEST = "hdrRequest";
	public static String HDR_PROCESS = "hdrProcess";
	public static String HDR_SWAP = "hdrSwap";
	public static String HDR_END = "hdrEnd";
	public static String HDR_INSTRUCTION = "hdrInstruction";
	public static String HDR_SORTLIST = "hdrSortlist";
	
	
	
	public static String HDR_SPLIT = "_";
	public static String HDR_SERVER = "hdrServer";
	public static String HDR_CLIENT = "hdrClient";
	public static String HDR_SERVER_INDICES = "hdrIndices";
	public static String HDR_CLIENT_END = "hdrClientEnd";
	
	
	
	public static int CLIENT_SIZE = 1;
	
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
