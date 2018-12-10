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

public class QueueManager {

    private Queue<SelectionInstruction> instructionQ;
    private ArrayList<InProcessInfo> inProcessList;
    private SelectionSort_UDP parent;
    private volatile boolean isFinished;
    private Object monitor;

    public QueueManager(SelectionSort_UDP parent){
        this.parent = parent;
        monitor = new Object();
        this.instructionQ = new LinkedList<>();
        this.inProcessList = new ArrayList<>();
    }

    /**
     * Delivers the instruction to the requester client.
     * If the message queue is empty, a message that tells the client to wait is sent instead.
     * @param consumerIP the IP address of the instruction to be delivered to.
     * @return true if delivery is successful, false if otherwise.
     */
    public boolean deliverInstruction(InetAddress consumerIP){
        synchronized (monitor) {
            if (instructionQ.isEmpty()) {
                parent.getServer().sendToClient(consumerIP,"EMPTY");
                return false;
            } else {
                SelectionInstruction toDeliver = instructionQ.poll();
                parent.getServer().sendToClient(consumerIP,toDeliver.toString());

                inProcessList.add(new InProcessInfo(toDeliver, consumerIP.toString(), this));

                return true;
            }
        }
    }

    public SelectionInstruction obtainInstructionLocal(String consumerIP){
        synchronized (monitor){
            if(instructionQ.isEmpty())
                return null;
            else{
                SelectionInstruction local = instructionQ.poll();

                inProcessList.add(new InProcessInfo(local, consumerIP, this));
                return local;
            }
        }
    }

    public void receiveSolution(int foundMin, SelectionInstruction origin){
        parent.compareAndSetMin(foundMin);
        removeInProcessInfo(origin);
    }

    public void receiveSolution(int foundMin, InetAddress sender){
        parent.compareAndSetMin(foundMin);
        removeInProcessInfo(sender);
    }


    public void timeout(InProcessInfo timedOut){
        removeInProcessInfo(timedOut);
        instructionQ.add(timedOut.getInstruction());
    }

    private void removeInProcessInfo(InetAddress toRemove){
        inProcessList.removeIf(obj -> obj.getConsumerIP().equals(toRemove));

        isFinished = inProcessList.isEmpty() && instructionQ.isEmpty();
    }

    private void removeInProcessInfo(SelectionInstruction toRemove){
        int a = toRemove.getStartIndex();
        int b = toRemove.getEndIndex();
        inProcessList.removeIf(obj -> obj.getInstruction().getStartIndex() == a
                && obj.getInstruction().getEndIndex() == b);

        isFinished = inProcessList.isEmpty() && instructionQ.isEmpty();
    }

    private void removeInProcessInfo(InProcessInfo toRemove){
        toRemove.clearConsumer();
        int a = toRemove.getInstruction().getStartIndex();
        int b = toRemove.getInstruction().getEndIndex();
        inProcessList.removeIf(obj -> obj.getInstruction().getStartIndex() == a
                && obj.getInstruction().getEndIndex() == b);

        isFinished = inProcessList.isEmpty() && instructionQ.isEmpty();
    }

    public void addInstructions(SelectionInstruction[] instructions){
        for(SelectionInstruction si : instructions)
            this.instructionQ.add(si);
        isFinished = false;
    }

    public boolean isFinished(){
        return this.isFinished;
    }
}
