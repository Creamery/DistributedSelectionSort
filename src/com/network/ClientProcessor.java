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

	
	public void updateIndices() {
		this.startIndex = this.getStartIndex()+1;
		this.endIndex = this.getEndIndex()+1;
		
		if(this.getEndIndex() > this.getSortList().size()) {
			this.endIndex = this.getSortList().size();
		}
		if(this.startIndex > this.endIndex) {
			this.startIndex = this.endIndex;
		}
	}
	
	public void run() {
		this.setMinimumIndex(this.getStartIndex());
		this.setMinimumValue(this.getSortList().get(this.getMinimumIndex()));

		// Find local minimum
		for(int i = this.getStartIndex()+1; i < this.getEndIndex(); i++) {
			if(this.getSortList().get(i) < getSortList().get(this.getMinimumIndex())) {
				this.setMinimumIndex(i);
				this.setMinimumValue(getSortList().get(i));
			}
		}
		this.setRunning(false);
	}
}
