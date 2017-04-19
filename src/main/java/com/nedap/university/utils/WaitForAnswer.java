package com.nedap.university.utils;

/**
 * Created by yvo.romp on 19/04/2017.
 */
public class WaitForAnswer {

    public void waitForAnswer(){
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
