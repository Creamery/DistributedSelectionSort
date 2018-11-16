package com.sample;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

public class SampleClient extends Thread {
	private String host;
	private String port;
	
	public void start(String host, String port) {
		this.setHost(host);
		this.setPort(port);
		this.run();
	}
	
	public void run() {
		System.out.println("[CLIENT]: "+"Running SampleClient...");
		String serverName = this.getHost();
		int port = Integer.parseInt(this.getPort());
		try {
			System.out.println("[CLIENT]: "+"Connecting to " + serverName + " on port " + port);
			Socket client = new Socket(serverName, port);
	        
	        System.out.println("[CLIENT]: "+"Just connected to " + client.getRemoteSocketAddress());
	        OutputStream outToServer = client.getOutputStream();
	        DataOutputStream out = new DataOutputStream(outToServer);
	        
	        out.writeUTF("[CLIENT]: "+"Hello from " + client.getLocalSocketAddress());
	        InputStream inFromServer = client.getInputStream();
	        DataInputStream in = new DataInputStream(inFromServer);
	        
	        System.out.println("[CLIENT]: "+"Server says " + in.readUTF());
	        client.close();
	     }
		catch (IOException e) {
			e.printStackTrace();
		}
	}

	public String getHost() {
		return host;
	}

	public void setHost(String host) {
		this.host = host;
	}

	public String getPort() {
		return port;
	}

	public void setPort(String port) {
		this.port = port;
	}
}
