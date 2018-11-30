package com.controller;

import java.net.InetAddress;
import java.net.UnknownHostException;

import com.network.MainClient;

public class ClientController {
	private MainClient client;
	
	// Create a client then start listening
	public void start() {
		this.createClient();
		this.listen();
	}
	
	// Create a new client
	public void createClient() {
		this.setClient(new MainClient());
	}
	
	// Listen for any server broadcast to connect to
	public void listen() {
		if(this.getClient() == null) {
			this.createClient();
		}
		this.getClient().listen();
	}
	
	public void send(int index, int value) {
		this.getClient().send(index, value);
	}
	
	public void stopListening() {
		this.getClient().stopListening();
	}
	public InetAddress getServerIP() {
		return this.getClient().getServerIP();
	}

	public void setServerIP(String server) {
		try {
			this.getClient().setServerIP(InetAddress.getByName(server));
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}
	}
	
	public MainClient getClient() {
		return client;
	}

	public void setClient(MainClient client) {
		this.client = client;
	}

	public InetAddress getAddress() {
		return this.getClient().getAddress();
	}

	public void setAddress(InetAddress address) {
		this.getClient().setAddress(address);
	}
}
