package com.network;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;

import com.message.MainMessage;

public class MainClient extends Thread {
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
		System.out.println("[CLIENT]: "+"Running MainClient...");
		
		String serverName = this.getHost();
		InetAddress ip = this.getAddress();
		int port = this.getPort();
		
		try {
			System.out.println("[CLIENT]: "+"Connecting to " + serverName + " on port " + port);
			Socket client = new Socket(ip, port);
	       
	        System.out.println("[CLIENT]: "+"Just connected to " + client.getRemoteSocketAddress());
	        // Object I/O Stream
	        OutputStream outToServer = client.getOutputStream();
	        ObjectOutputStream out = new ObjectOutputStream(outToServer);
	        
//	        out.writeUTF("[CLIENT]: "+"Hello from " + client.getLocalSocketAddress());
	        MainMessage sentMessage = new MainMessage();
	        sentMessage.setMessage("[CLIENT]: "+"Hello from " + client.getLocalSocketAddress());
	        out.writeObject(sentMessage);
	        
	        InputStream inFromServer = client.getInputStream();
	        ObjectInputStream in = new ObjectInputStream(inFromServer);
	      
	        try {
				MainMessage receivedMessage = (MainMessage) in.readObject();
		        System.out.println("[CLIENT]: "+"Server says " + receivedMessage.getMessage());
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			}
	        
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
