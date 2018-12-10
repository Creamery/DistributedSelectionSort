package com.SelectionSort;

import com.main.Info;
import com.message.messageQueue.SelectionInstruction;
import com.message.messageQueue.queueManager.QueueManager;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;

public class SelectionServerRunnable implements Runnable {

    private QueueManager qManager;
    private ArrayList<Integer> toSelect;
    private InetAddress own;
    private volatile boolean isRunning;
    private volatile boolean blockReady;

    public SelectionServerRunnable(QueueManager qManager, ArrayList<Integer> toSelect){
        this.qManager = qManager;
        this.toSelect = toSelect;
        try {
            this.own = InetAddress.getByName(Info.NETWORK);

            System.out.println("own ip: "+own);
        } catch (UnknownHostException e){ e.printStackTrace(); }
    }

    @Override
    public void run() {
        isRunning = true;
        while(isRunning){
            SelectionInstruction si = qManager.obtainInstructionLocal();
            if(si == null){
                blockReady = true;
                System.out.println("Server client runnable: blocking until ready...");
                // wait until block ready is set false
                while(blockReady && isRunning);
                System.out.println("Server client runnable: unblocked");
            }
            else{
                int localMin = findMin(si);
                System.out.println("Server client runnable: found minimum...");
                qManager.receiveSolution(localMin, own);
            }
        }
    }

    public void setRunning(boolean val){
        this.isRunning = val;
    }

    public void swap(int a, int b){
        System.out.println("Server client runnable: swapping "+a+"-"+b);
        int x = toSelect.get(a);
        toSelect.set(a,toSelect.get(b));
        toSelect.set(b,x);
        unBlockReady();
    }

    public void unBlockReady(){
        this.blockReady = false;
    }

    private int findMin(SelectionInstruction si){
        int a = si.getStartIndex();
        int b = si.getEndIndex();

        int localMin = a;
        for(int i = a+ 1; i < b; i++) {
            if(toSelect.get(i) < toSelect.get(localMin)) {
                localMin = i;
            }
        }

        return localMin;
    }
}
