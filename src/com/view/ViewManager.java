package com.view;

import java.util.Scanner;

import com.main.Print;


public class ViewManager extends Thread {

	private volatile Mode mode = Mode.MAIN;
	private Scanner scanner;
	private String command;
	
	public void run() {
		// Initialize mode
		this.setMode(Mode.MAIN);
		
		// Initialize scanner
		this.startScanner();
		
		while(this.getMode() != Mode.END) {

			this.consumeCommand();
			// Block until has next input
			while(!this.getScanner().hasNext()) { ; }
			
			// Store command
			this.setCommand(this.getScanner().nextLine());
			
			// Execute command
			this.listen(this.getCommand());
		}
		this.getScanner().close();
	}
	
	public void startScanner() {
		this.setScanner(new Scanner(System.in));
	}
	
	/**
	 * Listens to string input and responds according to the mode
	 * @param mode
	 */
	public void listen(String command) {
		switch(this.getMode()) {
			case MAIN:
				this.mainCommand(command);
				break;
				
			case SERVER:
				this.serverCommand(command);
				break;
				
			case CLIENT:
				this.clientCommand(command);
				break;
				
			default:
				break;
		}
	}
	
	public void consumeCommand() {
		this.setCommand("");
		Print.waiting();
	}
	
	public void mainCommand(String command) {
		if(isStartServer(command)) {
			Print.system("Starting server.");
		}
		else {
			Print.invalid(command);
		}
	}
	
	public void serverCommand(String command) {
		
	}
	
	public void clientCommand(String command) {
		
	}
	public boolean isStartServer(String command) {
		
		if(command.contains("server")) {
			return true;
		}
		
		else {
			return false;
		}
	}
	
	public void startServer() {
		
	}
	
	public Mode getMode() {
		return mode;
	}

	public void setMode(Mode mode) {
		this.mode = mode;
		Print.mode(mode);
	}

	public Scanner getScanner() {
		return scanner;
	}

	public void setScanner(Scanner scanner) {
		this.scanner = scanner;
	}

	public String getCommand() {
		return command;
	}

	public void setCommand(String command) {
		this.command = command.trim();
	}
	
}
