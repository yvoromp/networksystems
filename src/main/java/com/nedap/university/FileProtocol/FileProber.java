package com.nedap.university.FileProtocol;

import java.io.*;
import java.util.HashMap;

/**
 * Created by yvo.romp on 13/04/2017.
 */
public class FileProber {

    private File folder = new File("src/clientFolder");
    private File[] listOfFiles = folder.listFiles();
    private HashMap<Integer, String> fileMap;


    public byte[] filenamesToSend() throws IOException{
        fileMap = new HashMap<>();

        for (int i = 0; i < listOfFiles.length; i++) {
            if (listOfFiles[i].isFile()) {
                fileMap.put(i+1,listOfFiles[i].getName());
                System.out.println(i+1 + ".  " + listOfFiles[i].getName());
            }
        }
        return serialize(fileMap);
    }

    public void filenamesToReceive(byte[] mapAsByteArray) throws IOException, ClassNotFoundException{
        fileMap = (HashMap<Integer,String>) deserialize(mapAsByteArray);

            for (Integer i : fileMap.keySet()) {
                String key = i.toString();
                String value = fileMap.get(i).toString();
                System.out.println(key+ ".  " + value);
        }
    }

    public static byte[] serialize(HashMap<Integer, String> files) throws IOException{
        ByteArrayOutputStream bOut = new ByteArrayOutputStream();
        ObjectOutputStream oOut = new ObjectOutputStream(bOut);
        oOut.writeObject(files);
        return bOut.toByteArray();
    }

    public static Object deserialize(byte[] bytes) throws IOException, ClassNotFoundException{
        ByteArrayInputStream bIn = new ByteArrayInputStream(bytes);
        ObjectInputStream oIn = new ObjectInputStream(bIn);
        return oIn.readObject();
    }
}
