package com.main;

import java.io.IOException;

import com.network.candy.CandyClient;
import com.network.candy.CandyServer;

public class CandyDriver {

	public static void main(String[] args) {
		System.out.println("[Starting] main (CandyDriver)");

		int port = 80;
		
		try {
			Thread server = new CandyServer(port);
			server.start();
			
			CandyClient client = new CandyClient();
			client.start("IPHost", "49.147.224.188", String.valueOf(port));
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
