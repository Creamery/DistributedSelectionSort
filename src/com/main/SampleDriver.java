package com.main;

import java.io.IOException;

import com.sample.SampleClient;
import com.sample.SampleServer;

public class SampleDriver {

	public static void main(String[] args) {
		System.out.println("[Starting] main (SampleDriver)");

		try {
			Thread server = new SampleServer(Info.PORT);
			server.start();
			
			SampleClient client = new SampleClient();
			client.start("localhost", String.valueOf(Info.PORT));
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
