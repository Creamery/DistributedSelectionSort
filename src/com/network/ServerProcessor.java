package com.network;

import java.util.ArrayList;

import com.reusables.CsvParser;


public class ServerProcessor extends ProcessorConnector {
	private static final String generated_name_prefix = "unsorted_generated_";
	private static final int _100 = 100;
	private static final String filename = generated_name_prefix+_100+".csv";
	
	
	private int currentIndex = 0;
	private boolean isDone = false;
	private int splitCount;
	
	public ServerProcessor(int split) {
		this.setSortList(this.generateList());
		this.setSplitCount(split);
	}

	// Generate list as specified by CSVParser
	public ArrayList<Integer> generateList() {
		ArrayList<Integer> list = CsvParser.read(filename);
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
	public ArrayList<ProcessorIndices> computeIndices() {
		ArrayList<ProcessorIndices> indices = new ArrayList<ProcessorIndices>();
		int size = (int)Math.floor(((double)this.getSortList().size()-(double)this.getCurrentIndex())/(double)this.getSplitCount());
		int index = this.getCurrentIndex();
		
		int sIndex;
		int eIndex;
		for(int i = 0; i < this.getSplitCount(); i++) {
			sIndex = index;
			eIndex = index+size;
			indices.add(new ProcessorIndices(sIndex, eIndex));
			
			index += size;
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
