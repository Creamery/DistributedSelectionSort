package com.network;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.*;
import java.util.ArrayList;

import com.SelectionSort.SelectionClient_UDP;
import com.main.Info;
import com.message.MainMessage;
import com.message.NotifySwapMessage;
import com.message.messageQueue.SelectionInstruction;
import com.network.protocols.TCPTwoWayQueue;
import com.network.protocols.UDPListener;
import com.network.protocols.UDPUnpacker;
import com.reusables.General;

public class MainClient extends Thread implements UDPUnpacker {
	// private TCPTwoWay tcpStream;
//	private TCPTwoWayQueue tcpStream;
	private UDPListener udpListener;
	private ClientProcessor processor;
	
	private InetAddress address;
    private InetAddress serverIP;
    
	private DatagramSocket udpSocket;

	private int UDPPort;
	private int TCPPort;

	//UDP Properties
	private ArrayList<Integer> toSort;
	private DatagramSocket mainUDPSocket;
	private DatagramSocket requestSocket;

	private volatile MainMessage mainMessage;
	public MainClient() {
		this.setUDPPort(Info.BROADCAST_PORT);
		this.setTCPPort(Info.PORT);
		
//		try {
//			this.setProcessor(new ClientProcessor());
//			this.setTcpStream(new TCPTwoWay("Client", this.getTCPPort(), this.getProcessor()));
//			this.setTcpStream(new TCPTwoWayQueue("Client", this.getTCPPort(), this.getProcessor()));
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
		
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
	
	public void send(String message) {
		System.out.println("Sending "+message);
		MainMessage mainMessage = new MainMessage();
		mainMessage.setMessage(message);
//		this.getTcpStream().sendAsClient(message);
	}
	

	// Listen for any server broadcast to connect
	public void listen(String newHeader) {
		this.getUdpListener().listen(newHeader);
	}
	
	// Listen to a server broadcast
	public void run() {
	}
	
	// Send IP as a reply
	public void send() {
		System.out.println("Sending IP...");
		byte[] buffer = (Info.HDR_CLIENT+Info.HDR_SPLIT+Info.NETWORK+"#").getBytes();
		System.out.println("IP: "+Info.NETWORK);
		DatagramPacket packet = null;
		
		// Prepare to send
		try {
			packet = new DatagramPacket(buffer, buffer.length, this.getServerIP(), this.getUDPPort());

			System.out.println("Sending packet to "+this.getServerIP()+" : "+this.getUDPPort());
			// Send IP
			this.getUdpSocket().send(packet);
			// System.out.println("Sending packet to port "+this.getUdpSocket().getPort());
			// Print.serverBroadcast();
		} catch(IOException e){
			e.printStackTrace();
		}
	}
	@Override
	public void unpack(String message) {
		this.stopListening();
		String ip = message.substring(message.indexOf("/")+1);
		System.out.println("Unpacked message: "+message+"\nTrimmed: "+ip);
		try {
			this.setServerIP(InetAddress.getByName(ip));
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}
		System.out.println("TODO TCP setup");
        // this.setupTCPStream();
		System.out.println("UDP reply send");
		this.send();
	}
	
	// Set the server and client addresses of the TCP connection
	public void setupTCPStream() {
		System.out.println("TCP Setup");
//		this.getTcpStream().setServerIP(this.getServerIP());
//		this.getTcpStream().setClientIP(this.getAddress());
//
//		this.getTcpStream().initializeClientSocket(this.getServerIP());
//		this.getTcpStream().startAsClient();
	}

	/// UDP-related methods

	/**
	 * This method is called when the client is 'ready' for sorting.
	 */
	public void setupUDPStream(){
		System.out.println("UDP Setup");
		// UDP -- wait server to send a TCP connection request
		try {
			DatagramSocket udpSocket = new DatagramSocket(Info.PORT);
			byte[] buf = new byte[Info.UDP_PACKET_SIZE];
			DatagramPacket pck = new DatagramPacket(buf,buf.length);
			try {
				System.out.println("Waiting for SYNC prompt...");
				udpSocket.receive(pck);
			} catch (IOException e) {
				e.printStackTrace();
			}
			String msg = new String(pck.getData()).trim();
			if(msg.equals("SYNC")){
				// SEND A TCP Connection Request
				try {
					Socket inSocket = new Socket(this.getServerIP(), Info.PORT);
					ObjectInputStream inStream = new ObjectInputStream(inSocket.getInputStream());

					try{
						System.out.println("Waiting for SORTLIST");
						this.toSort = (ArrayList<Integer>) inStream.readObject();

						inStream.close();
						inSocket.close();
					} catch(ClassNotFoundException e){ e. printStackTrace(); }
				} catch (IOException e){ e.printStackTrace(); }
			}
			udpSocket.close();
		} catch (SocketException e){ e.printStackTrace(); }
		// proceed to client-side selection
		this.runConsumerSelectionLoop();
	}

	private void runConsumerSelectionLoop(){
		try{
			mainUDPSocket = new DatagramSocket(Info.PORT);
			requestSocket = new DatagramSocket(Info.REQUEST_PORT);
		} catch (SocketException e){ e.printStackTrace(); }

		boolean isRunning = true;
		while(isRunning){
			// Wait for Server's ready message
			byte[] buf = new byte[Info.UDP_PACKET_SIZE];
			DatagramPacket pck = new DatagramPacket(buf,buf.length);
			try{
				mainUDPSocket.receive(pck);
			} catch (IOException e){ e.printStackTrace(); }
			String received = new String(pck.getData()).trim();
			if(received.equals("READY")){
				// Start requesting for an instruction
				String msg = "";
				while(!msg.equals("EMPTY")){
					sendServer("REQ");
					msg = waitFromServer();
					if(msg.contains("INTSR:")){
						// Process Instruction
						SelectionInstruction instr = SelectionInstruction.parseString(msg);
						int localMin = SelectionClient_UDP.runSelection(toSort,instr);
						// Send Local Min
						this.sendServer("LMIN:"+localMin);
//						this.sendServer("LMIN:"+localMin+"-"+instr.getStartIndex()+"-"+instr.getEndIndex());
					}
				}
				String swapInstr = waitFromServer();
				if(msg.contains("SWAP:")){
					// Perform swap to resync list
					NotifySwapMessage nsm = NotifySwapMessage.parseString(swapInstr);
					int a = toSort.get(nsm.getIndexA());
					toSort.set(nsm.getIndexA(),toSort.get(nsm.getIndexB()));
					toSort.set(nsm.getIndexB(),a);
				}
				else
					new Exception("Invalid protocol sequence!").printStackTrace();

			}else if(received.equals("STOP")){
				isRunning = false;
				System.out.println("Sort Complete");
			}
		}
	}

	public void sendServer(String message){
		byte[] byt = General.padMessage(message.getBytes());
		DatagramPacket pck = new DatagramPacket(byt,byt.length,
				this.getServerIP(),Info.PORT);
		try {
			requestSocket.send(pck);
		} catch (IOException e){ e.printStackTrace(); }
	}

	public String waitFromServer(){
		byte[] buf = new byte[Info.UDP_PACKET_SIZE];
		DatagramPacket pck = new DatagramPacket(buf,buf.length);
		try{
			requestSocket.receive(pck);
		} catch (IOException e){ e.printStackTrace(); }
		return new String(pck.getData()).trim();
	}
	/// End of UDP-related methods

	public void setMainMessage(String message) {
		this.mainMessage = new MainMessage();
		mainMessage.setMessage(message);
	}
	
	public MainMessage getMainMessage() {
		this.setMainMessage("message");
		return this.mainMessage;
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
				this.setUdpSocket(new DatagramSocket(this.getUDPPort()));
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

//	public TCPTwoWayQueue getTcpStream() {
//		return tcpStream;
//	}
//
//	public void setTcpStream(TCPTwoWayQueue tcpStream) {
//		this.tcpStream = tcpStream;
//	}
//	public TCPTwoWay getTcpStream() {
//		return tcpStream;
//	}
//
//	public void setTcpStream(TCPTwoWay tcpStream) {
//		this.tcpStream = tcpStream;
//	}

	public UDPListener getUdpListener() {
		return udpListener;
	}

	public void setUdpListener(UDPListener udpListener) {
		this.udpListener = udpListener;
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
}