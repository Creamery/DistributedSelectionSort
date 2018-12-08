package com.network;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.ArrayList;

import com.main.Info;
import com.main.Print;
import com.network.protocols.TCPTwoWay;
import com.network.protocols.UDPListener;
import com.network.protocols.UDPUnpacker;

public class MainServer extends Thread implements UDPUnpacker {
	private TCPTwoWay tcpStream;
	private UDPListener udpListener;
	private ServerProcessor processor;
	
//	private ServerSocket serverSocket;
	private InetAddress address;
	
	private DatagramSocket udpSocket;
	private ObjectOutputStream objectOutputStream;
	

	private ArrayList<InetAddress> listClients;
	private int UDPPort;
	private int TCPPort;
	
	public MainServer() throws IOException {
		this.setUDPPort(Info.BROADCAST_PORT);
		this.setTCPPort(Info.PORT);
		try {
			this.setAddress(InetAddress.getByName(Info.NETWORK.split("/")[1]));
			this.setTcpStream(new TCPTwoWay("Server", this.getTCPPort()));
		} catch (IOException e) {
			e.printStackTrace();
		}
		this.setUdpListener(new UDPListener(this));
//		serverSocket = new ServerSocket(this.getTCPPort());
		// Set how long the server will wait for a connection
//		serverSocket.setSoTimeout(0);
	}
	
	
	// Initialize processor then run it
	public void process() {
		if(this.getProcessor() == null) {
			this.setProcessor(new ServerProcessor());
		}
		this.getProcessor().process();
	}
	
	public void listen() {
		this.getUdpListener().listen();
	}
	public void stopListening() {
		this.getUdpListener().stopListening();
	}
	
	// Announce the server IP so that listening clients can connect
	public void broadcast() {
		this.stopListening();
		
		// Initialize sockets
		try {
			this.getUdpSocket().setBroadcast(true);
		} catch (SocketException e) {
			e.printStackTrace();
		}

		// Get value server IP  (this device)
		byte[] buffer = Info.NETWORK.getBytes();
		DatagramPacket packet = null;
		
		// Prepare the broadcast
		try {
			packet = new DatagramPacket(buffer, buffer.length, InetAddress.getByName("255.255.255.255"), Info.BROADCAST_PORT);
		} catch (UnknownHostException e1) {
			e1.printStackTrace();
		}
		try {
			System.out.println("Sending packet");
			// Send IP
			this.getUdpSocket().send(packet);
			// System.out.println("Sending packet to port "+this.getUdpSocket().getPort());
			Print.serverBroadcast();
		} catch(IOException e){
			e.printStackTrace();
		}
		
		// Close the socket NOTE: Same socket used by udpListener
		// this.getUdpSocket().close();


//		this.getTcpStream().start();
		// Prepare to listen to replies
		this.listen();


		// Start TCP Connection
		// this.startTCPConnection();
		 
	}
	
	public InetAddress getAddress() {
		return address;
	}

	public void setAddress(InetAddress address) {
		this.address = address;
	}
	
	@Override
	public DatagramSocket getUdpSocket() {
		if(this.udpSocket == null) {
			try {
				this.setUdpSocket(new DatagramSocket(Info.BROADCAST_PORT));
			} catch (SocketException e) {
				e.printStackTrace();
			}
		}
		return udpSocket;
	}
	
	public void setUdpSocket(DatagramSocket udpSocket) {
		this.udpSocket = udpSocket;
	}

	public ServerProcessor getProcessor() {
		return processor;
	}

	public void setProcessor(ServerProcessor processor) {
		this.processor = processor;
	}

	public void run() {
		
		
	}

	public ArrayList<InetAddress> getListClients() {
		if(this.listClients == null) {
			this.listClients = new ArrayList<InetAddress>();
		}
		return listClients;
	}

	public void setListClients(ArrayList<InetAddress> listClients) {
		this.listClients = listClients;
	}

	public ObjectOutputStream getObjectOutputStream() {
		return objectOutputStream;
	}

	public void setObjectOutputStream(ObjectOutputStream objectOutputStream) {
		this.objectOutputStream = objectOutputStream;
	}

	public TCPTwoWay getTcpStream() {
		return tcpStream;
	}

	public void setTcpStream(TCPTwoWay tcpStream) {
		this.tcpStream = tcpStream;
	}

	public UDPListener getUdpListener() {
		return udpListener;
	}

	public void setUdpListener(UDPListener udpListener) {
		this.udpListener = udpListener;
	}


	@Override
	public void unpack(String message) {
		try {
			String ip = message.substring(message.indexOf("/")+1);
			System.out.println("Unpacked message: "+message+"\nTrimmed: "+ip);
	
			InetAddress address = InetAddress.getByName(ip);
			System.out.println("this address is "+this.getAddress());
			if(this.getAddress().toString().substring(1).equals(ip)) {
				System.out.println("Received own address");
			}
			else {
				this.stopListening();
				this.getListClients().add(address);
				System.out.println("Added client "+ip);
			}
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}
		// Start TCP Connection
		// this.startTCPConnection();
	}

	public void startTCPConnection() {
		// TCP Start
		 this.getTcpStream().startAsServer();
	}

	public int getUDPPort() {
		return UDPPort;
	}


	public void setUDPPort(int uDPPort) {
		UDPPort = uDPPort;
	}


	public int getTCPPort() {
		return TCPPort;
	}


	public void setTCPPort(int tCPPort) {
		TCPPort = tCPPort;
	}

	/*
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
	*/
}