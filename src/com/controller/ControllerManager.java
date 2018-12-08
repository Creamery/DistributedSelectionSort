package com.controller;

import com.main.Print;
import com.message.MainMessage;

/**
 * Contains connector functions to alter servers and clients
 * @author Candy
 *
 */
public class ControllerManager {
	private static ControllerManager instance;
	
	private ServerController server;
	private ClientController client;
	
	public static ControllerManager Instance() {
		if(instance == null) {
			instance = new ControllerManager();
		}
		return instance;
	}
	
	public static void StartServer() {
		Print.system("Starting server.");
	}

	public ServerController getServer() {
		if(this.server == null) {
			this.server = new ServerController();
		}
		return server;
	}

	public void setServer(ServerController server) {
		this.server = server;
	}

	public MainMessage getClientMessage() {
		return this.getClient().getMainMessage();	
	}
	public ClientController getClient() {
		if(this.client == null) {
			this.client = new ClientController();
		}
		return client;
	}

	public void setClient(ClientController client) {
		this.client = client;
	}
}
