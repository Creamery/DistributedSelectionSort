package com.network.protocols;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;

import com.controller.ControllerManager;
import com.main.Info;
import com.message.MainMessage;
import com.message.TCPMessage;

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
	private OutputStream outToServer;
	private DataOutputStream dataOutToServer;
	private InputStream inFromServer;
	private DataInputStream dataInFromServer;

	private Socket socket;
	private volatile MainMessage mainMessage;
	
	public DataOutputStream getDataOutToServer() {
		return dataOutToServer;
	}

	public void setDataOutToServer(DataOutputStream dataOutToServer) {
		this.dataOutToServer = dataOutToServer;
	}

	// SERVER 
	public TCPTwoWay(String name, int port) throws IOException {
		this.setHostName(name);
		this.setTcpMessage(new TCPMessage());
		
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
			try {
				Socket server = this.getServerSocket().accept();
				
				System.out.println("Just connected to " + server.getRemoteSocketAddress());
				
				
				
				ObjectOutputStream oos = new ObjectOutputStream(server.getOutputStream());
				ObjectInputStream ois = new ObjectInputStream(server.getInputStream());
				MainMessage message;
				
				while (this.isReceiving()) {
					try {
						System.out.println("Waiting for message...");
						// WAIT for message
						message = (MainMessage) ois.readObject();
						if(message != null) {
							System.out.println("Received "+message.getMessage());
							message = new MainMessage();
							message.setMessage("server got it");
							
							// SEND message
							oos.writeObject(message);
							message = null;;
						}
					} catch (ClassNotFoundException e) {
						e.printStackTrace();
					}
				}
				
				
				/*
				BufferedReader keyRead = new BufferedReader(new InputStreamReader(System.in));
				
				// sending to client (pwrite object)
				OutputStream ostream = server.getOutputStream(); 
				PrintWriter pwrite = new PrintWriter(ostream, true);
				// receiving from server ( receiveRead  object)
				InputStream istream = server.getInputStream();
				BufferedReader receiveRead = new BufferedReader(new InputStreamReader(istream));

				String receiveMessage, sendMessage;               
				while(isReceiving()) {
					if((receiveMessage = receiveRead.readLine()) != null) {
						System.out.println(receiveMessage);         
					}         
					sendMessage = keyRead.readLine(); 
					pwrite.println(sendMessage);             
					pwrite.flush();
				}        
				*/
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
//			    oos.writeObject(message);
			    while (this.isSending()) {
			    	try {
			    		System.out.println("Waiting for message != null");
			    		this.setMainMessage(null);
			    		while(this.mainMessage == null) {
			    			this.mainMessage = ControllerManager.Instance().getClientMessage();
			    		};
			    		
			    		this.setMainMessage(new MainMessage());
			    		this.getMainMessage().setMessage("wan");
			    		// SEND message
		    			oos.writeObject(this.getMainMessage());
		    			this.setMainMessage(null);
			    		// WAIT message
			    		System.out.println("Waiting for message...");
						message = (MainMessage) ois.readObject();

				    	if(message != null) {
				    		System.out.println("Client received "+message.getMessage());
				    		this.setMainMessage(new MainMessage());
				    		this.getMainMessage().setMessage("yowz?");
				    		oos.writeObject(this.getMainMessage());
				    		
				    		message = null;
				    	}
				    	
					} catch (ClassNotFoundException e) {
						e.printStackTrace();
					}
			    }
			    
				/*
				BufferedReader keyRead = new BufferedReader(new InputStreamReader(System.in));
	            // sending to client (pwrite object)
				OutputStream ostream = socket.getOutputStream(); 
				PrintWriter pwrite = new PrintWriter(ostream, true);
	
	            // receiving from server ( receiveRead  object)
				InputStream istream = socket.getInputStream();
				BufferedReader receiveRead = new BufferedReader(new InputStreamReader(istream));
	
				System.out.println("Start the chitchat, type and press Enter key");
	
				String receiveMessage, sendMessage;               
				while(isSending()) {
					sendMessage = keyRead.readLine();  // keyboard reading
					pwrite.println(sendMessage);       // sending to server
					pwrite.flush();                    // flush the data
					if((receiveMessage = receiveRead.readLine()) != null) //receive from server
					{
					System.out.println(receiveMessage); // displaying at DOS prompt
					}         
				}
				*/     
			} catch (IOException e) {
				System.out.println("Exception object streams");
				e.printStackTrace();
			}
		}

		// Wait for message to receive
//		while(isReceiving()) {
//			try {

//				System.out.println("isReceiving");

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
				/*
				try {
					// Receive a TCPMessage object from input stream
					TCPMessage receivedMessage = (TCPMessage) this.getObjectInputStream().readObject();
					System.out.println(this.getHostName()+" received "+receivedMessage.getMessage());
				} catch (ClassNotFoundException e) {
					e.printStackTrace();
				}
				*/
				// SENDING a message
//				this.setObjectOutputStream(new ObjectOutputStream(this.getTcpServerSocket().getOutputStream()));
				
//				this.getTcpMessage().setMessage("Message to send");
//				this.getObjectOutputStream().writeObject(this.getTcpMessage());
				
//				sentMessage.setMessage("[SERVER]: "+"Thank you for connecting to " + server.getLocalSocketAddress()
//					+ "\nGoodbye!");
				
			
//			} catch (SocketTimeoutException s) {
//				 System.out.println("[SERVER]: "+"Socket timed out!");
//				 break;
//			 } catch (IOException e) {
//				 e.printStackTrace();
//				 break;
//			 }
//		}

	}
//	public void sendAsClient(String message) {
//		System.out.println("Sending as client");
//		try {
//			this.setTcpClientSocket(new Socket(this.getServerIP(), this.getPort()));
//			
////			PrintWriter toServer = new PrintWriter(this.getTcpClientSocket().getOutputStream(),true);
////			BufferedReader fromServer = new BufferedReader(new InputStreamReader(this.getTcpClientSocket().getInputStream()));
//		
//			DataInputStream fromServer = new DataInputStream(new BufferedInputStream(this.getTcpClientSocket().getInputStream()));;
//			
////			toServer.println("Hello from " + this.getTcpClientSocket().getLocalSocketAddress()); 
////			String line = fromServer.readLine();
////			System.out.println("Client received: " + line + " from Server");
//			
//			while(isSending()) {
//				String line = fromServer.readUTF();
//				System.out.println(line);
//				if(line.trim().equals("end")) {
//					System.out.println("Ending client");
//					this.setSending(false);
//				}
////				line = fromServer.readLine();
////				System.out.println("Client received: " + line + " from Server");	
//			}
//			
//			System.out.println("Client object streams END");
//			
//		} catch (IOException e) {
//			System.out.println("Exception object streams");
//			e.printStackTrace();
//		}
//	}
	public void send(int index, int value) {
		this.getTcpMessage().setMessage("Sent index: "+index+" "+value+"value");
		
		try {
			
			this.getObjectOutputStream().writeObject(this.getTcpMessage());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	// CLIENT
	
	/*
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
	
}
