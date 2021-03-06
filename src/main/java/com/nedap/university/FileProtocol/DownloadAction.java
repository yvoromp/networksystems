package com.nedap.university.FileProtocol;

import com.nedap.university.client.UDPClient.UDPClient;

/**
 * Created by yvo.romp on 14/04/2017.
 */
public class DownloadAction extends Action {

    private int fileID;

    public DownloadAction(UDPClient client, String s){
        fileID = Integer.parseInt(s);
        performAction(client);
    }

    @Override
    public void performAction(UDPClient client){
        client.getCommandHandlerOfClient().sendDownloadMessage(fileID);
    }
}
