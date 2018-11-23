package com.main;

import java.util.Scanner;

public class Driver {

	public static void main(String[] args) {
		System.out.println("[Starting] main (Driver)");
		MainServerDriver.main(args);

		Scanner scanner = new Scanner(System.in);
		String command = "";
		
		while(!command.equals("END")) {
			command = scanner.nextLine().trim();
			System.out.println(command);
			if(command.equals("new client")) {
				System.out.println("NEW CLIENT");
				MainClientDriver.main(args);
			}
		}
		scanner.close();
	}
}
