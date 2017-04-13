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
        String order = splitInputString[0];

        switch (order){
            case LS:
                action = new LSAction(client,order);
                break;
        }
        return action;
    }
}
