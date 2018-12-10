package com.message.messageQueue.queueManager;

import com.SelectionSort.SelectionSort_UDP;
import com.main.Info;
import com.message.messageQueue.InProcessInfo;
import com.message.messageQueue.SelectionInstruction;
import com.network.MainServer;
import com.reusables.General;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

public class QueueManager {

    private Queue<SelectionInstruction> instructionQ;
    private ArrayList<InProcessInfo> inProcessList;
    private SelectionSort_UDP parent;
    private volatile int instructionCount;

    public QueueManager(SelectionSort_UDP parent){
        this.parent = parent;
        this.instructionQ = new ConcurrentLinkedQueue<>();
        this.inProcessList = new ArrayList<>();
    }

    /**
     * Delivers the instruction to the requester client.
     * If the message queue is empty, a message that tells the client to wait is sent instead.
     * @param consumerIP the IP address of the instruction to be delivered to.
     * @return true if delivery is successful, false if otherwise.
     */
    public synchronized boolean deliverInstruction(InetAddress consumerIP){
        if (instructionCount < 1) {
            parent.getServer().sendToClient(consumerIP,"EMPTY");
            return false;
        } else {
            SelectionInstruction toDeliver = instructionQ.poll();
            instructionCount--;
            System.out.println("Instructions left: "+instructionCount);
            parent.getServer().sendToClient(consumerIP,toDeliver.toString());
            inProcessList.add(new InProcessInfo(toDeliver, consumerIP.toString(), this));

            return true;
        }
    }

    public synchronized SelectionInstruction obtainInstructionLocal(){
        if(instructionCount < 1)
            return null;
        else{
            SelectionInstruction local = instructionQ.poll();
            instructionCount--;
            System.out.println(Thread.currentThread().getName()+" - LOCAL||Instructions left: "+instructionCount);
            System.out.println("Server-Side:: Obtained:"+ local.toString());
            inProcessList.add(new InProcessInfo(local, Info.NETWORK, this));
            return local;
        }
    }

//    public void receiveSolution(int foundMin, SelectionInstruction origin){
//        parent.compareAndSetMin(foundMin);
//        removeInProcessInfo(origin);
//    }

    public synchronized void receiveSolution(int foundMin, InetAddress sender){
        parent.compareAndSetMin(foundMin);
        removeInProcessInfo(sender);
    }

    public synchronized void receiveLocalSolution(int foundMin, SelectionInstruction si){
        parent.compareAndSetMin(foundMin);
        removeInProcessInfo(si);
    }

    public synchronized void timeout(InProcessInfo timedOut){
        removeInProcessInfo(timedOut.getConsumerIP());
        instructionQ.add(timedOut.getInstruction());
        instructionCount++;
        parent.incrementLeft();
    }

    private synchronized void removeInProcessInfo(InetAddress toRemove){

        InProcessInfo removed = inProcessList.stream()
            .filter(obj -> obj.getConsumerIP().equals(toRemove))
            .findFirst()
            .get();
        removed.clearConsumer();
        inProcessList.remove(removed);
        if(removed != null)
            this.parent.decrementLeft();
    }

    private synchronized void removeInProcessInfo(SelectionInstruction toRemove){
        int a = toRemove.getStartIndex();
        int b = toRemove.getEndIndex();

        InProcessInfo removed = inProcessList.stream()
                .filter(obj -> obj.getInstruction().getStartIndex() == a && obj.getInstruction().getEndIndex() == b)
                .findFirst()
                .get();
        removed.clearConsumer();
        inProcessList.remove(removed);
        if(removed != null)
            this.parent.decrementLeft();
    }
//
//    private void removeInProcessInfo(InProcessInfo toRemove){
//        toRemove.clearConsumer();
//        int a = toRemove.getInstruction().getStartIndex();
//        int b = toRemove.getInstruction().getEndIndex();
//        inProcessList.removeIf(obj -> obj.getInstruction().getStartIndex() == a
//                && obj.getInstruction().getEndIndex() == b);
//
//        isFinished = inProcessList.isEmpty() && instructionQ.isEmpty();
//        if(isFinished)
//            parent.shouldContinue = true;
//
//    }

//    private synchronized void removeInProcessInfo(SelectionInstruction toRemove){
//        int a = toRemove.getStartIndex();
//        int b = toRemove.getEndIndex();
//        inProcessList.removeIf(obj -> obj.getInstruction().getStartIndex() == a
//                && obj.getInstruction().getEndIndex() == b);
//
//        isFinished = inProcessList.isEmpty() && instructionQ.isEmpty();
//    }
//
//    private synchronized void removeInProcessInfo(InProcessInfo toRemove){
//        toRemove.clearConsumer();
//        int a = toRemove.getInstruction().getStartIndex();
//        int b = toRemove.getInstruction().getEndIndex();
//        inProcessList.removeIf(obj -> obj.getInstruction().getStartIndex() == a
//                && obj.getInstruction().getEndIndex() == b);
//
//        isFinished = inProcessList.isEmpty() && instructionQ.isEmpty();
//    }

    public synchronized void addInstructions(SelectionInstruction[] instructions){
        for(SelectionInstruction si : instructions) {
            this.instructionQ.add(si);
        }
        parent.setLeft(instructions.length);
        instructionCount = instructions.length;
    }

    public synchronized void clearQueue(){
        this.instructionQ.clear();
    }

}
