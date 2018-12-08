package com.network;

import java.util.ArrayList;

public class ServerProcessor extends ProcessorConnector {
	
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
	
	@ Override
	public void start(ArrayList<Integer> list, int start, int end) {
		super.start(list, start, end);
		// TODO: process
	}
	
	
	public void process() {
		this.setRunning(true);
		this.run();
	}
	public void run() {
		while(this.isRunning()) {
			
		}
	}
}
