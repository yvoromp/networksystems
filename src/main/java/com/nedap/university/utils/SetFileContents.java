package com.nedap.university.utils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by yvo.romp on 17/04/2017.
 */
public class SetFileContents {


    public void setByteArrToFileClient(byte[] arr, String id){
        System.out.println("FILE IS BEING BUILD!");
        System.out.println("building file......"+id);
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(String.format("src/clientFolder/%s",id));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        try {
            fos.write(arr);
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void setByteArrToFileServer(byte[] arr, String id){
        System.out.println("FILE IS BEING BUILD!!");
        System.out.println("building file......"+id);
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(String.format("/home/pi/severFolder/%s",id));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        try {
            fos.write(arr);
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
