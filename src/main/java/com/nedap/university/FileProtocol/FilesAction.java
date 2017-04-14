package com.nedap.university.FileProtocol;

import com.nedap.university.client.UDPClient.UDPClient;

/**
 * Created by yvo.romp on 14/04/2017.
 */
public class FilesAction extends Action{

    public FilesAction(UDPClient client, String s){
        performAction(client);
    }

    @Override
    public void performAction(UDPClient client){
        client.getCommandHandlerOfClient().printFiles();

    }
}
