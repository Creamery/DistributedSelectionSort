package com.network;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;

import com.main.Info;
import com.message.MainMessage;
public class MainServer extends Thread {

	
   private ServerSocket serverSocket;
   private InetAddress address;
   private boolean isRunning;
	
   private DatagramSocket udpSocket;
   private DatagramPacket packet;
   public DatagramPacket getPacket() {
	   return packet;
   }

   public void setPacket(DatagramPacket packet) {
	   this.packet = packet;
   }

   public MainServer(int port) throws IOException {
	   serverSocket = new ServerSocket(port);
	   // Set how long the server will wait for a connection
	   serverSocket.setSoTimeout(0);
   }
   
   public void end() {
	   // Stop thread
	   this.setRunning(false);
   }
   
   public void broadcast() {
	   try {
           this.setUdpSocket(new DatagramSocket(Info.BROADCAST_PORT));
           this.getUdpSocket().setBroadcast(true);
       } catch (SocketException e) {
           e.printStackTrace();
       }
	   
	   // Get value server IP
       byte[] buffer = Info.NETWORK.getBytes();
       
       DatagramPacket packet = null;
       
       try {
    	   packet = new DatagramPacket(buffer, buffer.length, InetAddress.getByName("255.255.255.255"), Info.BROADCAST_PORT);
       } catch (UnknownHostException e1) {
    	   e1.printStackTrace();
       }
       
       try {
           this.getUdpSocket().send(packet);
           System.out.println("Sent message");
       } catch(IOException e){
           e.printStackTrace();
       }
      this.getUdpSocket().close();
   }
   
   public void run() {
	   this.setRunning(true);
	   while(this.isRunning()) {
		   try {
			   System.out.println("[SERVER]: "+"Waiting for client on port " +
					   serverSocket.getLocalPort() + "...");
			   Socket server = serverSocket.accept();

			   System.out.println("[SERVER]: "+"Just connected to " + server.getRemoteSocketAddress());
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
	   
	public boolean isRunning() {
		return isRunning;
	}

	public void setRunning(boolean isRunning) {
		this.isRunning = isRunning;
	}
	   
	   public DatagramSocket getUdpSocket() {
		return udpSocket;
	   }

	   public void setUdpSocket(DatagramSocket udpSocket) {
		this.udpSocket = udpSocket;
	   }

}
