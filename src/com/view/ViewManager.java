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
			this.getScanner().reset();
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
			// Create a server
			ControllerManager.Instance().getServer().start();
			this.setMode(Mode.SERVER);	
		}
		else if(isStartClient(command)) {

			this.setMode(Mode.CLIENT);
			// Create a client and start listening
			// ControllerManager.Instance().getClient().start();
		}
		else if(isExit(command)) {
			Print.response("Goodbye!");
			this.setMode(Mode.END);
		}
		else {
			Print.invalid(command);
			Print.waiting();
		}
	}
	
	public void serverCommand(String command) {
		if(isExit(command)) {
			this.setMode(Mode.MAIN);
		}
		else if(isStart(command) || isSort(command)) {
			// Start sorting
			ControllerManager.Instance().getServer().connectToClients();
		}
		else if(isStop(command)) {
			// Stop sorting
		}
		else if(isView(command)) {
			// View clients
		}
		else if(isBroadcast(command)) {
			// Broadcast
			ControllerManager.Instance().getServer().broadcast();
		}
		else {
			Print.invalid(command);
			Print.waiting();
		}
		this.consumeCommand();
	}
	
	
	public void clientCommand(String command) {
		System.out.println("Client Command");
		if(isStart(command)) {
			// Start client
			ControllerManager.Instance().getClient().start();
		}
		else if(isSend(command)) {
			System.out.println("Entered send command");
			String message = command.replaceFirst("send ", "");
			ControllerManager.Instance().getClient().send(message);
		}
		else if(isView(command)) {
			// View server
			Print.clientConnected(ControllerManager.Instance().getClient().getServerIP().toString());
		}
		else if(isStop(command)) {
			// Stop client
			System.out.println("Stop called but did nothing.");
		}
		else if(isExit(command)) {
			this.setMode(Mode.MAIN);
		}
		else {
			Print.invalid(command);
			Print.waiting();
		}
		this.consumeCommand();
	}
	
	public boolean isSend(String command) {
		String lowCommand = command.toLowerCase();
		if( lowCommand.contains("send")) {
			return true;
		}
		else {
			return false;
		}
	}
	
	public boolean isStart(String command) {
		String lowCommand = command.toLowerCase();
		if( lowCommand.contains("start") ||
			lowCommand.contains("begin") ||
			lowCommand.contains("go")) {
			return true;
		}
		else {
			return false;
		}
	}
	
	public boolean isView(String command) {
		String lowCommand = command.toLowerCase();
		if(lowCommand.contains("view")) {
			return true;
		}
		else {
			return false;
		}
	}
	public boolean isSort(String command) {
		String lowCommand = command.toLowerCase();
		if(lowCommand.contains("sort")) {
			return true;
		}
		else {
			return false;
		}
	}
	public boolean isBroadcast(String command) {
		String lowCommand = command.toLowerCase();
		if(lowCommand.contains("broadcast")) {
			return true;
		}
		else {
			return false;
		}
	}
	public boolean isStop(String command) {
		String lowCommand = command.toLowerCase();
		if( lowCommand.contains("stop") ||
			lowCommand.contains("wait")) {
			return true;
		}
		else {
			return false;
		}
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
