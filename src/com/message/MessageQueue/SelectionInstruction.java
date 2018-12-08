package com.message.MessageQueue;

import java.io.Serializable;

public class SelectionInstruction implements Serializable {
    private int startIndex;
    private int endIndex;

    public SelectionInstruction(int startIndex, int endIndex, QueueManager manager){
        this.startIndex = startIndex;
        this.endIndex = endIndex;
    }

    public int GetStartIndex(){
        return this.startIndex;
    }

    public int GetEndIndex(){
        return this.endIndex;
    }

}
