package com.SelectionSort;

import com.message.messageQueue.queueManager.QManagerListener;
import com.message.messageQueue.queueManager.QueueManager;
import com.message.messageQueue.SelectionInstruction;
import com.network.MainServer;

import java.util.ArrayList;

public class SelectionSort_UDP {

    private QueueManager qManager;
    private Thread clientListener;
    private ArrayList<Integer> toSort;
    private int splitCount;
    private MainServer parent;

    public SelectionSort_UDP(ArrayList<Integer> toSort, int numPartitions, MainServer parent){
        qManager = new QueueManager();
        clientListener = new Thread(new QManagerListener(qManager));
        this.toSort = toSort;
        this.splitCount = numPartitions;
        this.parent = parent;
    }

    public void runSorting(){
        int size = toSort.size();
        clientListener.start();
        for(int i=0; i<size; i++){
            int min_index = i;

            qManager.addInstructions(getInstructions(i));
            clientListener.notify();
            //TODO: Send 'READY' message to clients



            // TODO: tell clients to resynchronize list by sending SwapInstruction
            // TODO: then tell them to wait until the 'READY' message has been sent by the server
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
