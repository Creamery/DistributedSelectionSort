package com.controller;

import java.io.IOException;
import java.net.InetAddress;

import com.network.MainServer;

public class ServerController {
	private MainServer mainServer;
	private InetAddress address;
	
	
	// Add functionality to start as a server
	public void start() {
		try {
			this.setMainServer(new MainServer());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	// Broadcast server IP and listen for replies
	public void broadcast() {
		this.getMainServer().broadcast();
	}
	
	// Start processing
	public void process() {
		this.getMainServer().process();
	}

	public InetAddress getAddress() {
		return address;
	}

	public void setAddress(InetAddress address) {
		this.address = address;
	}

	public MainServer getMainServer() {
		return mainServer;
	}

	public void setMainServer(MainServer mainServer) {
		this.mainServer = mainServer;
	}
}
