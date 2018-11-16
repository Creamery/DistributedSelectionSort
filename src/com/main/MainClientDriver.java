package com.main;

import java.io.IOException;

import com.network.MainClient;

public class MainClientDriver {
	public static void main(String[] args) {
		System.out.println("[Starting] main (MainClientDriver)");

		try {
			MainClient client = new MainClient();
			client.start("IPHost", Info.LOCAL_NET, String.valueOf(Info.PORT));
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
