package com.nedap.university.utils;

import com.nedap.university.FileProtocol.FileProber;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by yvo.romp on 15/04/2017.
 */
public class getFileContents {

    public byte[] getFileToBytesifServer(int id){
        FileProber fileProber = new FileProber();
        File fileToSend = fileProber.returnFileOfMapWithFilesIfServer(id);
        String name = fileToSend.getName();
        byte[] array = new byte[0];
        try {
            array = Files.readAllBytes(new File("/home/pi/serverFolder",name).toPath());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return array;
    }

    public byte[] getFileToBytesifClient(int id){
        FileProber fileProber = new FileProber();
        File fileToSend = fileProber.returnFileOfMapWithFilesIfClient(id);
        String name = fileToSend.getName();
        byte[] array = new byte[0];
        try {
            array = Files.readAllBytes(new File("src/clientFolder/",name).toPath());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return array;
    }



}
