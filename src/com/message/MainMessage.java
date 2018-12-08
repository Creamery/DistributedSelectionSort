package com.message;

import java.io.Serializable;
import java.util.ArrayList;

public class MainMessage implements Serializable {
	private static final long serialVersionUID = 1L;
	private String message;
	private ArrayList<Integer> sortList;
	private int startIndex;
	private int endIndex;
	
	// Call this if you want to reuse the MainMessage
	public void reset() {
		this.message = "";
		this.sortList = null;
		this.startIndex = -1;
		this.endIndex = -1;
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

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public ArrayList<Integer> getSortList() {
		return sortList;
	}

	public void setSortList(ArrayList<Integer> sortList) {
		this.sortList = sortList;
	}
	
}
