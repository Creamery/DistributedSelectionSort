package com.network;

import java.util.ArrayList;

public abstract class ProcessorConnector extends Thread{
	protected volatile boolean isRunning;
	protected volatile ArrayList<Integer> sortList;
	protected volatile int startIndex;
	protected volatile int endIndex;
	
	public void start(ArrayList<Integer> list, int start, int end) {
		this.setSortList(list);
		this.setStartIndex(start);
		this.setEndIndex(end);
	}
	
	
	public void setSortList(ArrayList<Integer> list) {
		this.sortList = list;
	}
	public ArrayList<Integer> getSortList() {
		return sortList;
	}
	public boolean isRunning() {
		return isRunning;
	}
	public void setRunning(boolean isRunning) {
		this.isRunning = isRunning;
	}
	public int getStartIndex() {
		return startIndex;
	}
	public void setStartIndex(int start) {
		this.startIndex = start;
	}
	public int getEndIndex() {
		return endIndex;
	}
	public void setEndIndex(int end) {
		this.endIndex = end;
	}
}
