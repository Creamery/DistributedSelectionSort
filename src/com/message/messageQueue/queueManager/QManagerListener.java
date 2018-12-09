package com.message.messageQueue.queueManager;

/**
 * Waits for requests from the clients
 */
public class QManagerListener implements Runnable {

    private QueueManager mngr;

    public QManagerListener(QueueManager mngr) {
        this.mngr = mngr;
    }

    public void run(){
        //TODO: wait for client's messages, and invoke the QueueManager's Methods (i.e. deliverInstruction)
    }
}
