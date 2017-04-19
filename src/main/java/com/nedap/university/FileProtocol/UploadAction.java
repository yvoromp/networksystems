package com.nedap.university.FileProtocol;

import com.nedap.university.client.UDPClient.UDPClient;

/**
 * Created by yvo.romp on 17/04/2017.
 */
public class UploadAction extends Action {

    private int fileID;

    public UploadAction(UDPClient client, String s){
        fileID = Integer.parseInt(s);
        performAction(client);
    }

    @Override
    public void performAction(UDPClient client){
        client.getCommandHandlerOfClient().sendUploadMessage(fileID);
    }
}
