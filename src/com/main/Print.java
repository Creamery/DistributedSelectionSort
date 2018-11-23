package com.main;

import java.util.Random;

import com.view.Mode;

public class Print {
	
	private static String SYSTEM = "> ";

	public static void message(String message) {
		System.out.println(message);
	}
	
	public static void system(String message) {
		System.err.println(SYSTEM+message);
	}

	public static void mode(Mode mode) {
		System.err.println();
		System.err.println("[@' ▽  '@]: Welcome to "+mode+" mode. Here are the available commands: ");
		commands(mode);
	}
	
	public static void commands(Mode mode) {
		switch(mode) {
			case MAIN:
				System.err.println("\t\t⦿ SERVER ");
				System.err.println("\t\t⦿ CLIENT ");
				System.err.println("\t\t⦿ EXIT ");
			break;
			default:
				break;
		}
	}
	
	public static void invalid(String message) {
		Random random = new Random();

		System.err.print("[*˃ ᆺ  ˂*]: ");
		switch(random.nextInt(5)) {
			case 0:
				System.err.println("Dafuq is "+message+"??");
				break;
			case 1:
				System.err.println("I don't know how to "+message+"??");
				break;
			case 2:
				System.err.println("I don't recognize \""+message+"\"");
				break;
			case 3:
				System.err.println("What's \""+message+"\" supposed to mean?");
				break;
			default:
				System.err.println(message+" is an invalid command.");
				break;
		}
	}
	
	
	public static void waiting() {
		System.err.println();
		System.out.println();
		System.out.print("[@' w '@]: ");
	}
}
