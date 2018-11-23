package com.view;

import java.util.Scanner;

import com.controller.ControllerManager;
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
		this.getScanner().reset();
	}
	
	public void mainCommand(String command) {
		if(isStartServer(command)) {
			ControllerManager.Instance().getServer().start();
			this.setMode(Mode.SERVER);
		}
		else if(isStartClient(command)) {
			ControllerManager.Instance().getClient().start();
			this.setMode(Mode.CLIENT);
		}
		else if(isExit(command)) {
			Print.response("Goodbye!");
			this.setMode(Mode.END);
		}
		else {
			Print.invalid(command);
			Print.waiting();
		}
		this.consumeCommand();
	}
	
	public void serverCommand(String command) {
		if(isExit(command)) {
			this.setMode(Mode.MAIN);
		}
		else {
			Print.invalid(command);
			Print.waiting();
		}
		this.consumeCommand();
	}
	
	public void clientCommand(String command) {
		if(isExit(command)) {
			this.setMode(Mode.MAIN);
		}
		else {
			Print.invalid(command);
			Print.waiting();
		}
		this.consumeCommand();
	}
	public boolean isStartServer(String command) {
		if(command.toLowerCase().contains("server")) {
			return true;
		}
		
		else {
			return false;
		}
	}
	public boolean isStartClient(String command) {
		if(command.toLowerCase().contains("client")) {
			return true;
		}
		
		else {
			return false;
		}
	}
	public boolean isExit(String command) {
		String lowCommand = command.toLowerCase();
		if(	lowCommand.contains("exit") ||
			lowCommand.contains("end") ||
			lowCommand.contains("bye") ||
			lowCommand.contains("close")) {
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
