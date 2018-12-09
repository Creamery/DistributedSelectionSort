package com.SelectionSort;

import com.message.messageQueue.queueManager.QManagerListener;
import com.message.messageQueue.queueManager.QueueManager;
import com.message.messageQueue.SelectionInstruction;

import java.util.ArrayList;

public class SelectionBase {

    private QueueManager qManager;
    private Thread clientListener;
    private ArrayList<Integer> toSort;
    private int splitCount;

    public SelectionBase(ArrayList<Integer> toSort, int numPartitions){
        qManager = new QueueManager();
        clientListener = new Thread(new QManagerListener(qManager));
        this.toSort = toSort;
        this.splitCount = numPartitions;
    }

    public void RunSorting(){
        int size = toSort.size();
        clientListener.start();
        for(int i=0; i<size; i++){
            int min_index = i;

            qManager.addInstructions(getInstructions(i));
            clientListener.notify();
            //TODO: Send 'READY' message to clients


            // Wait until the new instructions has been reinitialized
            try {
                clientListener.wait();
            } catch (InterruptedException e){
                e.printStackTrace();
            }
        }
    }

    /**
     * Returns a list of selection instructions for the consumers to process.
     * @param startIndex the startIndex for the current iteration
     * @return list of SelectionInstruction
     */
    private SelectionInstruction[] getInstructions(int startIndex){
        SelectionInstruction[] siList = new SelectionInstruction[splitCount];
        // Find the indeces
        double rawSizePerPartition = (double) (toSort.size() - startIndex) / (double) splitCount;
        boolean isExact = rawSizePerPartition % 1 == 0 ? true : false;
        int sizePerPartition = (int)Math.floor(rawSizePerPartition);
        int curIndex = startIndex;
        int nextIndex = curIndex;
        for(int t = 0; t < splitCount; t++){
            nextIndex += sizePerPartition;
            if(!isExact && t == splitCount -1)
                nextIndex +=1;

            siList[t] = new SelectionInstruction(curIndex, nextIndex);
            curIndex = nextIndex;
        }

        return siList;
    }
}
