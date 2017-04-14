package com.nedap.university.FileProtocol;

import com.nedap.university.client.UDPClient.UDPClient;

/**
 * Created by yvo.romp on 13/04/2017.
 */
public abstract class Action {

    public abstract void performAction(UDPClient client);
}
