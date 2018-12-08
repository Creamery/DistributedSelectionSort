package com.network;

public class ClientProcessor extends ProcessorConnector {

	@ Override
	public void process() {
		this.resetMinimum();
		this.setRunning(true);
		
		this.run();
	}
	
	public void run() {
		System.out.println("SORTING");
		
		// TODO Remove
		this.setRunning(false);
		this.setMinimumIndex(1);
		this.setMinimumValue(7);
		
		
		while(this.isRunning()) {
			// Add sorting here
			
		}
	}
}
