package com.network;

import java.io.IOException;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;

import com.main.Info;
import com.network.protocols.TCPTwoWay;
import com.network.protocols.UDPListener;
import com.network.protocols.UDPUnpacker;

public class MainClient extends Thread implements UDPUnpacker {
	private TCPTwoWay tcpStream;
	private UDPListener udpListener;
	private ClientProcessor processor;
	
	private InetAddress address;
    private InetAddress serverIP;
    
	private DatagramSocket udpSocket;

	
	public MainClient() {
		try {
			this.setTcpStream(new TCPTwoWay("Client "));
		} catch (IOException e) {
			e.printStackTrace();
		}
		this.setUdpListener(new UDPListener(this));
	}
	
	// Initialize processor then run it
	public void process() {
		if(this.getProcessor() == null) {
			this.setProcessor(new ClientProcessor());
		}
		this.getProcessor().process();
	}
	
	// Send a disconnect message to the server
	public void disconnect() {
		
	}
	
	public void send(int index, int value) {
		this.getTcpStream().send(index, value);
	}
	
	// Listen for any server broadcast to connect
	public void listen() {
		this.getUdpListener().listen();
	}
	
	// Listen to a server broadcast
	public void run() {

	}


	@Override
	public void unpack(String message) {
		System.out.println("Unpacked message: "+message);
		try {
			this.setServerIP(InetAddress.getByName(message));
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}
        this.setupTCPStream();
	}
	
	// Set the server and client addresses of the TCP connection
	public void setupTCPStream() {
		this.getTcpStream().setServerIP(this.getServerIP());
		this.getTcpStream().setClientIP(this.getAddress());
	}
	
	// Start sorting
	/*
	public void start(String host, String address, String port) throws UnknownHostException {
		this.setHost(host);
		this.setPort(Integer.parseInt(port));
		this.setAddress(InetAddress.getByName(address));
		
		this.run();
	}
	*/
	/*
	public void run() {
		System.out.println("[CLIENT]: "+"Running MainClient...");
		
		String serverName = this.getHost();
		InetAddress ip = this.getAddress();
		int port = this.getPort();
		
		try {
			System.out.println("[CLIENT]: "+"Connecting to " + serverName + " on port " + port);
			Socket client = new Socket(ip, port);
	       
	        System.out.println("[CLIENT]: "+"Just connected to " + client.getRemoteSocketAddress());
	        // Object I/O Stream
	        OutputStream outToServer = client.getOutputStream();
	        ObjectOutputStream out = new ObjectOutputStream(outToServer);
	        
//	        out.writeUTF("[CLIENT]: "+"Hello from " + client.getLocalSocketAddress());
	        MainMessage sentMessage = new MainMessage();
	        sentMessage.setMessage("[CLIENT]: "+"Hello from " + client.getLocalSocketAddress());
	        out.writeObject(sentMessage);
	        
	        InputStream inFromServer = client.getInputStream();
	        ObjectInputStream in = new ObjectInputStream(inFromServer);
	      
	        try {
				MainMessage receivedMessage = (MainMessage) in.readObject();
		        System.out.println("[CLIENT]: "+"Server says " + receivedMessage.getMessage());
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			}
	        
	        client.close();
	     }
		catch (IOException e) {
			e.printStackTrace();
		}
	}
	*/

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

	public void stopListening() {
		this.getUdpListener().stopListening();
	}
	public void setUdpSocket(DatagramSocket udpSocket) {
		this.udpSocket = udpSocket;
	}

	public InetAddress getServerIP() {
		return serverIP;
	}

	public void setServerIP(InetAddress serverIP) {
		this.serverIP = serverIP;
	}

	public ClientProcessor getProcessor() {
		return processor;
	}

	public void setProcessor(ClientProcessor processor) {
		this.processor = processor;
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
}