package com.nedap.university.utils;

import com.nedap.university.FileProtocol.FileProber;

import java.io.File;
import java.io.FileInputStream;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by yvo.romp on 15/04/2017.
 */
public class getFileContents {

    public Integer[] getFileContentsIfServer(int fileID){
        //return the File on the pi that has the given file id
        FileProber fileProber = new FileProber();
        File fileToSend = fileProber.returnFileOfMapWithFilesIfServer(fileID);
        try (FileInputStream fileStream = new FileInputStream(fileToSend)) {
            Integer[] fileContents = new Integer[(int) fileToSend.length()];

            for (int i = 0; i < fileContents.length; i++) {
                int nextByte = fileStream.read();
                if (nextByte == -1) {
                    throw new Exception("File size is smaller than reported");
                }

                fileContents[i] = nextByte;
            }
            return fileContents;
        } catch (Exception e) {
            System.err.println(e.getMessage());
            System.err.println(e.getStackTrace());
            return null;
        }

    }

    public Integer[] getFileContentsIfClient(int fileID){
        //return the File on the pi that has the given file id
        FileProber fileProber = new FileProber();
        File fileToSend = fileProber.returnFileOfMapWithFilesIfClient(fileID);
        try (FileInputStream fileStream = new FileInputStream(fileToSend)) {
            Integer[] fileContents = new Integer[(int) fileToSend.length()];

            for (int i = 0; i < fileContents.length; i++) {
                int nextByte = fileStream.read();
                if (nextByte == -1) {
                    throw new Exception("File size is smaller than reported");
                }

                fileContents[i] = nextByte;
            }
            return fileContents;
        } catch (Exception e) {
            System.out.println("no such file yet initialized");
            return null;
        }

    }
}
