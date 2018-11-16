package com.main;

import java.io.IOException;

import com.network.MainServer;

public class MainServerDriver {


	public static void main(String[] args) {
		System.out.println("[Starting] main (MainServerDriver)");
		
		try {
			Thread server = new MainServer(Info.PORT);
			server.start();
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
