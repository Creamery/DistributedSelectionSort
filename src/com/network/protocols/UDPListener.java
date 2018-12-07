package com.network.protocols;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;

import com.main.Info;
import com.message.MainMessage;

public class UDPListener extends Thread {
	private UDPUnpacker parent;
	
	private boolean isListening;
	private DatagramPacket packet;
	private DatagramSocket udpSocket;

    private byte[] buffer = new byte[Info.BUFFER_SIZE];
    
    public UDPListener(UDPUnpacker parent) {
    	this.setParent(parent);
    	this.setUdpSocket(parent.getUdpSocket());
    }

	public void listen() {
		System.out.println("LISTENING (UDPListener)");
		this.setListening(true);
		this.run();
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
	public void send() {
		this.setPacket(new DatagramPacket(this.getBuffer(), this.getBuffer().length));
	}
	public void run() {
		System.out.println("Listening...");
		while(this.isListening()) {


			System.out.println("Sending (1)");
			// Initialize a new packet per iteration
			this.send();
			
			
			// Allow socket to receive packets
			try {

				System.out.println("Waiting for UDP socket wait");
				this.getUdpSocket().receive(packet);
			} catch (IOException e1) {
				e1.printStackTrace();
			}
			// Decode packet message
			String message = new String(packet.getData()).trim();

			System.out.println("Received "+message);
			// If message is not empty, add the client IP to the list of clients
			if(message != ""){

				System.out.println("Unpacking...");
				this.getParent().unpack(message);
			}
			else {

				System.out.println("Message is empty...");
			}
		}
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
}