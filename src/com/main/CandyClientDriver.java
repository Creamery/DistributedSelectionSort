package com.main;

import java.io.IOException;

import com.network.candy.CandyClient;

public class CandyClientDriver {
	public static String PUBLIC_NET = "49.147.224.188";
	public static String LOCAL_NET = "192.168.96.105";
	public static void main(String[] args) {
		System.out.println("[Starting] main (CandyClientDriver)");

		
		try {
			CandyClient client = new CandyClient();
			client.start("IPHost", PUBLIC_NET, String.valueOf(CandyServerDriver.PORT));
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
