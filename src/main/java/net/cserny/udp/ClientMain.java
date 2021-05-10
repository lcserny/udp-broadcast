package net.cserny.udp;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class ClientMain implements Runnable {

    public static void main(String[] args) {
        new ClientMain().run();
    }

    @Override
    public void run() {
        try {
            DatagramSocket socket = new DatagramSocket(new InetSocketAddress(51234));
            socket.setBroadcast(true);

            ScheduledExecutorService scheduledExecutor = Executors.newSingleThreadScheduledExecutor();
            scheduledExecutor.scheduleWithFixedDelay(() -> {
                try {
                    receive(socket);
                } catch (IOException e) {
                    System.out.println(e.getMessage());
                }
            }, 0, 50, TimeUnit.MILLISECONDS);
//            scheduledExecutor.shutdown();
            System.out.println("Listening for messages...");

            System.out.println("Broadcasting message...");
            broadcast(socket, "Hello", InetAddress.getByName("255.255.255.255"));
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    private void broadcast(DatagramSocket socket, String message, InetAddress address) throws IOException {
        byte[] messageBytes = message.getBytes();
        DatagramPacket sending = new DatagramPacket(messageBytes, messageBytes.length, address, 41234);
        socket.send(sending);

        System.out.println("Sending: " + message);
    }

    private void receive(DatagramSocket socket) throws IOException {
        byte[] buffer = new byte[256];
        DatagramPacket receiving = new DatagramPacket(buffer, buffer.length);
        socket.receive(receiving);

        System.out.println("Received: " + new String(receiving.getData(), receiving.getOffset(), receiving.getLength(), StandardCharsets.UTF_8));
    }
}
