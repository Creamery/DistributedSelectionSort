package com.network;

public class ClientProcessor extends ProcessorConnector {

	@ Override
	public void process() {
		this.setRunning(true);
		this.run();
	}
	
	public void process(int start, int end) {
		this.setRunning(true);
		this.setStartIndex(start);
		this.setEndIndex(end);
		this.run();
	}
	
	public void printList() {
		for(int i = 0; i < this.getSortList().size(); i++) {
			System.out.print(this.getSortList().get(i)+" ");
		}
	}
	
	public void run() {
		System.out.println("SORTING");
		
		this.setMinimumIndex(this.getStartIndex());
		this.setMinimumValue(this.getSortList().get(this.getMinimumIndex()));

		// System.out.println("New sort sList["+this.getSortList().size()+"] "+this.getSortList().get(this.getSortList().size()-1));
		printList();
		// Find local minimum
		for(int i = this.getStartIndex()+1; i < this.getEndIndex(); i++) {
			System.out.println("Comparing "+getSortList().get(i)+" and "+getSortList().get(this.getMinimumIndex()));
			
			if(this.getSortList().get(i) < getSortList().get(this.getMinimumIndex())) {
				
				this.setMinimumIndex(i);
				this.setMinimumValue(this.getSortList().get(i));
				System.out.println("LESS. NEW VAL "+this.getMinimumIndex() +" and " + this.getMinimumValue());
			}
		}
		System.out.println("Min value is "+this.getMinimumValue());
		this.setRunning(false);
	}
}
