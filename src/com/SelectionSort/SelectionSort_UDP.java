package com.SelectionSort;

import com.main.Info;
import com.message.NotifySwapMessage;
import com.message.messageQueue.queueManager.QManagerListener;
import com.message.messageQueue.queueManager.QueueManager;
import com.message.messageQueue.SelectionInstruction;
import com.network.MainServer;
import com.reusables.Stopwatch;

import java.time.temporal.IsoFields;
import java.util.ArrayList;

public class SelectionSort_UDP {

    private Thread serverClientThread;
    private SelectionServerRunnable serverClientRunnable;

    private QueueManager qManager;
    private Thread clientRequestListener;
    private QManagerListener qRunnable;
    private volatile ArrayList<Integer> toSort;
    private volatile int curMin;
    public volatile boolean shouldContinue;
    private int splitCount;
    private MainServer parent;

    public SelectionSort_UDP(ArrayList<Integer> toSort, MainServer parent){
        this.parent = parent;
        qManager = new QueueManager(this);
        qRunnable = new QManagerListener(qManager);
        clientRequestListener = new Thread(qRunnable);
        this.toSort = toSort;
        if(Info.ENABLE_SERVER_RUNNABLE){
            serverClientRunnable = new SelectionServerRunnable(qManager,toSort);
            serverClientThread = new Thread(serverClientRunnable);
        }
    }

    public ArrayList<Integer> runSorting(int numPartitions){
        Stopwatch.start("runSorting");
        this.splitCount = numPartitions;
        int size = toSort.size();
        clientRequestListener.start();

        if(Info.ENABLE_SERVER_RUNNABLE)
            serverClientThread.start();

        for(int i=0; i<size; i++){
            curMin = i;
            System.out.println("Reloading instructions");
            qManager.addInstructions(getInstructions(i));
//            clientRequestListener.notify();
            shouldContinue = false;
            parent.sendAllClients("READY");
            if(Info.ENABLE_SERVER_RUNNABLE)
                serverClientRunnable.unBlockReady();
            System.out.println("Instructions ready for consumption");

            //spin-lock
            while(!shouldContinue);

            System.out.println("QManager Finished");

            // Perform swap
            System.out.println("Performing Swap between: "+i+"-"+curMin);
            int a = toSort.get(i);
            toSort.set(i,toSort.get(curMin));
            toSort.set(curMin,a);
            getServer().sendAllClients(new NotifySwapMessage(i,curMin).toString());
            if(Info.ENABLE_SERVER_RUNNABLE)
                serverClientRunnable.swap(i,curMin);

        }
//        clientRequestListener.notify();
        getServer().sendAllClients("STOP");
        System.out.println("Stopping clients");
        if(Info.ENABLE_SERVER_RUNNABLE)
            serverClientRunnable.setRunning(false);
        qRunnable.stop();
        System.out.println("Sorting completed");

        Stopwatch.endAndPrint("runSorting");
        return toSort;
    }

    public void compareAndSetMin(int newMin){
        System.out.println("Comparing "+newMin+" with old:"+curMin);
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

            System.out.println("instruction range: "+curIndex+"-"+nextIndex);
            siList[t] = new SelectionInstruction(curIndex, nextIndex);
            curIndex = nextIndex;
        }
        System.out.println("Total of "+siList.length+" instructions were generated");
        return siList;
    }

    public MainServer getServer(){
        return this.parent;
    }
}
