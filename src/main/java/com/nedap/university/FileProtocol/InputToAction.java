package com.nedap.university.FileProtocol;


import com.nedap.university.client.UDPClient.UDPClient;

/**
 * Created by yvo.romp on 13/04/2017.
 */
public class InputToAction implements terminalActions{

    public InputToAction() {

    }

    public Action input(UDPClient client, String textFromTerminal){
        Action action = null;
        String[] splitInputString = textFromTerminal.split(" ");
        String commandToGive = splitInputString[0];

        switch (commandToGive){
            case LS:
                action = new LSAction(client,commandToGive);
                break;
            case FILES:
                action = new FilesAction(client,commandToGive);
                break;
            case DOWNLOAD:
                commandToGive = splitInputString[1];
                action = new DownloadAction(client,commandToGive);
                break;
            case UPLOAD:
                commandToGive = splitInputString[1];
                action = new UploadAction(client,commandToGive);
        }
        return action;
    }
}
