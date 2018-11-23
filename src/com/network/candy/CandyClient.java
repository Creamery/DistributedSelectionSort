package com.network.candy;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;

public class CandyClient extends Thread {
	private String host;
	private int port;
	private InetAddress address;
	
	public void start(String host, String address, String port) throws UnknownHostException {
		this.setHost(host);
		this.setPort(Integer.parseInt(port));
		this.setAddress(InetAddress.getByName(address));
		
		this.run();
	}
	
	public void run() {
		System.out.println("[CLIENT]: "+"Running CandyClient...");
		
		String serverName = this.getHost();
		InetAddress ip = this.getAddress();
		int port = this.getPort();
		
		try {
			System.out.println("[CLIENT]: "+"Connecting to " + serverName + " on port " + port);
			Socket client = new Socket(ip, port);
	       
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

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}

	public InetAddress getAddress() {
		return address;
	}

	public void setAddress(InetAddress address) {
		this.address = address;
	}
}
