package com.nedap.university.server;

import com.nedap.university.server.UDPServer.UDPServer;

public class MainServer {

    private static boolean keepAlive = true;
    private static boolean running = false;

    private MainServer() {}

    public static void main(String[] args) {
        running = true;
        System.out.println("Hello, Nedap University");
        System.out.println("i'm a server");

        UDPServer server = new UDPServer();
        Thread serverThread = new Thread(server);
        serverThread.start();

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
