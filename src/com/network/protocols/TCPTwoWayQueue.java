package com.network.protocols;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

import com.main.Info;
import com.main.Print;
import com.message.MainMessage;
import com.message.TCPMessage;
import com.network.ClientProcessor;
import com.network.ProcessorConnector;
import com.network.ProcessorIndices;
import com.network.ServerProcessor;
import com.reusables.CsvWriter;
import com.reusables.Stopwatch;

public class TCPTwoWayQueue extends Thread {
	private boolean isServer;
	
	private String hostName;
	
	private boolean isReceiving;
	private boolean isSending;
	private TCPMessage tcpMessage;
	private int port;
	private volatile int minValue = 99999999; // TODO: make a better sentinel
	private volatile int minIndex = -1;
	
	private volatile MainMessage message;
	private volatile MainMessage packetRequest;
	private volatile MainMessage packetInstruction;
	
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
	

	public TCPTwoWayQueue(String name, int port, ProcessorConnector processorConnector) throws IOException {
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


	public MainMessage requestPacket() {
		MainMessage request = new MainMessage();
		request.setHeader(Info.HDR_REQUEST);
		return request;
	}
	
	public void processInstructionPacket(MainMessage instructionPacket) {
		
	}
	
	public void processRequestPacket(int client, MainMessage requestPacket) {
		try {

			// Process the requestPacket
			
			// Then send back an instruction packet
			MainMessage instructionPacket = new MainMessage();
			this.getListClientOutputStreams().get(client).writeObject(instructionPacket);
			
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	// Run Listener
	public void run() {
		if(isServer()) {

			Print.message("Starting as SERVER");
			ServerProcessor serverProcessor = (ServerProcessor) this.getProcessor();
			// ArrayList<ProcessorIndices> indices = null;
			
			
			try {
				
				Socket server = null;
				ObjectOutputStream oos = null;
				ObjectInputStream ois = null;
				
				// ACCEPT CLIENTS
				for(int i = 0; i < Info.CLIENT_SIZE; i++) {
					server = this.getServerSocket().accept();
					this.getListClientSockets().add(server);
					
					Print.response("Just connected to " + server.getRemoteSocketAddress());

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
				
				// Receiving is for server
				this.setReceiving(true);
				while(this.isReceiving()) {
					try {
						// Iteratively check each client if an object is sent (blocks until client sends)
						for(int i = 0; i < this.getListClientSockets().size(); i++) {
								this.packetRequest = (MainMessage) this.getListClientInputStreams().get(i).readObject();
								System.out.println("Received a packet from Client "+i);
								this.processRequestPacket(i, packetRequest);
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
			Print.message("Starting as CLIENT");
			
			try {
				
				ClientProcessor clientProcessor = (ClientProcessor) this.getProcessor();
				socket = new Socket(this.getServerIP(), this.getPort());
//				ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());
//			    ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
				ObjectInputStream fromServer = new ObjectInputStream(socket.getInputStream());
			    ObjectOutputStream toServer = new ObjectOutputStream(socket.getOutputStream());
			    message = null;
			    
			    
			    // WAIT for array
			    try {
					do {
						message = (MainMessage) fromServer.readObject();
						if(message.getMessage().contains(Info.MSG_SERVER_ARRAY)) {
				    		clientProcessor.setSortList(message.getSortList());
						}
					}while(!message.getMessage().contains(Info.MSG_SERVER_ARRAY));
					message.reset();
			    } catch (ClassNotFoundException e) {
					e.printStackTrace();
				}
			    
			    // BEGIN LOOP
			    while (this.isSending()) {
			    	try {
			    		
			    		
			    		
			    		// SEND a requestPacket
			    		toServer.writeObject(requestPacket());
			    		System.out.println("Request sent to server");
			    		
			    		
			    		// WAIT for server reply
			    		this.packetInstruction = (MainMessage) fromServer.readObject();
			    		System.out.println("Received a packet from Server ");
			    		
			    		// PROCESS the instruction
			    		this.processInstructionPacket(packetInstruction);
			    		
			    		
			    		
			    		
			    		
			    		
					} catch (ClassNotFoundException e) {
						e.printStackTrace();
					}
			    }
			    System.out.println("Client done");
			} catch (IOException e) {
				// System.out.println("Exception object streams");
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

	public MainMessage getPacketRequest() {
		return packetRequest;
	}


	public void setPacketRequest(MainMessage packetRequest) {
		this.packetRequest = packetRequest;
	}


	public MainMessage getPacketInstruction() {
		return packetInstruction;
	}


	public void setPacketInstruction(MainMessage packetInstruction) {
		this.packetInstruction = packetInstruction;
	}
	
}
