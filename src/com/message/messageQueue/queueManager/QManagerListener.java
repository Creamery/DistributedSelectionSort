package com.message.messageQueue.queueManager;

import com.main.Info;
import com.message.messageQueue.SelectionInstruction;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;

/**
 * Waits for requests from the clients
 */
public class QManagerListener implements Runnable {

    private volatile boolean isListening;
    private QueueManager mngr;
    private DatagramSocket reqSocket;
    private DatagramPacket recentPck;

    public QManagerListener(QueueManager mngr) {
        this.mngr = mngr;
        try {
            this.reqSocket = new DatagramSocket(Info.REQUEST_PORT);
        } catch (SocketException e) { e.printStackTrace(); }
    }

    public void run() {
        isListening = true;
        while(isListening){
            System.out.println("Waiting for request.");
            String request = receiveRequest();
            if(request.contains("REQ")){
                System.out.println("obtained request from "+recentPck.getAddress());
                mngr.deliverInstruction(recentPck.getAddress());
                System.out.println("instructions sent to "+recentPck.getAddress());
            }else if(request.contains("LMIN:")){
                // Process the received local minimum
//                mngr.receiveSolution(extractFoundMin(request),extractOrigin(request));
                System.out.println("solution obtained from "+recentPck.getAddress());
                mngr.receiveSolution(extractFoundMin(request),recentPck.getAddress());
                System.out.println("instructions sent to "+recentPck.getAddress());
                // resend an instruction if there is still one
                mngr.deliverInstruction(recentPck.getAddress());
            }
        }
    }

    public void stop(){
        isListening = false;
    }

    private String receiveRequest(){
        byte[] buf = new byte[Info.UDP_PACKET_SIZE];
        recentPck = new DatagramPacket(buf,buf.length);
        try{
            reqSocket.receive(recentPck);
        } catch (IOException e){ e.printStackTrace(); }

        return new String(recentPck.getData()).trim();
    }

    private int extractFoundMin(String msg){
        String a = msg.substring(5);
        return Integer.parseInt(a);
    }

    private SelectionInstruction extractOrigin(String msg){
        String a = msg.substring(5);
        String[] t = a.split("-");
        return new SelectionInstruction(Integer.parseInt(t[1]),Integer.parseInt(t[2]));
    }
}
