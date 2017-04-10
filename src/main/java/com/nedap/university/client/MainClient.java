package com.nedap.university.client;

import com.nedap.university.client.UDPClient.UDPClient;

public class MainClient {

    private static boolean keepAlive = true;
    private static boolean running = false;

    private MainClient() {}

    public static void main(String[] args) {
        running = true;
        System.out.println("Hello, Nedap University");
        System.out.println("i'm a client");

        UDPClient client = new UDPClient();
        Thread clientThread = new Thread(client);
        clientThread.start();

        initShutdownHook();

        while (keepAlive) {
            try {
                // do useful stuff
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }

        System.out.println("Stopped");
        running = false;
    }

    private static void initShutdownHook() {
        final Thread shutdownThread = new Thread() {
            @Override
            public void run() {
                keepAlive = false;
                while (running) {
                    try {
                        Thread.sleep(10);
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }
                }
            }
        };
        Runtime.getRuntime().addShutdownHook(shutdownThread);
    }
}
