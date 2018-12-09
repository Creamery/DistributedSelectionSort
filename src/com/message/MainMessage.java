package com.message;

import java.io.Serializable;
import java.util.ArrayList;

public class MainMessage implements Serializable {
	private static final long serialVersionUID = 1L;
	private volatile PacketType packetHeader;
	private volatile String header = "";
	private volatile String message;
	private volatile ArrayList<Integer> sortList;
	private volatile int startIndex;
	private volatile int endIndex;
	
	private volatile int minIndex;
	private volatile int minValue;

	private volatile int clientIndex;

	private volatile int swapIndex1;
	private volatile int swapIndex2;

	// Call this if you want to reuse the MainMessage
	public void reset() {
		this.header = "";
		this.message = "";
		this.sortList = null;
		this.startIndex = -1;
		this.endIndex = -1;
	}
	
	public void setMinimumValues(int index, int value) {
		this.setMinIndex(index);
		this.setMinValue(value);
	}
	
	public void updateList() {
		this.setStartIndex(this.getStartIndex()+1);
		this.setEndIndex(this.getEndIndex()+1);
		System.out.println("sIndex = "+this.getStartIndex()+" "+this.getEndIndex());
	}
	
	public void setIndices(ArrayList<Integer> list, int start, int end) {
		this.setSortList(list);
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
		if(this.getSortList() != null && endIndex > this.getSortList().size()) {
			endIndex = this.getSortList().size();
		}
		this.endIndex = endIndex;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public ArrayList<Integer> getSortList() {
		return sortList;
	}

	public void setSortList(ArrayList<Integer> list) {
		this.sortList = new ArrayList<Integer>();

		for(int i = 0; i < list.size(); i++) {
			this.sortList.add(list.get(i));
		}
	}

	public int getMinIndex() {
		return minIndex;
	}

	public void setMinIndex(int minIndex) {
		this.minIndex = minIndex;
	}

	public int getMinValue() {
		return minValue;
	}

	public void setMinValue(int minValue) {
		this.minValue = minValue;
	}

	public String getHeader() {
		return header;
	}

	public void setHeader(String header) {
		this.header = header;
	}

	public int getClientIndex() {
		return clientIndex;
	}

	public void setClientIndex(int clientIndex) {
		this.clientIndex = clientIndex;
	}

	public int getSwapIndex1() {
		return swapIndex1;
	}

	public void setSwapIndex1(int swapIndex1) {
		this.swapIndex1 = swapIndex1;
	}

	public int getSwapIndex2() {
		return swapIndex2;
	}

	public void setSwapIndex2(int swapIndex2) {
		this.swapIndex2 = swapIndex2;
	}

	public PacketType getPacketHeader() {
		return packetHeader;
	}

	public void setPacketHeader(PacketType packetHeader) {
		this.packetHeader = packetHeader;
	}
	
}
