package com.network;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;

import com.message.MainMessage;
public class MainServer extends Thread {

	
	   private ServerSocket serverSocket;
	   private InetAddress address;
	   
	   public MainServer(int port) throws IOException {
	      serverSocket = new ServerSocket(port);
	      
	      // Set how long the server will wait for a connection
	      serverSocket.setSoTimeout(1000000000);
	   }

	   public void run() {
		   while(true) {
			   try {
				   System.out.println("[SERVER]: "+"Waiting for client on port " + 
	               serverSocket.getLocalPort() + "...");
				   Socket server = serverSocket.accept();
				   
				   
				   System.out.println("[SERVER]: "+"Just connected to " + server.getRemoteSocketAddress());
				   
				   /*// Data I/O
				   DataInputStream in = new DataInputStream(server.getInputStream());
				   System.out.println(in.readUTF());

				   DataOutputStream out = new DataOutputStream(server.getOutputStream());
				   out.writeUTF("[SERVER]: "+"Thank you for connecting to " + server.getLocalSocketAddress()
				   	+ "\nGoodbye!");
				   	
				   	objOut.writeUTF("[SERVER]: "+"Thank you for connecting to " + server.getLocalSocketAddress()
				   	+ "\nGoodbye!");
				   */
				   
				   // Object I/O
				   ObjectInputStream objIn = new ObjectInputStream(server.getInputStream());
				   try {
					   MainMessage receivedMessage = (MainMessage) objIn.readObject();
					   System.out.println(receivedMessage.getMessage());
				   } catch (ClassNotFoundException e) {
					   e.printStackTrace();
				   }
				   
				   ObjectOutputStream objOut = new ObjectOutputStream(server.getOutputStream());
				   MainMessage sentMessage = new MainMessage();
				   sentMessage.setMessage("[SERVER]: "+"Thank you for connecting to " + server.getLocalSocketAddress()
				   	+ "\nGoodbye!");
				   objOut.writeObject(sentMessage);
				   
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
