package com.main;

import java.io.IOException;

import com.network.candy.CandyServer;

public class CandyServerDriver {

	public static void main(String[] args) {
		System.out.println("[Starting] main (CandyServerDriver)");
		
		try {
			Thread server = new CandyServer(Info.PORT);
			server.start();
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
