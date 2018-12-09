package com.network;

public class ProcessorIndices {

	private int startIndex;
	private int endIndex;
	
	public ProcessorIndices(int start, int end) {
		this.setStartIndex(start);
		this.setEndIndex(end);
	}
	
	public int getStartIndex() {
		return startIndex;
	}
	public void setStartIndex(int startIndex) {
		this.startIndex = startIndex;
	}
	public int getEndIndex() {
		return endIndex;
	}
	public void setEndIndex(int endIndex) {
		this.endIndex = endIndex;
	}
	
}
