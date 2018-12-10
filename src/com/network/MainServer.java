package com.network;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.*;
import java.util.ArrayList;
import java.util.HashMap;

import com.SelectionSort.SelectionSort_UDP;
import com.main.Info;
import com.main.Print;
import com.message.messageQueue.queueManager.QueueManager;
import com.network.protocols.TCPTwoWay;
import com.network.protocols.TCPTwoWayQueue;
import com.network.protocols.UDPListener;
import com.network.protocols.UDPUnpacker;
import com.reusables.CsvParser;
import com.reusables.CsvWriter;
import com.reusables.General;
import sun.nio.cs.ext.TIS_620;

public class MainServer extends Thread implements UDPUnpacker {

	private QueueManager messageQ;

	// private TCPTwoWay tcpStream;
	private TCPTwoWayQueue tcpStream;
	private UDPListener udpListener;
//	private ServerProcessor processor;
	
	private InetAddress address;
	
	private DatagramSocket udpSocket;
	private ObjectOutputStream objectOutputStream;
	

	private ArrayList<InetAddress> listClients;
	private int UDPPort;
	private int TCPPort;

	// MessageQueue implementation of Selection Sort
    private ArrayList<Integer> toSort;
    // UDP::
    private SelectionSort_UDP sSort_UDP;
    private DatagramSocket mainUDPSocket;
	
	public MainServer() throws IOException {
		this.setUDPPort(Info.BROADCAST_PORT);
		this.setTCPPort(Info.PORT);
		
		try {
//			this.setProcessor(new ServerProcessor(Info.CLIENT_SIZE));
			System.out.println("IP:"+Info.NETWORK);
			this.setAddress(InetAddress.getByName(Info.NETWORK.trim()));
//			this.setTcpStream(new TCPTwoWay("Server", this.getTCPPort(), this.getProcessor()));
//			this.setTcpStream(new TCPTwoWayQueue("Server", this.getTCPPort(), this.getProcessor()));
		} catch (IOException e) {
			e.printStackTrace();
		}

		// Load ArrayList
        toSort = CsvParser.read(Info.FileNames.preset_filename);

		this.setUdpListener(new UDPListener(this));
//		this.messageQ = new QueueManager();
	}
	
	
//	// Initialize processor then run it
//	public void process() {
//		if(this.getProcessor() == null) {
//			this.setProcessor(new ServerProcessor(Info.CLIENT_SIZE));
//		}
//		this.getProcessor().process();
//	}
//

	public void listen(String newHeader) {
		this.getUdpListener().listen(newHeader);
	}
	public void stopListening() {
        Print.response("Stopped listening");
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
		byte[] buffer = (Info.HDR_SERVER+Info.HDR_SPLIT+Info.NETWORK).getBytes();
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
		
		// Prepare to listen to replies
		this.listen(Info.HDR_SERVER);
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

//	public ServerProcessor getProcessor() {
//		return processor;
//	}
//
//	public void setProcessor(ServerProcessor processor) {
//		this.processor = processor;
//	}

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
	public TCPTwoWayQueue getTcpStream() {
		return tcpStream;
	}

	public void setTcpStream(TCPTwoWayQueue tcpStream) {
		this.tcpStream = tcpStream;
	}
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


	@Override
	public void unpack(String message) {
		try {
			String ip = message.substring(message.indexOf("_")+1, message.indexOf("#"));
			InetAddress address = InetAddress.getByName(ip);
			
			if(this.getAddress().toString().substring(1).equals(ip)) {
				//System.out.println("Received own address");
			}
			else {
				this.getListClients().add(address);
				Print.message("Added client "+ip);
				
				if(this.getListClients().size() == Info.CLIENT_SIZE) {
					this.stopListening();
					// Begin Sorting TODO: Temporary
					try {
						Thread.sleep(1000);
						synchronizeArrayWithClients();
						startSort_UDP();
					} catch(Exception e){ e.printStackTrace(); }
				}
			}
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}
	}

	public void startTCPConnection() {
		// TCP -- Synchronize Array
		 this.getTcpStream().startAsServer();
	}

	public void synchronizeArrayWithClients(){
		System.out.println("Synchronizing toSort list with clients...");
	    // UDP -- Tell clients to send a TCP connection request
		try {
			DatagramSocket udpSocket = new DatagramSocket(Info.PORT);

			for (int i=0; i<this.getListClients().size();i++) {
				byte[] buf = General.padMessage(new String("SYNC").getBytes());

				DatagramPacket pck = new DatagramPacket(buf, buf.length,
						this.getListClients().get(i), 4000);
				System.out.println("Sending 'SYNC' of size"+pck.getLength()+" to "+pck.getAddress()+" : "+pck.getPort());
				try {
					udpSocket.send(pck);
					System.out.println("Message successfully sent.");
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			udpSocket.close();
		} catch (SocketException e){ e.printStackTrace(); }

        // TCP -- Synchronize Array
        try {
            ServerSocket sendArraySocket = new ServerSocket(Info.PORT);
            ObjectOutputStream[] toClientStreams = new ObjectOutputStream[this.getListClients().size()];
            // Create a TCP connection for each client
			System.out.println("Establishing TCP Connections...");
            for (int i=0; i<this.getListClients().size();i++){
                Socket streamSocket = sendArraySocket.accept();
                Print.response("Just connected to " + streamSocket.getRemoteSocketAddress());
                toClientStreams[i] = new ObjectOutputStream(streamSocket.getOutputStream());
            }
            // Send arrayList through the outputStreams
			System.out.println("Sending list...");
            for(int i=0; i<toClientStreams.length;i++){
                toClientStreams[i].writeObject(toSort);
            }

        } catch (IOException e){
            e.printStackTrace();
        }

		System.out.println("Synchronization complete!");
    }

    /// METHODS FOR UDP-BASED SORTING
    public void startSort_UDP(){
		System.out.println("Starting sort...");
		try {
			this.mainUDPSocket = new DatagramSocket(Info.PORT);
		} catch (SocketException e){ e.printStackTrace(); }

	    sSort_UDP = new SelectionSort_UDP(this.toSort,this.getListClients().size(),this);
        ArrayList<Integer> sorted = sSort_UDP.runSorting();
		System.out.println("List sorted!");
		System.out.println("Saving sorted list to file...");
		CsvWriter.write(sorted,"neil-test");
		System.out.println("List successfully saved!");
    }


    public void sendAllClients(String message){
		for(int i=0; i<this.getListClients().size(); i++){
			sendToClient(this.listClients.get(i),message);
		}
	}

	public void sendToClient(InetAddress dest, String message){
		byte[] buf = General.padMessage(message.getBytes());
		DatagramPacket pck = new DatagramPacket(buf, Info.UDP_PACKET_SIZE,
				dest, Info.PORT);
		try {
			this.mainUDPSocket.send(pck);
		} catch (IOException e){ e.printStackTrace(); }
	}

	public DatagramSocket getUDPSocket(){
		return this.mainUDPSocket;
	}

    /// END OF METHODS FOR UDP-BASED SORTING

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


	public QueueManager getMessageQ() {
		return messageQ;
	}


	public void setMessageQ(QueueManager messageQ) {
		this.messageQ = messageQ;
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