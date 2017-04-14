package com.nedap.university.UDPpackageStructure;

import com.nedap.university.client.UDPClient.commandHandlerOfClient;
import com.nedap.university.server.UDPServer.commandHandlerOfServer;

/**
 * Created by yvo.romp on 13/04/2017.
 */
public class UDPFlags {

    private commandHandlerOfClient cHandler;
    private commandHandlerOfServer sHandler;

    //flags
    private boolean isBC = false;               //1 is activated
    private boolean isACK = false;             //10 is activated
    private boolean isNewPort = false;        //100 is activated
    private boolean isRequest = false;         //1000 is activated
    private boolean isReqAnswer = false;         //10000 is activated

    private int flagValue;
    private final int BC_FLAG_VALUE = 1;
    private final int ACK_FLAG_VAlUE = 10;
    private final int NEWPORT_FLAG_VALUE = 100;
    private final int REQUEST_FLAG_VALUE = 1000;
    private final int REQUEST_ANSWER_FLAG_VALUE = 10000;

    public UDPFlags(commandHandlerOfClient cHandler){
        this.cHandler = cHandler;
    }

    public UDPFlags(commandHandlerOfServer sHandler){
        this.sHandler = sHandler;
    }

    public int checkForFlags(){
        flagValue = 0;
        if(isBC){
            flagValue += BC_FLAG_VALUE;
        }if(isACK){
            flagValue += ACK_FLAG_VAlUE;
        }if(isNewPort){
            flagValue += NEWPORT_FLAG_VALUE;
        }if(isRequest){
            flagValue += REQUEST_FLAG_VALUE;
        }if(isReqAnswer){
            flagValue += REQUEST_ANSWER_FLAG_VALUE;
        }
        return flagValue;

    }

    public void resetFlags(){
        isACK = false;
        isBC = false;
        isNewPort = false;
        isRequest = false;
        isReqAnswer = false;
    }

    public boolean isBC() {
        return isBC;
    }

    public void setBC() {
        isBC = true;
    }

    public boolean isACK() {
        return isACK;
    }

    public void setACK() {
        isACK = true;
    }

    public boolean isNewPort() {
        return isNewPort;
    }

    public void setNewPort() {
        isNewPort = true;
    }

    public boolean isRequest() {
        return isRequest;
    }

    public void setRequest() {
        isRequest = true;
    }

    public boolean isReqAnswer() {
        return isReqAnswer;
    }

    public void setReqAnswer() {
        isReqAnswer = true;
    }
}
