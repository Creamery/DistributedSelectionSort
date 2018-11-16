package com.main;

import java.io.IOException;

import com.network.candy.CandyClient;

public class CandyClientDriver {
	public static void main(String[] args) {
		System.out.println("[Starting] main (CandyClientDriver)");

		
		try {
			CandyClient client = new CandyClient();
			client.start("IPHost", Info.PUBLIC_NET, String.valueOf(Info.PORT));
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
