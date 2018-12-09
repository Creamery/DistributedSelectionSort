package com.test;

import com.main.Info;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.util.Scanner;

public class BroadcastClient extends Thread {
    public static int NumberOfClientsActive = 0;
    private int clientNo;
    private boolean running = false;
    private DatagramSocket socket;

    public BroadcastClient(){
        clientNo = ++NumberOfClientsActive;
        try {
            socket = new DatagramSocket(Info.BROADCAST_PORT);
            socket.setBroadcast(true);
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    public void run(){
        running = true;
        String input;
        Scanner sc = new Scanner(System.in);
        while(running) {
//            System.out.printf("write 'a' to continue...\n");
//            input = sc.nextLine();
            if (true) {
                byte[] buf = new byte[256];

                DatagramPacket packet = new DatagramPacket(buf, buf.length);
                try {
                    System.out.println("waiting for packet");
                    socket.receive(packet);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                System.out.println("waiting for packet");
                String message = new String(packet.getData()).trim();
                if (message != "") {
                    System.out.println("Client #" + clientNo + " received the message '" + message + "' from broadcast.");
                    if (message == "end")
                        abort();
                }
            }
        }

        socket.close();
    }

    public void abort(){
        running = false;
    }
}
