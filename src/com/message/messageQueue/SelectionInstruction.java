package com.message.messageQueue;

import java.io.Serializable;

public class SelectionInstruction implements Serializable {
    private int startIndex;
    private int endIndex;

    public SelectionInstruction(int startIndex, int endIndex){
        this.startIndex = startIndex;
        this.endIndex = endIndex;
    }

    public int getStartIndex(){
        return this.startIndex;
    }

    public int getEndIndex(){
        return this.endIndex;
    }

    public String toString(){
        return "INSTR:"+startIndex+"-"+endIndex;
    }

}