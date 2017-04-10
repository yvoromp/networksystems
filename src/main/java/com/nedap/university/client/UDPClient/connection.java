package com.nedap.university.client.UDPClient;

import java.net.InetAddress;

/**
 * Created by yvo.romp on 10/04/2017.
 */


public class connection {

    private InetAddress address;
    private int port;

    public connection(InetAddress foundInetAddress, int foundPort){
        address = foundInetAddress;
        port = foundPort;
        addConnectionToMap(address,port);

    }

    private void addConnectionToMap(InetAddress newFoundAddress, int newFoundPort){
    }

    public InetAddress getAddress() {
        return address;
    }

    public void setAddress(InetAddress address) {
        this.address = address;
    }


    public int getPort() {
        return port;
    }
    public void setPort(int port) {
        this.port = port;
    }


}
