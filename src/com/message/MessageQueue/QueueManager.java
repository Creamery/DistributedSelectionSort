package com.message.MessageQueue;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;

public class QueueManager {

    private Queue<SelectionInstruction> instructionQ;
    private ArrayList<InProcessInfo> inProcessList;

    public QueueManager(){
        this.instructionQ = new LinkedList<>();
        this.inProcessList = new ArrayList<>();
    }

    /**
     *
     * @param consumerIP the IP address of the instruction to be delivered to.
     * @return true if delivery is successful, false if otherwise.
     */
    public synchronized boolean deliverInstruction(String consumerIP){
        if(instructionQ.isEmpty())
            return false;
        else {
            SelectionInstruction toDeliver = instructionQ.poll();
            // TODO: Send `toDeliver` to consumerIP

            inProcessList.add(new InProcessInfo(toDeliver, consumerIP, this));

            return true;
        }
    }

    public void timeout(InProcessInfo timedOut){
        removeInProcessInfo(timedOut);
        instructionQ.add(timedOut.getInstruction());
    }

    private void removeInProcessInfo(InProcessInfo toRemove){
        toRemove.clearConsumer();
        inProcessList.remove(toRemove);
    }
}
