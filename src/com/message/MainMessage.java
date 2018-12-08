package com.message;

import java.io.Serializable;
import java.util.ArrayList;

public class MainMessage implements Serializable {
	private static final long serialVersionUID = 1L;
	private String header = "";
	private String message;
	private ArrayList<Integer> sortList;
	private int startIndex;
	private int endIndex;
	
	private int minIndex;
	private int minValue;
	
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
	
	public void setIndices(int start, int end) {
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
	
}
