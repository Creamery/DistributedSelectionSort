package com.network;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.UnknownHostException;

import com.main.Info;
import com.main.Print;
import com.network.tcp.TCPTwoWay;

public class MainClient extends Thread {
	private TCPTwoWay tcpStream;
	
	private ClientProcessor processor;
	
	private InetAddress address;
    private InetAddress serverIP;
    
	private DatagramSocket udpSocket;
	private DatagramPacket packet;
    private byte[] buffer = new byte[Info.BUFFER_SIZE];

	private boolean isListening;
	
	public MainClient() {
		try {
			this.setTcpStream(new TCPTwoWay("Client "));
		} catch (IOException e) {
			e.printStackTrace();
		}
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
        this.setListening(true);
        
    	// To listen, first create a new UDP socket on an arbitrary BROADCAST_PORT and set its Broadcast to true
        try {
            this.setUdpSocket(new DatagramSocket(Info.BROADCAST_PORT));
            this.getUdpSocket().setBroadcast(true);
        }catch(Exception e){
            e.printStackTrace();
        }
        
        // Run the listening loop
        this.run();
	}
	
	public void stopListening() {
		this.setListening(false);
	}
	
	// Listen to a server broadcast
	public void run() {
        while(this.isListening()){
            this.setPacket(new DatagramPacket(this.getBuffer() ,this.getBuffer().length));
            try {
            	this.getUdpSocket().receive(this.getPacket());
            } catch(IOException e){
                e.printStackTrace();
            }
            // Message contains server IP
            String message = new String(this.getPacket().getData()).trim();
            if(message != ""){
            	try {
					this.setServerIP(InetAddress.getByName(message));
				} catch (UnknownHostException e) {
					e.printStackTrace();
				}
            	// Stop listening
            	this.setListening(false);
            	Print.clientConnected(message);
            }
        }
        this.getUdpSocket().close();
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

	public DatagramSocket getUdpSocket() {
		return udpSocket;
	}

	public void setUdpSocket(DatagramSocket udpSocket) {
		this.udpSocket = udpSocket;
	}
	public boolean isListening() {
		return isListening;
	}
	public void setListening(boolean isListening) {
		this.isListening = isListening;
	}

	public byte[] getBuffer() {
		return buffer;
	}

	public void setBuffer(byte[] buffer) {
		this.buffer = buffer;
	}

	public DatagramPacket getPacket() {
		return packet;
	}

	public void setPacket(DatagramPacket packet) {
		this.packet = packet;
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
}
