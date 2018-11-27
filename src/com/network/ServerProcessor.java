package com.network;

public class ServerProcessor extends Thread {
	private boolean isRunning;

	// Initialize before running
	public void process() {
		this.setRunning(true);
		this.run();
	}
	
	public void run() {
		while(this.isRunning()) {
			
		}
	}
	
	public boolean isRunning() {
		return isRunning;
	}
	
	public void setRunning(boolean isRunning) {
		this.isRunning = isRunning;
	}
}
