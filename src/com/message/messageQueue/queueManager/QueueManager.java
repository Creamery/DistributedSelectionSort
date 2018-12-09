package com.message.messageQueue.queueManager;

import com.message.messageQueue.InProcessInfo;
import com.message.messageQueue.SelectionInstruction;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;

public class QueueManager {

    private Queue<SelectionInstruction> instructionQ;
    private ArrayList<InProcessInfo> inProcessList;
    private boolean isFinished;
    private Object monitor;


    public QueueManager(){
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
    public boolean deliverInstruction(String consumerIP){
        synchronized (monitor) {
            if (instructionQ.isEmpty()) {
                // TODO: Send the client a message that tells them to `wait` for a `READY` message
                return false;
            } else {
                SelectionInstruction toDeliver = instructionQ.poll();
                // TODO: Send `toDeliver` to consumerIP

                inProcessList.add(new InProcessInfo(toDeliver, consumerIP, this));

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

    public void timeout(InProcessInfo timedOut){
        removeInProcessInfo(timedOut);
        instructionQ.add(timedOut.getInstruction());
    }

    private void removeInProcessInfo(InProcessInfo toRemove){
        toRemove.clearConsumer();
        inProcessList.remove(toRemove);

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
