package com.message.messageQueue;

import com.main.Info;
import com.message.messageQueue.Timeout.TimeoutTask;
import com.message.messageQueue.queueManager.QueueManager;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Timer;

public class InProcessInfo {
    SelectionInstruction instruction;

    private String consumerIP;
    Timer timer;

    private QueueManager manager;

    public InProcessInfo(SelectionInstruction instruction, String consumerIP, QueueManager manager){
        this.instruction = instruction;
        this.manager = manager;
        this.setConsumer(consumerIP);
    }

    public void setConsumer(String consumerIP) {
        this.consumerIP = consumerIP;
        this.timer = new Timer();
        this.timer.schedule(new TimeoutTask(manager,this), Info.TIMEOUT_DELAY);
    }

    /**
     * Called when the following events occur:
     *  - Timeout occurred
     *  - Task is successfully completed
     *  This is to be called before instance is removed from InProcess List of QueueManager
     */
    public void clearConsumer(){
        this.timer.cancel();
        this.timer.purge();
        this.timer = null;
        this.consumerIP = null;
    }

    public String getConsumer(){
        return this.consumerIP;
    }

    public InetAddress getConsumerIP(){
        try {
            return InetAddress.getByName(this.consumerIP.substring(1));
        }catch (UnknownHostException e){
            e.printStackTrace();;
        }
        return null;
    }

    public SelectionInstruction getInstruction(){
        return this.instruction;
    }
}
