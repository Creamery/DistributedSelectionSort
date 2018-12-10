package com.SelectionSort;

import com.message.messageQueue.SelectionInstruction;

import java.util.ArrayList;

public class SelectionClient_UDP {

    public static int runSelection(ArrayList<Integer> toSelect, SelectionInstruction instruction){
        int startIndex = instruction.getStartIndex();
        int endIndex = instruction.getEndIndex();

        int localMin = startIndex;
        for(int i = startIndex + 1; i < endIndex; i++) {
            if(toSelect.get(i) < toSelect.get(localMin)) {
                localMin = i;
            }
        }

        return localMin;
    }
}
