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
		
		this.setMinimumIndex(this.getStartIndex());
		
		// Find local minimum
		for(int i = this.getStartIndex()+1; i < this.getEndIndex(); i++) {
			if(this.getSortList().get(i) < this.getSortList().get(this.getMinimumIndex())) {
				
				this.setMinimumIndex(i);
				this.setMinimumValue(this.getSortList().get(i));
			}
		}
		System.out.println("Min value is "+this.getMinimumValue());
		this.setRunning(false);
	}
}
