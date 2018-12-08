package com.network;

import java.util.ArrayList;

public class ClientProcessor extends ProcessorConnector {


	@ Override
	public void start(ArrayList<Integer> list, int start, int end) {
		super.start(list, start, end);
		// TODO: process
	}
	
	// Initialize before running
	public void process() {
		this.setRunning(true);
		this.run();
	}
	
	public void run() {
		while(this.isRunning()) {
			// Add sorting here
		}
	}
}
