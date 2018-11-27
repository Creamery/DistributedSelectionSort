package com.main;

import java.util.Random;

import com.controller.ClientController;
import com.controller.ServerController;
import com.view.Mode;

public class Print {
	
	private static String SYSTEM = "> ";

	public static void message(String message) {
		System.out.println(message);
	}
	
	public static void system(String message) {
		System.err.println(SYSTEM+message);
	}
	
	public static void clientConnected(String message) {
		System.err.println("Client connected to "+message);
	}
	
	public static void clients(ServerController server) {
		String strList = "";

		/*
		ClientController client;
		ArrayList<InetAddress> listClients = server.getListClients();
		for(int i = 0; i < listClients.size(); i++) {
			client = listClients.get(i);
			strList += "\\n\\t\\t⦿ " + client.getAddress();
		}
		*/
		System.err.println("\n\n[@' ▽  '@]: You have the following connected clients: " + strList);
	}

	public static void server(ClientController client) {
		System.err.println("\n\n[@' ▽  '@]: You are connected to the "+client.getServerIP()+" server.");
	}
	public static void serverBroadcast() {
		System.err.println("\n\n[@' ▽  '@]: Broadcast sent. Waiting for a reply...");
	}
	public static void mode(Mode mode) {
		if(mode != Mode.END) {
			switch(mode) {
				case MAIN:
					System.err.println("\n\n[@' ▽  '@]: Welcome to "+mode+" mode. Here are the available commands: " +
					"\n\t\t⦿ SERVER " +
					"\n\t\t⦿ CLIENT " +
					"\n\t\t⦿ EXIT ");
					break;
				case SERVER:
					System.err.println("\n\n[@' ▽  '@]: Welcome to "+mode+" mode. Here are the available commands: " +
							"\n\t\t⦿ BROADCAST IP " +
							"\n\t\t⦿ VIEW CLIENTS " +
							"\n\t\t⦿ START SORTING "	 +
							"\n\t\t⦿ STOP SORTING "	 +
							"\n\t\t⦿ EXIT ");
					break;
				case CLIENT:
					System.err.println("\n\n[@' ▽  '@]: Welcome to "+mode+" mode. Here are the available commands: " +
							"\n\t\t⦿ START CLIENT " +
							"\n\t\t⦿ STOP CLIENT " +
							"\n\t\t⦿ VIEW SERVER " +
							"\n\t\t⦿ EXIT ");
					break;
				default:
					break;
			}
			Print.waiting();
		}
	}
	
	public static void response(String message) {
		System.out.println("[☆＾ ▽  ＾☆]: "+message);
	}
	
	public static void invalid(String message) {
		Random random = new Random();

		switch(random.nextInt(5)) {
			case 0:
				System.err.println("[*˃ ᆺ  ˂*]: " + "Dafuq is "+message+"??");
				break;
			case 1:
				System.err.println("[*˃ ᆺ  ˂*]: " + "I don't know how to "+message+"??");
				break;
			case 2:
				System.err.println("[*˃ ᆺ  ˂*]: " + "I don't recognize \""+message+"\"");
				break;
			case 3:
				System.err.println("[*˃ ᆺ  ˂*]: " + "What's \""+message+"\" supposed to mean?");
				break;
			default:
				System.err.println("[*˃ ᆺ  ˂*]: " + message+" is an invalid command.");
				break;
		}
	}
	
	
	public static void waiting() {
		System.err.println();
		System.out.println();
		System.out.print("[@' w '@]: ");
	}
}
