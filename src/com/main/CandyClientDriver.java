package com.main;

import java.io.IOException;

import com.network.candy.CandyClient;

public class CandyClientDriver {
	public static void main(String[] args) {
		System.out.println("[Starting] main (CandyClientDriver)");

		
		try {
			CandyClient client = new CandyClient();
			client.start("IPHost", Info.NETWORK, String.valueOf(Info.PORT));
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
