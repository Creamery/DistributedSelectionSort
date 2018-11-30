package com.network.tcp;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;

import com.main.Info;
import com.message.TCPMessage;

public class TCPTwoWay extends Thread {
	
	private boolean isReceiving;
	private TCPMessage tcpMessage;
	private int port;
	
	// For SERVER
	private ServerSocket serverSocket;
	private Socket tcpServerSocket;
	private InetAddress serverIP;
	private ObjectInputStream objectInputStream;
	
	// For CLIENT
	private InetAddress clientIP;
	private ObjectOutputStream objectOutputStream;
	private Socket tcpClientSocket;
	private OutputStream outToServer;
	private DataOutputStream dataOutToServer;
	private InputStream inFromServer;
	private DataInputStream dataInFromServer;
	
	public DataOutputStream getDataOutToServer() {
		return dataOutToServer;
	}

	public void setDataOutToServer(DataOutputStream dataOutToServer) {
		this.dataOutToServer = dataOutToServer;
	}

	// SERVER 
	public TCPTwoWay() throws IOException {

		this.setTcpMessage(new TCPMessage());
		
		// Initialize server socket
		this.setServerSocket(new ServerSocket(Info.PORT));
		this.getServerSocket().setSoTimeout(Info.SERVER_TIMEOUT);
		this.setPort(Info.PORT);
		
		
		// Set clientIP to self IP
		this.setClientIP(InetAddress.getLocalHost());
	}
	
	public void receive() {
		this.setReceiving(true);
	}
	
	// To be called after a successful accept.
	public void InitializeReceiver() throws IOException {
		// Prepare object I/O
		this.setObjectInputStream(new ObjectInputStream(this.getTcpServerSocket().getInputStream()));
		this.setObjectOutputStream(new ObjectOutputStream(this.getTcpServerSocket().getOutputStream()));
		
	}
	
	// Run listener
	public void run() {
		try {
			// Wait for TCP connection from CLIENT
			this.setTcpServerSocket(this.getServerSocket().accept());
			
			// Prompt successful connection
			System.out.println("[SERVER]: "+"Just connected to " + this.getTcpServerSocket().getRemoteSocketAddress());
			
			this.InitializeReceiver();
			
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		
		// Wait for message to receive
		while(isReceiving()) {
			try {

//				System.out.println("[SERVER]: "+"Waiting for client on port " +
//						serverSocket.getLocalPort() + "...");
//				// Wait for TCP connection from CLIENT
//				this.setTcpServerSocket(this.getServerSocket().accept());
//				// Prompt successful connection
//				System.out.println("[SERVER]: "+"Just connected to " + this.getTcpServerSocket().getRemoteSocketAddress());
//				
//				// Prepare object I/O
//				this.setObjectInputStream(new ObjectInputStream(this.getTcpServerSocket().getInputStream()));
			
				
				// RECEIVING a message				
				try {
					// Receive a TCPMessage object from input stream
					TCPMessage receivedMessage = (TCPMessage) this.getObjectInputStream().readObject();
					System.out.println(receivedMessage.getMessage());
				} catch (ClassNotFoundException e) {
					e.printStackTrace();
				}
				
				// SENDING a message
//				this.setObjectOutputStream(new ObjectOutputStream(this.getTcpServerSocket().getOutputStream()));
				
//				this.getTcpMessage().setMessage("Message to send");
//				this.getObjectOutputStream().writeObject(this.getTcpMessage());
				
//				sentMessage.setMessage("[SERVER]: "+"Thank you for connecting to " + server.getLocalSocketAddress()
//					+ "\nGoodbye!");
				
			
			} catch (SocketTimeoutException s) {
				 System.out.println("[SERVER]: "+"Socket timed out!");
				 break;
			 } catch (IOException e) {
				 e.printStackTrace();
				 break;
			 }
		}

		// Close socket when done receiving
		try {
			this.getTcpServerSocket().close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	
	// CLIENT
	
	public void initializeSender(InetAddress serverIP) {
		this.setServerIP(serverIP);
		
		try {
			this.setTcpClientSocket(new Socket(this.getServerIP(), this.getPort()));
			System.out.println("[CLIENT]: "+"Just connected to " + this.getTcpClientSocket().getRemoteSocketAddress());
				
	        this.setOutToServer(this.getTcpClientSocket().getOutputStream());
	        this.setDataOutToServer(new DataOutputStream(outToServer));
	        
	        
	        this.setInFromServer(this.getTcpClientSocket().getInputStream());
	        this.setDataInFromServer(new DataInputStream(inFromServer));
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
	
	// Closes the client socket. Assumes that it has been initialized.
	public void closeSender() {
        try {
			this.getTcpClientSocket().close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/*
	// Send a message to serverIP. Call initializeSender first.
	public void send(int index, int value) {
		System.out.println("[CLIENT]: "+"Running CandyClient...");
		
		
		try {
//			System.out.println("[CLIENT]: "+"Connecting to " + serverName + " on port " + port);

	        // Write to server
	        this.getDataOutToServer().writeUTF("[CLIENT]: "+ this.getTcpClientSocket().getLocalSocketAddress()
	        		+" index: " + index + " value: " + value);

	        
//	        this.setInFromServer(this.getTcpClientSocket().getInputStream());
//	        this.setDataInFromServer(new DataInputStream(inFromServer));
//	        System.out.println("[CLIENT]: "+"Server says " + this.getDataInFromServer().readUTF());
	     }
		catch (IOException e) {
			e.printStackTrace();
		}
	}
	*/
	public void send(int index, int value) {
		
	}

	public boolean isReceiving() {
		return isReceiving;
	}

	public void setReceiving(boolean isReceiving) {
		this.isReceiving = isReceiving;
	}

	public Socket getTcpServerSocket() {
		return tcpServerSocket;
	}

	public void setTcpServerSocket(Socket tcpSocket) {
		this.tcpServerSocket = tcpSocket;
	}

	public ServerSocket getServerSocket() {
		return serverSocket;
	}

	public void setServerSocket(ServerSocket serverSocket) {
		this.serverSocket = serverSocket;
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}

	public InetAddress getClientIP() {
		return clientIP;
	}

	public void setClientIP(InetAddress clientIP) {
		this.clientIP = clientIP;
	}

	public ObjectInputStream getObjectInputStream() {
		return objectInputStream;
	}

	public void setObjectInputStream(ObjectInputStream objectInputStream) {
		this.objectInputStream = objectInputStream;
	}

	public ObjectOutputStream getObjectOutputStream() {
		return objectOutputStream;
	}

	public void setObjectOutputStream(ObjectOutputStream objectOutputStream) {
		this.objectOutputStream = objectOutputStream;
	}

	public TCPMessage getTcpMessage() {
		return tcpMessage;
	}

	public void setTcpMessage(TCPMessage tcpMessage) {
		this.tcpMessage = tcpMessage;
	}

	public Socket getTcpClientSocket() {
		return tcpClientSocket;
	}

	public void setTcpClientSocket(Socket tcpClientSocket) {
		this.tcpClientSocket = tcpClientSocket;
	}

	public InetAddress getServerIP() {
		return serverIP;
	}

	public void setServerIP(InetAddress serverIP) {
		this.serverIP = serverIP;
	}

	public OutputStream getOutToServer() {
		return outToServer;
	}

	public void setOutToServer(OutputStream outToServer) {
		this.outToServer = outToServer;
	}

	public InputStream getInFromServer() {
		return inFromServer;
	}

	public void setInFromServer(InputStream inFromServer) {
		this.inFromServer = inFromServer;
	}

	public DataInputStream getDataInFromServer() {
		return dataInFromServer;
	}

	public void setDataInFromServer(DataInputStream dataInFromServer) {
		this.dataInFromServer = dataInFromServer;
	}
	
}
