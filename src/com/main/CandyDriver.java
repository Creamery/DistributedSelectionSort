package com.main;

import java.io.IOException;

import com.sample.SampleClient;
import com.sample.SampleServer;

public class CandyDriver {

	public static void main(String[] args) {
		System.out.println("[Starting] main (CandyDriver)");

		int port = 6606;
		
		try {
			Thread server = new SampleServer(port);
			server.start();
			
			SampleClient client = new SampleClient();
			client.start("localhost", String.valueOf(port));
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
