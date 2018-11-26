package com.controller;

import java.io.IOException;
import java.net.InetAddress;
import java.util.ArrayList;

import com.main.Info;
import com.network.MainServer;

public class ServerController {
	private MainServer mainServer;
	private InetAddress address;
	
	private ArrayList<ClientController> listClients;
	
	// Add functionality to start as a server
	public void start() {
		try {
			this.setMainServer(new MainServer(Info.PORT));
		} catch (IOException e) {
			e.printStackTrace();
		}
		// Start thread
		this.getMainServer().run();
	}
	
	public void broadcast() {
		
	}
	public void stop() {
		// End the thread
		this.getMainServer().end();
	}

	public ArrayList<ClientController> getListClients() {
		return listClients;
	}

	public void setListClients(ArrayList<ClientController> listClients) {
		this.listClients = listClients;
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
