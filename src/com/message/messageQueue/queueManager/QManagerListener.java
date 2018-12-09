package com.message.messageQueue.queueManager;

import java.net.DatagramPacket;
import java.net.DatagramSocket;

/**
 * Waits for requests from the clients
 */
public class QManagerListener implements Runnable {

    private boolean isListening;
    private QueueManager mngr;

    public QManagerListener(QueueManager mngr) {
        this.mngr = mngr;
    }

    public void run() {
        isListening = true;
        while(isListening){
            //TODO: wait for client's messages, and invoke the QueueManager's Methods (i.e. deliverInstruction)
//            DatagramSocket s;
//            DatagramPacket p;
//            s.receive(p);
//            String s = new String(new byte[256],)
        }
    }
}
