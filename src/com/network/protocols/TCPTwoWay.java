package com.network.protocols;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

import com.controller.ControllerManager;
import com.main.Info;
import com.message.MainMessage;
import com.message.TCPMessage;
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
	private ObjectInputStream objectInputStream;
	
	// For CLIENT
	private InetAddress clientIP;
	private ObjectOutputStream objectOutputStream;
	private Socket tcpClientSocket;

	private Socket socket;
	private volatile MainMessage mainMessage;
	private ProcessorConnector processor;
	

	// SERVER 
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
	}
	
//	public void start() {
//		this.setReceiving(true);
//		this.setSending(true);
//		this.run();
//	}


	
	// To be called after a successful accept.
	public void initializeObjectStreams(ObjectOutputStream outputStream, ObjectInputStream inputStream) throws IOException {
		// Prepare object I/O
		this.setObjectInputStream(inputStream);
		this.setObjectOutputStream(outputStream);

//		this.setObjectInputStream(new ObjectInputStream(this.getTcpServerSocket().getInputStream()));
//		this.setObjectOutputStream(new ObjectOutputStream(this.getTcpServerSocket().getOutputStream()));
	}
	
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
		
			/*
			System.out.println("Socket sent");
			this.setTcpClientSocket(new Socket(this.getServerIP(), this.getPort()));
			
			System.out.println("[CLIENT]: "+"Just connected to " + this.getTcpClientSocket().getRemoteSocketAddress());
			

			System.out.println("Client object streams 0");
			this.setOutToServer(this.getTcpClientSocket().getOutputStream());
			this.setDataOutToServer(new DataOutputStream(outToServer));
			this.setInFromServer(this.getTcpClientSocket().getInputStream());
			this.setDataInFromServer(new DataInputStream(inFromServer));


			System.out.println("Client object streams 1");
			*/
//	        OutputStream outToServer = client.getOutputStream();
//	        ObjectOutputStream out = new ObjectOutputStream(outToServer);
////	        out.writeUTF("[CLIENT]: "+"Hello from " + client.getLocalSocketAddress());
//	        MainMessage sentMessage = new MainMessage();
//	        sentMessage.setMessage("[CLIENT]: "+"Hello from " + client.getLocalSocketAddress());
//	        out.writeObject(sentMessage);
//	        InputStream inFromServer = client.getInputStream();
//	        ObjectInputStream in = new ObjectInputStream(inFromServer);
	        

//			System.out.println("Client object streams 2");
//	        this.initializeObjectStreams(
//	        		new ObjectOutputStream(this.getTcpClientSocket().getOutputStream()),
//	        		new ObjectInputStream(this.getTcpClientSocket().getInputStream()));

	        
			
	}

//	public void sendAsClient(String message) {
//		this.setMainMessage(new MainMessage());
//		this.getMainMessage().setMessage(message);
//	}
	
	// Run listener
	public void run() {
		System.out.println("TCP listener run");
		if(isServer()) {
			System.out.println("As Server");
			ServerProcessor serverProcessor = (ServerProcessor) this.getProcessor();
			ArrayList<ProcessorIndices> indices = null;
			ProcessorIndices processorIndex = null;
			int minValue = 99999999;
			int minIndex = -1;
			
			try {
				Socket server = this.getServerSocket().accept();
				
				System.out.println("Just connected to " + server.getRemoteSocketAddress());
				
				
				
				ObjectOutputStream oos = new ObjectOutputStream(server.getOutputStream());
				ObjectInputStream ois = new ObjectInputStream(server.getInputStream());
				MainMessage message;
				
				// Initial send for array
				message = new MainMessage();
				message.setMessage(Info.MSG_SERVER_ARRAY);	
				message.setSortList(this.getProcessor().getSortList());
				
				// SEND message
				oos.writeObject(message);
				
				System.out.println("Waiting for reply...");
				message.reset();
				// WAIT for message
				try {
					do {
						message = (MainMessage) ois.readObject();
					}
					while(!message.getMessage().contains(Info.MSG_CLIENT_RECEIVED));
				} catch (ClassNotFoundException e1) {
					e1.printStackTrace();
				}
				
				
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
							oos.writeObject(message);
						}
						
						System.out.println("Waiting for Minimum Value...");
						// WAIT for message (each CLIENT)
						for(int i = 0; i < Info.CLIENT_SIZE; i++) {
							message = (MainMessage) ois.readObject();
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
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		else {
			System.out.println("As Client");
			try {

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
				    		System.out.println("GOT ARRAY");
							this.setMainMessage(new MainMessage());
				    		this.getMainMessage().setMessage(Info.MSG_CLIENT_RECEIVED);
				    		oos.writeObject(this.getMainMessage());
						}
					}while(!message.getMessage().contains(Info.MSG_SERVER_ARRAY));
					
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
			} catch (IOException e) {
				System.out.println("Exception object streams");
				e.printStackTrace();
			}
		}
	}

	public void send(int index, int value) {
		this.getTcpMessage().setMessage("Sent index: "+index+" "+value+"value");
		
		try {
			
			this.getObjectOutputStream().writeObject(this.getTcpMessage());
		} catch (IOException e) {
			e.printStackTrace();
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

	public void setMainMessage(MainMessage mainMessage) {
		this.mainMessage = mainMessage;
	}

	public ProcessorConnector getProcessor() {
		return processor;
	}

	public void setProcessor(ProcessorConnector processor) {
		this.processor = processor;
	}
	
}
