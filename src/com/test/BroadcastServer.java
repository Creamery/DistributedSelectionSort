package com.test;

import com.main.Info;

import java.io.IOException;
import java.net.*;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Objects;

public class BroadcastServer {

    private static DatagramSocket socket;

    public void broadcast(String message, InetAddress destination){
        try {
            socket = new DatagramSocket(Info.BROADCAST_PORT);
            socket.setBroadcast(true);
        } catch (SocketException e) {
            e.printStackTrace();
        }
        byte[] buffer = message.getBytes();
        byte[] paddedBuffer = new byte[buffer.length + (256 - buffer.length)];
        System.arraycopy(buffer,0,paddedBuffer,0,buffer.length);
        System.out.println(buffer.length);
        System.out.println(paddedBuffer.length);
        String s = new String(paddedBuffer);
        System.out.println(s.trim());
        DatagramPacket packet = new DatagramPacket(paddedBuffer, paddedBuffer.length ,destination, Info.BROADCAST_PORT);
        try {
            socket.send(packet);
            System.out.println("Sent message");
        } catch(IOException e){
            e.printStackTrace();
        }
        socket.close();
    }

    List<InetAddress> listAllBroadcastAddresses() throws SocketException {
        List<InetAddress> broadcastList = new ArrayList<>();
        Enumeration<NetworkInterface> interfaces
                = NetworkInterface.getNetworkInterfaces();
        while (((Enumeration<NetworkInterface>) interfaces).hasMoreElements()) {
            NetworkInterface networkInterface = interfaces.nextElement();

            if (networkInterface.isLoopback() || !networkInterface.isUp()) {
                continue;
            }

            networkInterface.getInterfaceAddresses().stream()
                    .map(a -> a.getBroadcast())
                    .filter(Objects::nonNull)
                    .forEach(broadcastList::add);
        }
        return broadcastList;
    }
}
