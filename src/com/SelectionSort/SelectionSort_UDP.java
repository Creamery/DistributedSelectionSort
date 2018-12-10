package com.SelectionSort;

import com.message.NotifySwapMessage;
import com.message.messageQueue.queueManager.QManagerListener;
import com.message.messageQueue.queueManager.QueueManager;
import com.message.messageQueue.SelectionInstruction;
import com.network.MainServer;

import java.util.ArrayList;

public class SelectionSort_UDP {

    private QueueManager qManager;
    private Thread clientRequestListener;
    private QManagerListener qRunnable;
    private volatile ArrayList<Integer> toSort;
    private volatile int curMin;
    private int splitCount;
    private MainServer parent;

    public SelectionSort_UDP(ArrayList<Integer> toSort, int numPartitions, MainServer parent){
        this.parent = parent;
        qManager = new QueueManager(this);
        qRunnable = new QManagerListener(qManager);
        clientRequestListener = new Thread(qRunnable);
        this.toSort = toSort;
        this.splitCount = numPartitions;
    }

    public ArrayList<Integer> runSorting(){
        int size = toSort.size();
        clientRequestListener.start();
        for(int i=0; i<size; i++){
            curMin = i;
            System.out.println("Reloading instructions");
            qManager.addInstructions(getInstructions(i));
//            clientRequestListener.notify();
            parent.sendAllClients("READY");
            System.out.println("Instructions ready for consumption");
            //spin-lock
            while(!qManager.isFinished());
            System.out.println("Instructions empty");
            getServer().sendAllClients(new NotifySwapMessage(i,curMin).toString());
            // Wait until the new instructions has been reinitialized
//            try {
//                clientRequestListener.wait();
//            } catch (InterruptedException e){
//                e.printStackTrace();
//            }
        }
//        clientRequestListener.notify();
        getServer().sendAllClients("STOP");
        qRunnable.stop();
        System.out.println("Sorting completed");
        return toSort;
    }

    public void compareAndSetMin(int newMin){
        if(toSort.get(newMin) < toSort.get(curMin))
            curMin = newMin;
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

    public MainServer getServer(){
        return this.parent;
    }
}
