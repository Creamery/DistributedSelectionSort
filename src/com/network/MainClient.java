package com.network;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.*;
import java.time.Instant;
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
import com.reusables.Stopwatch;

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
	private DatagramSocket prepUDPSocket;
	private Socket inSocket;

	private volatile MainMessage mainMessage;
	public MainClient() {
		this.setUDPPort(Info.BROADCAST_PORT);
		this.setTCPPort(Info.PORT);
		try {
			this.prepUDPSocket = new DatagramSocket(4000);
		} catch(Exception e) {e.printStackTrace();}
//		try {
//			this.setProcessor(new ClientProcessor());
//			this.setTcpStream(new TCPTwoWay("Client", this.getTCPPort(), this.getProcessor()));
//			this.setTcpStream(new TCPTwoWayQueue("Client", this.getTCPPort(), this.getProcessor()));
//		} catch (IOException e) {
//			e.printStackTrace();
//		}

		try{
			mainUDPSocket = new DatagramSocket(Info.PORT);
			requestSocket = new DatagramSocket(Info.REQUEST_PORT);
		} catch (SocketException e){ e.printStackTrace(); }

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
		String ip = message.substring(message.indexOf("_")+1);
		System.out.println("Unpacked message: "+message+"\nTrimmed: "+ip);
		try {
			this.setServerIP(InetAddress.getByName(ip));

		} catch (Exception e) {
			e.printStackTrace();
		}
		System.out.println("TODO TCP setup");
        // this.setupTCPStream();
		System.out.println("UDP reply send");
		this.send();
//		this.udpSocket.close();
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
		stopListening();
		System.out.println("UDP Setup");
		// UDP -- wait server to send a TCP connection request
		try {
//			this.prepUDPSocket = new DatagramSocket(4000);
//			this.prepUDPSocket.connect(this.getServerIP(),4000);
			byte[] buf = new byte[Info.UDP_PACKET_SIZE];
			System.out.println("buf len: "+buf.length);
//			getUdpListener().setBuffer(buf);
			DatagramPacket pck = new DatagramPacket(buf,buf.length);
//			try {
				System.out.println("Waiting for SYNC prompt, listening at:"+this.prepUDPSocket.getPort()+"...");
				this.prepUDPSocket.receive(pck);
//				listen("SYNC");
			} catch (IOException e) {
				e.printStackTrace();
			}
			String msg = "SYNC";
			if(msg.equals("SYNC")){
				// SEND A TCP Connection Request
				try {
//					Thread.sleep(2000);
					this.inSocket = new Socket(this.getServerIP(), Info.PORT);
					ObjectInputStream inStream = new ObjectInputStream(inSocket.getInputStream());

					try{
						System.out.println("Waiting for SORTLIST");
						ArrayList<Integer> a = (ArrayList<Integer>) inStream.readObject();
						toSort = a;
						System.out.println("SORTLIST obtained");
						inStream.close();
						inSocket.close();
					} catch(ClassNotFoundException e){ e. printStackTrace(); }
				} catch (Exception e){ e.printStackTrace(); }
			}
			this.prepUDPSocket.close();
//		} catch (SocketException e){ e.printStackTrace(); }
		// proceed to client-side selection
		this.runConsumerSelectionLoop();
	}

	private void runConsumerSelectionLoop(){

		boolean isRunning = true;
		boolean outOfOrderReady = false;
		General.trackStats_start("client");
		while(isRunning){
			// Wait for Server's ready message
			Stopwatch.start_aggregate();
			String msg;
			if(outOfOrderReady)
				msg = "READY";
			else
				msg = waitFromServer();
			if(msg.equals("READY")){
				outOfOrderReady = false;
				this.sendServer("REQ");
				msg = this.waitFromServer();
				if(msg.contains("INSTR:")){
					while(msg.contains("INSTR:")){
						if(msg.contains("INSTR:")){
							// Process Instruction
							SelectionInstruction si = SelectionInstruction.parseString(msg);
//							System.out.println("Obtained Instruction: "+si.toString());
							int lm = SelectionClient_UDP.runSelection(toSort,si);

							// Send Local Min
//							System.out.println("found local minimum");
							this.sendServer("LMIN:"+lm);
//							System.out.println("sent local minimum");

							msg = this.waitFromServer();
						}
					}
				}

				if(msg.contains("READY")) {
					outOfOrderReady = true;
					msg = waitFromServer();
				}

				if(msg.contains("EMPTY")){

					msg = waitFromServer();

					if(msg.contains("READY")) {
						outOfOrderReady = true;
						msg = waitFromServer();
					}

					if(msg.contains("SWAP")){
						// Perform swap to resync list
						NotifySwapMessage nsm = NotifySwapMessage.parseString(msg);
						System.out.println("Swapping "+nsm.getIndexA()+" with "+nsm.getIndexB());
						int a = toSort.get(nsm.getIndexA());
						toSort.set(nsm.getIndexA(),toSort.get(nsm.getIndexB()));
						toSort.set(nsm.getIndexB(),a);
					}
				}else if(msg.contains("SWAP")){
					// Perform swap to resync list
					NotifySwapMessage nsm = NotifySwapMessage.parseString(msg);
					System.out.println("Swapping "+nsm.getIndexA()+" with "+nsm.getIndexB());
					//Perform swap later
					msg = waitFromServer();

					if(msg.contains("READY")) {
						outOfOrderReady = true;
						msg = waitFromServer();
					}

					if(msg.contains("EMPTY")){
//						System.out.println("received EMPTY");
						//Perform swap now
						int a = toSort.get(nsm.getIndexA());
						toSort.set(nsm.getIndexA(),toSort.get(nsm.getIndexB()));
						toSort.set(nsm.getIndexB(),a);
					}
				}
			}else if(msg.equals("STOP")){
				isRunning = false;
				General.trackStats_stop("client");
				System.out.println("Sort Complete");
			}
			Stopwatch.stop_aggregate();
		}
		Stopwatch.getAggregateAndPrint();
	}

	public void sendServer(String message){
		byte[] byt = General.padMessage(message.getBytes());
		DatagramPacket pck = new DatagramPacket(byt,byt.length,
				this.getServerIP(),Info.REQUEST_PORT);
		try {
			requestSocket.send(pck);
//			System.out.println(Instant.now()+" || SENT "+message);
		} catch (IOException e){ e.printStackTrace(); }
	}

	public String waitFromServer(){
		byte[] buf = new byte[Info.UDP_PACKET_SIZE];
		DatagramPacket pck = new DatagramPacket(buf,buf.length);
		try{
			mainUDPSocket.setSoTimeout((int)Info.TIMEOUT_DELAY);
			mainUDPSocket.receive(pck);
		}
		catch (SocketException e1){
			System.out.println("Timeout occurred");
			return "EMPTY";
		} catch (IOException e){ e.printStackTrace(); }
		String s = new String(pck.getData()).trim();
//		System.out.println(Instant.now()+" || Received: "+s);
		return s;
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
		this.udpListener = null;
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
		if(udpListener == null)
			udpListener = new UDPListener(this);
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