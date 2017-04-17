package com.nedap.university.utils;

import java.io.File;
import java.io.FileOutputStream;

/**
 * Created by yvo.romp on 17/04/2017.
 */
public class SetFileContents {

    //writes to clientfolder
    public static void setFileContents(Integer[] fileContents, int id) {
        System.out.println("FILE IS BEING BUILD!");
        File downloadedFile = new File(String.format("src/clientFolder/downloadedFile%d",id));
        try (FileOutputStream fileStream = new FileOutputStream(downloadedFile)) {
            for (Integer fileContent : fileContents) {
                fileStream.write(fileContent);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
