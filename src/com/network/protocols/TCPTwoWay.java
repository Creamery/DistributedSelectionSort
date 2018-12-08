package com.network.protocols;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

import com.main.Info;
import com.message.MainMessage;
import com.message.TCPMessage;
import com.network.ClientProcessor;
import com.network.ProcessorConnector;
import com.network.ProcessorIndices;
import com.network.ServerProcessor;

public class TCPTwoWay extends Thread {
	private boolean isServer;
	
	private String hostName;
	
	private boolean isReceiving;
	private boolean isSending;
	private TCPMessage tcpMessage;
	private int port;
	
	// For SERVER
	private ServerSocket serverSocket;
	private Socket tcpServerSocket;
	private InetAddress serverIP;
//	private ObjectInputStream objectInputStream;
	
	private ArrayList<Socket> listClientSockets;
	private ArrayList<ObjectOutputStream> listClientOutputStreams;
	private ArrayList<ObjectInputStream> listClientInputStreams;
	
	// For CLIENT
	private InetAddress clientIP;
//	private ObjectOutputStream objectOutputStream;
	private Socket tcpClientSocket;

	private Socket socket;
	private volatile MainMessage mainMessage;
	private ProcessorConnector processor;
	

	public TCPTwoWay(String name, int port, ProcessorConnector processorConnector) throws IOException {
		this.setHostName(name);
		this.setTcpMessage(new TCPMessage());
		this.setProcessor(processorConnector);
		
		// Initialize server socket
		this.setServerSocket(new ServerSocket(port));
		this.getServerSocket().setSoTimeout(Info.SERVER_TIMEOUT);
		this.setPort(Info.PORT);
		
		
		// Set clientIP to self IP
		this.setClientIP(InetAddress.getLocalHost());
		
		this.setMainMessage(null);
		this.setListClientSockets(new ArrayList<Socket>());
		this.setListClientOutputStreams(new ArrayList<ObjectOutputStream>());
		this.setListClientInputStreams(new ArrayList<ObjectInputStream>());
	}

	
	// To be called after a successful accept.
//	public void initializeObjectStreams(ObjectOutputStream outputStream, ObjectInputStream inputStream) throws IOException {
//		// Prepare object I/O
//		this.setObjectInputStream(inputStream);
//		this.setObjectOutputStream(outputStream);
//
////		this.setObjectInputStream(new ObjectInputStream(this.getTcpServerSocket().getInputStream()));
////		this.setObjectOutputStream(new ObjectOutputStream(this.getTcpServerSocket().getOutputStream()));
//	}
	
	public void startAsServer() {
		this.setServer(true);
		this.setReceiving(true);
		this.run();
	}
	public void startAsClient() {
		this.setServer(false);
		this.setSending(true);
		this.run();
	}
	public void initializeClientSocket(InetAddress serverIP) {
		this.setServerIP(serverIP);
		this.setPort(Info.PORT);
	}


	// Run Listener
	public void run() {
		System.out.println("TCP listener run");
		
		if(isServer()) {
			System.out.println("As Server");
			ServerProcessor serverProcessor = (ServerProcessor) this.getProcessor();
			ArrayList<ProcessorIndices> indices = null;
			ProcessorIndices processorIndex = null;
			int minValue = 99999999; // TODO: make a better sentinel
			int minIndex = -1;
			
			try {
				
				Socket server = null;
				ObjectOutputStream oos = null;
				ObjectInputStream ois = null;
				MainMessage message = null;
				
				// ACCEPT CLIENTS
				for(int i = 0; i < Info.CLIENT_SIZE; i++) {
					server = this.getServerSocket().accept();
					this.getListClientSockets().add(server);
					
					System.out.println("Just connected to " + server.getRemoteSocketAddress());

					oos = new ObjectOutputStream(server.getOutputStream());
					ois = new ObjectInputStream(server.getInputStream());
					
					this.getListClientOutputStreams().add(oos);
					this.getListClientInputStreams().add(ois);
				}
				
				
				// SEND ARRAY to all clients
				message = new MainMessage();
				message.setMessage(Info.MSG_SERVER_ARRAY);	
				message.setSortList(this.getProcessor().getSortList());
				sendToClients(message);
				message.reset();
				// oos.writeObject(message);
				
				/*
				System.out.println("Waiting for reply...");
				// WAIT for message
				try {
					do {
						message = (MainMessage) ois.readObject();
					}
					while(!message.getMessage().contains(Info.MSG_CLIENT_RECEIVED));
				} catch (ClassNotFoundException e1) {
					e1.printStackTrace();
				}
				*/
				
				
				// START PROCESSING
				while (this.isReceiving()) {
					try {
						// SEND indices
						message.reset();
						indices = serverProcessor.computeIndices(Info.CLIENT_SIZE);
						
						// For each CLIENT
						for(int i = 0; i < indices.size(); i++) {
							processorIndex = indices.get(i);
							message.reset();
							message.setIndices(processorIndex.getStartIndex(), processorIndex.getEndIndex());
							this.getListClientOutputStreams().get(i).writeObject(message);
							//oos.writeObject(message);
						}
						
						System.out.println("Waiting for Minimum Value...");
						// WAIT for message (each CLIENT)
						for(int i = 0; i < Info.CLIENT_SIZE; i++) {
							message = (MainMessage) this.getListClientInputStreams().get(i).readObject();
							System.out.println("Client "+i+"responded");
							//message = (MainMessage) ois.readObject();
							
							if(message.getMinValue() < minValue) {
								minValue = message.getMinValue();
								minIndex = message.getMinIndex();
							}
						}
						
						// SWAP
						System.out.println("minIndex "+minIndex);
						serverProcessor.swap(serverProcessor.getCurrentIndex(), minIndex);
						serverProcessor.next(); // Move current index
						
						if(serverProcessor.isDone()) {
							this.setReceiving(false);
						}
					} catch (ClassNotFoundException e) {
						e.printStackTrace();
					}
				}
				
				message.reset();
				message.setMessage(Info.MSG_CLIENT_END);
				this.sendToClients(message);
				
				System.out.println("Server done");
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		else {
			System.out.println("As Client");
			try {
				ClientProcessor clientProcessor = (ClientProcessor) this.getProcessor();
				
				socket = new Socket(this.getServerIP(), this.getPort());
				
				ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());
			    ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
			    MainMessage message = null;
			    
			    try {
				    // WAIT message
					do {
			    		System.out.println("Waiting for array...");
						message = (MainMessage) ois.readObject();
						if(message.getMessage().contains(Info.MSG_SERVER_ARRAY)) {
				    		clientProcessor.setSortList(message.getSortList());
				    		System.out.println("Client: Received ARRAY");
				    		
//							this.setMainMessage(new MainMessage());
//				    		this.getMainMessage().setMessage(Info.MSG_CLIENT_RECEIVED);
//				    		oos.writeObject(this.getMainMessage());
						}
					}while(!message.getMessage().contains(Info.MSG_SERVER_ARRAY));
					message.reset();
			    } catch (ClassNotFoundException e) {
					e.printStackTrace();
				}
			    
			    while (this.isSending()) {
			    	try {
			    		// WAIT for indices
			    		System.out.println("Waiting for indices (message != null)");
						message = (MainMessage) ois.readObject();
						
						// END
						if(message.getMessage().contains(Info.MSG_CLIENT_END)) {
							this.setSending(false);
						}
						
						// PROCESS
						else {
							this.getProcessor().setIndices(message);
							System.out.println("Received indices "+this.getProcessor().getStartIndex()+" "+this.getProcessor().getEndIndex());
							this.getProcessor().process();
							// Block while is processing
							while(this.getProcessor().getMinimumIndex() == -1) {};
							
							
				    		message.reset();
				    		message.setMinimumValues(this.getProcessor().getMinimumIndex(), this.getProcessor().getMinimumValue());
				    		
				    		// SEND message
				    		System.out.println("Sent min value");
			    			oos.writeObject(message);
			    			message.reset();
				    		
						}
					} catch (ClassNotFoundException e) {
						e.printStackTrace();
					}
			    }
			    System.out.println("Client done");
			} catch (IOException e) {
				System.out.println("Exception object streams");
				e.printStackTrace();
			}
		}
	}

	// Sends message to ALL clients
	public void sendToClients(MainMessage message) {
		for(int i = 0; i < getListClientOutputStreams().size(); i++) {
			try {
				this.getListClientOutputStreams().get(i).writeObject(message);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public boolean isReceiving() {
		return isReceiving;
	}

	public void setReceiving(boolean isReceiving) {
		this.isReceiving = isReceiving;
	}

	public boolean isSending() {
		return isSending;
	}

	public void setSending(boolean isSending) {
		this.isSending = isSending;
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

	public String getHostName() {
		return hostName;
	}

	public void setHostName(String hostName) {
		this.hostName = hostName;
	}

	public boolean isServer() {
		return isServer;
	}

	public void setServer(boolean isServer) {
		this.isServer = isServer;
	}

	public Socket getSocket() {
		return socket;
	}

	public void setSocket(Socket socket) {
		this.socket = socket;
	}

	public MainMessage getMainMessage() {
		return mainMessage;
	}

	public ArrayList<Socket> getListClientSockets() {
		return listClientSockets;
	}

	public void setListClientSockets(ArrayList<Socket> listClientSockets) {
		this.listClientSockets = listClientSockets;
	}

	public void setMainMessage(MainMessage mainMessage) {
		this.mainMessage = mainMessage;
	}

	public ProcessorConnector getProcessor() {
		return processor;
	}

	public void setProcessor(ProcessorConnector processor) {
		this.processor = processor;
	}

	public ArrayList<ObjectOutputStream> getListClientOutputStreams() {
		return listClientOutputStreams;
	}

	public void setListClientOutputStreams(ArrayList<ObjectOutputStream> listClientOutputStreams) {
		this.listClientOutputStreams = listClientOutputStreams;
	}


	public ArrayList<ObjectInputStream> getListClientInputStreams() {
		return listClientInputStreams;
	}


	public void setListClientInputStreams(ArrayList<ObjectInputStream> listClientInputStreams) {
		this.listClientInputStreams = listClientInputStreams;
	}
	
}
