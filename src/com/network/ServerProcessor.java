package com.network;

import java.util.ArrayList;

public class ServerProcessor extends ProcessorConnector {
	private int currentIndex = 0;
	private boolean isDone = false;
	
	
	public ServerProcessor() {
		this.setSortList(this.generateList());
	}

	public ArrayList<Integer> generateList() {
		ArrayList<Integer> list = new ArrayList<Integer>();
		list.add(3);
		list.add(1);
		list.add(2);
		return list;
	}
	
	public void next() {
		this.currentIndex += 1;
		if(this.currentIndex == this.getSortList().size()) { // TODO change to index size
			this.setDone(true);
		}
	}
	
	@ Override
	public void process() {
		this.setRunning(true);
		this.run();
	}
	
	public void swap(int index1, int index2) {
		int value1 = this.getSortList().get(index1);
		
		this.getSortList().set(index1, this.getSortList().get(index2));
		this.getSortList().set(index2, value1);
	}
	
	// Compute indices for N clients
	public ArrayList<ProcessorIndices> computeIndices(int clients) {
		ArrayList<ProcessorIndices> indices = new ArrayList<ProcessorIndices>();

		for(int i = 0; i < clients; i++) {
			indices.add(new ProcessorIndices(i, i+1));
		}
		
		return indices;
	}
	public void run() {
		while(this.isRunning()) {
			
		}
	}
	
	public boolean isDone() {
		return this.isDone;
	}
	
	public void setDone(boolean isDone) {
		this.isDone = isDone;
	}

	public int getCurrentIndex() {
		return currentIndex;
	}

	public void setCurrentIndex(int currentIndex) {
		this.currentIndex = currentIndex;
	}
}
