package com.controller;

import java.net.InetAddress;

public class ClientController {
	InetAddress address;
	ServerController server;

	// Add functionality to start as a client
	public void start() {
		
	}
	
	public ServerController getServer() {
		return server;
	}

	public void setServer(ServerController server) {
		this.server = server;
	}
	
	public InetAddress getAddress() {
		return address;
	}

	public void setAddress(InetAddress address) {
		this.address = address;
	}
}
