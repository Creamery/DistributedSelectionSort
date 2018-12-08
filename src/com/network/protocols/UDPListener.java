package com.network.protocols;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;

import com.main.Info;

public class UDPListener extends Thread {
	private UDPUnpacker parent;
	
	private boolean isListening = false;
	private DatagramPacket packet;
	private DatagramSocket udpSocket;
	private InetAddress sendAddress;
	
    private byte[] buffer = new byte[Info.BUFFER_SIZE];
    
    public UDPListener(UDPUnpacker parent) {
    	this.setParent(parent);
    	this.setUdpSocket(parent.getUdpSocket());
    }

	public void listen() {
		if(!this.isListening()) {
			System.out.println("LISTENING (UDPListener)");
			this.setListening(true);
			this.run();
		}
	}
	
	public void stopListening() {
		// Stop thread
		this.setListening(false);
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
	
	public void run() {
		System.out.println("UDP Listening...");
		
		while(this.isListening()) {
			// Initialize packet
			this.setPacket(new DatagramPacket(this.getBuffer(), this.getBuffer().length));
			
			// Allow socket to receive packets
			try {
				System.out.println("Waiting for UDP socket waiting on port "+this.getUdpSocket().getLocalPort());
				this.getUdpSocket().receive(packet);
			} catch (IOException e1) {
				e1.printStackTrace();
			}
			// Decode packet message
			String message = new String(packet.getData()).trim();
			System.out.println("Received "+message);
			
			// If message is not empty, add the client IP to the list of clients
			if(message != ""){
				this.setListening(false);

				System.out.println("Unpacking...");
				this.reply(packet);
				this.getParent().unpack(message);

				// Send back a new packet
				System.out.println("Sending back at port "+this.getUdpSocket().getPort());
				this.setPacket(new DatagramPacket(this.getBuffer(), this.getBuffer().length, this.getSendAddress(), this.getUdpSocket().getPort()));
		
			}
			else {

				System.out.println("Message is empty...");
			}
		}
	}
	
    public void reply(DatagramPacket packet) {
    	String message = new String(packet.getData()).trim();
		
    	String ip = message.substring(message.indexOf("/")+1);
		System.out.println("Unpacked message: "+message+"\nTrimmed: "+ip);

		try {
			this.setSendAddress(InetAddress.getByName(ip));
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}

		DatagramPacket reply = new DatagramPacket(buffer, buffer.length, this.getSendAddress(), this.getUdpSocket().getLocalPort());

		try {
			this.getUdpSocket().send(reply);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		System.out.println("Sent reply");
    }
    
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

	
	public UDPUnpacker getParent() {
		return parent;
	}

	public void setParent(UDPUnpacker parent) {
		this.parent = parent;
	}

	public InetAddress getSendAddress() {
		return sendAddress;
	}

	public void setSendAddress(InetAddress sendAddress) {
		this.sendAddress = sendAddress;
	}
}