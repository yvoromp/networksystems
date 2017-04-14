package com.nedap.university.FileProtocol;

import com.nedap.university.client.UDPClient.UDPClient;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * Created by yvo.romp on 13/04/2017.
 */
public class TerminalInputThread extends Thread{


    private String validKeyboardInput;
    private UDPClient client;

    public TerminalInputThread(UDPClient client){
        this.client = client;
    }

    public void run(){
            inputByKeyboard();
    }

    /**
     * checks if there is input
     */
    public boolean isKeyboardInput(){
        System.out.println("terminal activated for input!");
        BufferedReader k = new BufferedReader(new InputStreamReader(System.in));
        boolean valid = false;
        try{
            valid = ((validKeyboardInput = k.readLine()) != null);
        }catch (IOException e){
            System.out.println("can't read the input");
        }
        return valid;

    }

    /**
     * sends keyboardinput to client output
     */
    public void inputByKeyboard(){
        try{
            while(isKeyboardInput()){
                sendText(validKeyboardInput);
            }
        }catch (IllegalArgumentException e) {
            System.out.println("you entered nonsense");
        }
    }

    public void sendText(String textToSend){
        System.out.println("you entered: " + textToSend);
        InputToAction inputToAction = new InputToAction();
        inputToAction.input(client,textToSend);


    }
}
