package com.network;

import java.util.ArrayList;

import com.message.MainMessage;

public abstract class ProcessorConnector extends Thread{
	protected volatile boolean isRunning;
	protected volatile ArrayList<Integer> sortList;
	protected volatile int startIndex;
	protected volatile int endIndex;
	
	protected volatile int minimumValue = -1;
	protected volatile int minimumIndex = -1;
	

	public abstract void process();

	public void resetMinimum() {
		this.setMinimumIndex(-1);
		this.setMinimumValue(-1);
	}
	
	public void setIndices(MainMessage message) {
		this.startIndex = message.getStartIndex();
		this.endIndex = message.getEndIndex();
	}
	
	public void printList(ArrayList<Integer> list) {
		if(list != null) {
			for(int i = 0; i < list.size(); i++) {
				System.out.print(list.get(i)+" ");
			}
		}
	}
	
	public void setSortList(ArrayList<Integer> list) {
		if(this.getSortList() != null) {
			System.out.print("bef ");
			printList(this.getSortList());
		}
		System.out.print("new ");
		printList(list);
		
		this.sortList = new ArrayList<Integer>();
		for(int i = 0; i < list.size(); i++) {
			this.sortList.add(list.get(i));
		}
		
		System.out.print("aft ");
		printList(this.getSortList());
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


	public int getMinimumValue() {
		return minimumValue;
	}


	public void setMinimumValue(int minimumValue) {
		this.minimumValue = minimumValue;
	}


	public int getMinimumIndex() {
		return minimumIndex;
	}


	public void setMinimumIndex(int minimumIndex) {
		this.minimumIndex = minimumIndex;
	}
}
