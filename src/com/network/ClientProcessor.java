package com.network;

import java.util.ArrayList;

public class ClientProcessor extends ProcessorConnector {

	@ Override
	public void process() {
		this.setRunning(true);
		
		this.run();
	}
	public void process(ArrayList<Integer> list, int start, int end) {
		this.setRunning(true);
		this.setSortList(list);
		this.setStartIndex(start);
		this.setEndIndex(end);
		this.run();
	}
	
	public void run() {
		System.out.println("SORTING");
		
		this.setMinimumIndex(this.getStartIndex());
		this.setMinimumValue(this.getSortList().get(this.getMinimumIndex()));
		
		// Find local minimum
		for(int i = this.getStartIndex()+1; i < this.getEndIndex(); i++) {
			System.out.println("Comparing "+this.getSortList().get(i)+" and "+this.getSortList().get(this.getMinimumIndex()));
			if(this.getSortList().get(i) < this.getSortList().get(this.getMinimumIndex())) {
				
				this.setMinimumIndex(i);
				this.setMinimumValue(this.getSortList().get(i));
				System.out.println("LESS. NEW VAL "+this.getMinimumIndex() +" and " + this.getMinimumValue());
			}
		}
		System.out.println("Min value is "+this.getMinimumValue());
		this.setRunning(false);
	}
}
