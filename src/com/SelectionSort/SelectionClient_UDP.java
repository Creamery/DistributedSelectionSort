package com.SelectionSort;

import com.message.messageQueue.SelectionInstruction;

import java.util.ArrayList;

public class SelectionClient_UDP {

    private ArrayList<Integer> toSelect;
    private SelectionInstruction instruction;

    public SelectionClient_UDP(ArrayList<Integer> toSelect, SelectionInstruction instruction){
        this.toSelect = toSelect;
        this.instruction = instruction;
    }

    public void runSelection(){
        int startIndex = instruction.getStartIndex();
        int endIndex = instruction.getEndIndex();

        int localMin = startIndex;
        for(int i = startIndex + 1; i < endIndex; i++) {
            if(toSelect.get(i) < toSelect.get(localMin)) {
                localMin = i;
            }
        }

        // TODO: notify server of found LocalMin


    }
}
