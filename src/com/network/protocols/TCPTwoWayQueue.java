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
import com.message.PacketType;
import com.message.TCPMessage;
import com.network.ClientProcessor;
import com.network.ProcessorConnector;
import com.network.ServerProcessor;

public class TCPTwoWayQueue extends Thread {
	private boolean isServer;
	
	private String hostName;
	private boolean isReceiving;
	private boolean isSending;
	private TCPMessage tcpMessage;
	private int port;
	
	
	private volatile MainMessage packetRequest;
	private volatile MainMessage packetInstruction;
	
	// For SERVER
	private ServerSocket serverSocket;
	private Socket tcpServerSocket;
	private InetAddress serverIP;
	private volatile MainMessage packetServer;
	private volatile ServerProcessor serverProcessor;
	
	private volatile Socket serverStreamSocket;
	private volatile ObjectOutputStream toClient = null;
	private volatile ObjectInputStream fromClient = null;
	
	private ArrayList<Socket> listClientSockets;
	private ArrayList<ObjectOutputStream> listClientOutputStreams;
	private ArrayList<ObjectInputStream> listClientInputStreams;
	private volatile boolean serverProcessing;
	
	public boolean isServerProcessing() {
		return serverProcessing;
	}


	public void setServerProcessing(boolean serverProcessing) {
		this.serverProcessing = serverProcessing;
	}


	public boolean isClientProcessing() {
		return clientProcessing;
	}


	public void setClientProcessing(boolean clientProcessing) {
		this.clientProcessing = clientProcessing;
	}


	// For CLIENT
	private InetAddress clientIP;
	private Socket tcpClientSocket;
	private volatile MainMessage packetClient;
	private volatile ClientProcessor clientProcessor;
	
	private volatile Socket clientStreamSocket;
	private volatile ObjectOutputStream toServer = null;
	private volatile ObjectInputStream fromServer = null;
	private volatile boolean clientProcessing;
	
	private ProcessorConnector processor;
	

	public TCPTwoWayQueue(String name, int port, ProcessorConnector processorConnector) throws IOException {
		this.setHostName(name);
		this.setTcpMessage(new TCPMessage());
		this.setProcessor(processorConnector);
		
		
		this.setServerSocket(new ServerSocket(port)); // Initialize server socket
		this.getServerSocket().setSoTimeout(Info.SERVER_TIMEOUT);
		this.setPort(Info.PORT);
		
		this.setClientIP(InetAddress.getLocalHost()); // Set clientIP to self IP
		
		this.setListClientSockets(new ArrayList<Socket>());
		this.setListClientOutputStreams(new ArrayList<ObjectOutputStream>());
		this.setListClientInputStreams(new ArrayList<ObjectInputStream>());
	}


	// PACKET FACTORY --------------------------
	public MainMessage sortlistPacket(ArrayList<Integer> list) {
		MainMessage packet = new MainMessage();
		packet.setPacketHeader(PacketType.HDR_SORTLIST);
		packet.setSortList(list);
		return packet;
	}
	
	public MainMessage requestPacket() {
		MainMessage request = new MainMessage();
		request.setPacketHeader(PacketType.HDR_REQUEST);
		return request;
	}
	
	public MainMessage swapPacket() {
		MainMessage instruction = new MainMessage();
		instruction.setPacketHeader(PacketType.HDR_SWAP);
		return instruction;
	}
	
	public MainMessage processPacket(int start, int end) {
		MainMessage instruction = new MainMessage();
		
		instruction.setPacketHeader(PacketType.HDR_PROCESS);
		instruction.setStartIndex(start);
		instruction.setEndIndex(end);
		
		return instruction;
	}
	
	public MainMessage endPacket() {
		MainMessage instruction = new MainMessage();
		instruction.setPacketHeader(PacketType.HDR_END);
		return instruction;
	}
	//------------------------------------------
	
	
	
	
	// PACKET PROCESSORS -----------------------
	public void processInstructionPacket(MainMessage instructionPacket) { // FOR CLIENT
		this.setClientProcessing(true);
		PacketType type = instructionPacket.getPacketHeader();
		
		switch(type) {
			case HDR_SORTLIST:
				this.clientProcessor.setSortList(
						instructionPacket.getSortList());
				break;
				
			case HDR_PROCESS:
				this.clientProcessor.process(
						instructionPacket.getStartIndex(),
						instructionPacket.getEndIndex());
				break;
				
			case HDR_SWAP:
				this.clientProcessor.swap(
						instructionPacket.getSwapIndex1(),
						instructionPacket.getSwapIndex2());
				break;
				
			case HDR_END:
				this.setSending(false); // Breaks the client function loop
				break;
				
			default:
				break;
		}

		this.setClientProcessing(false); // TODO: Called by external scripts
	}
	
	public void processRequestPacket(int client, MainMessage requestPacket) { // FOR SERVER
		try {
			this.setServerProcessing(true);
			PacketType type = requestPacket.getPacketHeader();
			
			// Process the requestPacket
			switch(type) {
				case HDR_REQUEST:
					// Retrieve from message queue
					break;
				default:
					break;
			}
			
			// Then send back an instruction packet
			MainMessage instructionPacket = new MainMessage();
			this.getListClientOutputStreams().get(client).writeObject(instructionPacket);
			

			this.setServerProcessing(false); // TODO: Called by external scripts
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	//------------------------------------------
	
	
	
	
	
	
	// SERVER-CLIENT FUNCTIONS -----------------
	public void run() {
		

		// SERVER FUNCTION -------------------------
		if(isServer()) {
			try {
				Print.message("Starting as SERVER");
				this.serverProcessor = (ServerProcessor) this.getProcessor();

				// CONNECT to all clients
				for(int i = 0; i < Info.CLIENT_SIZE; i++) { 
					serverStreamSocket = this.getServerSocket().accept(); // Accept client connection
					this.getListClientSockets().add(serverStreamSocket);
					
					Print.response("Just connected to " + serverStreamSocket.getRemoteSocketAddress());

					toClient = new ObjectOutputStream(serverStreamSocket.getOutputStream());
					fromClient = new ObjectInputStream(serverStreamSocket.getInputStream());
					
					this.getListClientOutputStreams().add(toClient); // Then keep a reference to client I/O streams
					this.getListClientInputStreams().add(fromClient);
				}
				
				
				// SEND sortlist to all clients
				sendToClients(sortlistPacket(serverProcessor.getSortList()));
				
				
				// LISTEN to packet requests
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
		//------------------------------------------
		
		
		
		
		
		// CLIENT FUNCTION -------------------------
		else {
			try {
				Print.message("Starting as CLIENT");
				this.clientProcessor = (ClientProcessor) this.getProcessor();
				
				this.clientStreamSocket = new Socket(this.getServerIP(), this.getPort());
				ObjectInputStream fromServer = new ObjectInputStream(clientStreamSocket.getInputStream());
			    ObjectOutputStream toServer = new ObjectOutputStream(clientStreamSocket.getOutputStream());

			    // WAIT for sort list
			    try {
					this.packetClient = (MainMessage) fromServer.readObject();
					this.processInstructionPacket(this.packetClient);

			    } catch (ClassNotFoundException e) {
					e.printStackTrace();
				}
			    
			    
			    
			    // BEGIN LOOP
			    while (this.isSending()) {
			    	try {
			    		
			    		// SEND a requestPacket for the next instruction
			    		toServer.writeObject(requestPacket());
			    		System.out.println("Request sent to server");
			    		
			    		
			    		// WAIT for server reply (blocks)
			    		this.packetInstruction = (MainMessage) fromServer.readObject();
			    		System.out.println("Received a packet from Server ");
			    		
			    		
			    		// PROCESS the instruction
			    		this.processInstructionPacket(packetInstruction);
			    		
			    		
			    		// WAIT while client is processing
			    		while(isClientProcessing()) {};
			    		
			    		
					} catch (ClassNotFoundException e) {
						e.printStackTrace();
					}
			    }
			    System.out.println("Client done");
			    
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		//------------------------------------------
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
	public ArrayList<Socket> getListClientSockets() {
		return listClientSockets;
	}

	public void setListClientSockets(ArrayList<Socket> listClientSockets) {
		this.listClientSockets = listClientSockets;
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


	public MainMessage getPacketServer() {
		return packetServer;
	}


	public void setPacketServer(MainMessage packetServer) {
		this.packetServer = packetServer;
	}


	public MainMessage getPacketClient() {
		return packetClient;
	}


	public void setPacketClient(MainMessage packetClient) {
		this.packetClient = packetClient;
	}


	public ClientProcessor getClientProcessor() {
		return clientProcessor;
	}


	public void setClientProcessor(ClientProcessor clientProcessor) {
		this.clientProcessor = clientProcessor;
	}


	public ServerProcessor getServerProcessor() {
		return serverProcessor;
	}


	public void setServerProcessor(ServerProcessor serverProcessor) {
		this.serverProcessor = serverProcessor;
	}


	public Socket getServerStreamSocket() {
		return serverStreamSocket;
	}


	public void setServerStreamSocket(Socket serverStreamSocket) {
		this.serverStreamSocket = serverStreamSocket;
	}


	public Socket getClientStreamSocket() {
		return clientStreamSocket;
	}


	public void setClientStreamSocket(Socket clientStreamSocket) {
		this.clientStreamSocket = clientStreamSocket;
	}


	public ObjectOutputStream getToClient() {
		return toClient;
	}


	public void setToClient(ObjectOutputStream toClient) {
		this.toClient = toClient;
	}


	public ObjectInputStream getFromClient() {
		return fromClient;
	}


	public void setFromClient(ObjectInputStream fromClient) {
		this.fromClient = fromClient;
	}


	public ObjectOutputStream getToServer() {
		return toServer;
	}


	public void setToServer(ObjectOutputStream toServer) {
		this.toServer = toServer;
	}


	public ObjectInputStream getFromServer() {
		return fromServer;
	}


	public void setFromServer(ObjectInputStream fromServer) {
		this.fromServer = fromServer;
	}
	
}
