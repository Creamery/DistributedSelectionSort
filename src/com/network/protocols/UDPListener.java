package com.network.protocols;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.ArrayList;

import com.main.Info;

public class UDPListener extends Thread {

	private boolean isListening;
	private DatagramPacket packet;
	private DatagramSocket udpSocket;

	private ArrayList<InetAddress> listClients;
    private byte[] buffer = new byte[Info.BUFFER_SIZE];
    
    public UDPListener(DatagramSocket udpSocket, ArrayList<InetAddress> listClients) {
    	this.setUdpSocket(udpSocket);
    	this.setListClients(listClients);
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

	
	public void listen() {
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
	public ArrayList<InetAddress> getListClients() {
		if(this.listClients == null) {
			this.listClients = new ArrayList<InetAddress>();
		}
		return listClients;
	}

	public void setListClients(ArrayList<InetAddress> listClients) {
		this.listClients = listClients;
	}
	public void run() {
		System.out.println("Listening...");
		while(this.isListening()) {
			// Initialize a new packet per iteration
			this.setPacket(new DatagramPacket(this.getBuffer(), this.getBuffer().length));
			
			// Allow socket to receive packets
			try {
				this.getUdpSocket().receive(packet);
			} catch (IOException e1) {
				e1.printStackTrace();
			}
			
			// Decode packet message
			String message = new String(packet.getData()).trim();
			
			// If message is not empty, add the client IP to the list of clients
			if(message != ""){
				try {
					this.getListClients().add(InetAddress.getByName(message));
				} catch (UnknownHostException e) {
					e.printStackTrace();
				}
			}
		}
	}
}
