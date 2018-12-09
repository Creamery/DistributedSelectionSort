package com.test;

import com.main.Info;

import java.io.IOException;
import java.net.InetAddress;
import java.util.Scanner;

public class BroadcastDriver {

    public static void main(String[] args) throws IOException {
        BroadcastClient client1 = new BroadcastClient();
//        BroadcastClient client2 = new BroadcastClient();
//        BroadcastClient client3 = new BroadcastClient();
        client1.start();
//        client2.start();
//        client3.start();

//        BroadcastServer bs = new BroadcastServer();
//        String message = "";
//        Scanner sc = new Scanner(System.in);
//        while(message != "close"){
//            message = sc.nextLine();
//            bs.broadcast(message, InetAddress.getByName(Info.BROADCAST_IP));
//        }
    }
}
