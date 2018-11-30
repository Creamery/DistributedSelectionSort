package com.network.protocols;

import java.net.DatagramSocket;

public interface UDPUnpacker {
	// Unpack function
	public void unpack(String message);
	public DatagramSocket getUdpSocket();
}
