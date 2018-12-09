package com.network;

import java.util.ArrayList;

import com.reusables.CsvParser;


public class ServerProcessor extends ProcessorConnector {
	private static final String generated_name_prefix = "unsorted_generated_";
	private static final int _100 = 100;
	private static final String filename = generated_name_prefix+_100+".csv";
	
	
	private volatile int currentIndex = 0;
	private volatile boolean isDone = false;
	private int splitCount;
	
	public ServerProcessor(int split) {
		setSortList(this.generateList());
		this.setSplitCount(split);
	}

	// Generate list as specified by CSVParser
	public ArrayList<Integer> generateList() {
		ArrayList<Integer> list = CsvParser.read(filename);
		return list;
	}
	
	public void next() {
		this.currentIndex += 1;
		System.out.println("CurrIndex "+currentIndex);
		if(this.currentIndex == getSortList().size()-1) { // TODO change to index size
			this.setDone(true);
		}
	}
	public void printList() {
		for(int i = 0; i < getSortList().size(); i++) {
			System.out.print(getSortList().get(i)+" ");
		}
	}
	@ Override
	public void process() {
		this.setRunning(true);
		this.run();
	}
	
	public void swap(int minIndex) {
		int temp = getSortList().get(this.getCurrentIndex());
		this.getSortList().set(this.getCurrentIndex(), this.getSortList().get(minIndex));
		this.getSortList().set(minIndex, temp);
	}
	
	// Compute indices for N clients
	public ArrayList<ProcessorIndices> computeIndices() {
		ArrayList<ProcessorIndices> indices = new ArrayList<ProcessorIndices>();
		int size = (int)Math.floor(((double)getSortList().size()-(double)this.getCurrentIndex())/(double)this.getSplitCount());
		int index = this.getCurrentIndex();
		
		System.out.println("Size is "+size);
	
		int sIndex = index;
		int eIndex = sIndex+size;
		for(int i = 0; i < this.getSplitCount(); i++) {
			
			indices.add(new ProcessorIndices(sIndex, eIndex));
			
			sIndex = eIndex;
			eIndex = sIndex + size;
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

	public int getSplitCount() {
		return splitCount;
	}

	public void setSplitCount(int splitCount) {
		this.splitCount = splitCount;
	}
}
