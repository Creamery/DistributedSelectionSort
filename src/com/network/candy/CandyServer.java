package com.network.candy;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;
public class CandyServer extends Thread {

	   private ServerSocket serverSocket;
	   private InetAddress address;
	   
	   public CandyServer(int port) throws IOException {
	      serverSocket = new ServerSocket(port);
	      
	      System.out.println("Server Timeout is "+serverSocket.getSoTimeout());
	      // Set how long the server will wait for a connection
//	      serverSocket.setSoTimeout(1000000000);
	   }

	   public void run() {
		   while(true) {
			   try {
				   System.out.println("[SERVER]: "+"Waiting for client on port " + 
	               serverSocket.getLocalPort() + "...");
				   Socket server = serverSocket.accept();
				   System.out.println("[SERVER]: "+"Just connected to " + server.getRemoteSocketAddress());
				   DataInputStream in = new DataInputStream(server.getInputStream());

				   System.out.println(in.readUTF());
				   DataOutputStream out = new DataOutputStream(server.getOutputStream());
				   out.writeUTF("[SERVER]: "+"Thank you for connecting to " + server.getLocalSocketAddress()
				   	+ "\nGoodbye!");
				   server.close();
	            
	         } catch (SocketTimeoutException s) {
	        	 System.out.println("[SERVER]: "+"Socket timed out!");
	        	 break;
	         } catch (IOException e) {
	        	 e.printStackTrace();
	        	 break;
	         }
	      }
	   }

	public InetAddress getAddress() {
		return address;
	}

	public void setAddress(InetAddress address) {
		this.address = address;
	}
}
