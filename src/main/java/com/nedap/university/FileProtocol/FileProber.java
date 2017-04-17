package com.nedap.university.FileProtocol;

import java.io.*;
import java.util.HashMap;

/**
 * Created by yvo.romp on 13/04/2017.
 */
public class FileProber {

    private File Cfolder = new File("src/clientFolder");

    private File Sfolder = new File("home/pi/serverFolder");        //FOR REAL
    //private File Sfolder = new File("src/clientFolder");    //FOR LOCAL TESTING
    private File[] listOfFilesOfClient = Cfolder.listFiles();
    private File[] listOfFilesOfServer = Sfolder.listFiles();
    private HashMap<Integer, String> fileMap;

    /**
     * serverpart
     */
    public byte[] filenamesToSendIfServer() throws IOException{
        fileMap = new HashMap<>();

        for (int i = 0; i < listOfFilesOfServer.length; i++) {
            if (listOfFilesOfServer[i].isFile()) {
                fileMap.put(i+1,listOfFilesOfServer[i].getName());
                System.out.println(i+1 + ".  " + listOfFilesOfServer[i].getName());
            }
        }
        return serialize(fileMap);
    }

    public void filenamesToReceive(byte[] mapAsByteArray) throws IOException, ClassNotFoundException{
        fileMap = (HashMap<Integer,String>) deserialize(mapAsByteArray);
        String bound = "============= LIST OF FILES =============";
        System.out.println(bound);
            for (Integer i : fileMap.keySet()) {
                String key = i.toString();
                String value = fileMap.get(i).toString();
                System.out.println(key + ".  " + value);
        }
        System.out.println(bound+ "\n");

    }

    public File returnFileOfMapWithFilesIfServer(int fileId){
        HashMap<Integer,File> tempFileMap = new HashMap<>();
        for (int i = 0; i < listOfFilesOfServer.length; i++) {
            if (listOfFilesOfServer[i].isFile()) {
                tempFileMap.put(i+1,listOfFilesOfServer[i]);
            }
        }
        return tempFileMap.get(fileId);
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

    /**
     * clientpart
     */
    public byte[] filenamesToSendIfClient() throws IOException{
        fileMap = new HashMap<>();

        for (int i = 0; i < listOfFilesOfClient.length; i++) {
            if (listOfFilesOfClient[i].isFile()) {
                fileMap.put(i+1,listOfFilesOfClient[i].getName());
                System.out.println(i+1 + ".  " + listOfFilesOfClient[i].getName());
            }
        }
        return serialize(fileMap);
    }

    public void printAllFilesOfClient() {
        String bound = "============= FILES OF CLIENT =============";
        System.out.println("\n" + bound);
        for (int i = 0; i < listOfFilesOfClient.length; i++) {
            if (listOfFilesOfClient[i].isFile()) {
                System.out.println(i + 1 + ".  " + listOfFilesOfClient[i].getName());
            }
        }
        System.out.println(bound+ "\n");
    }

    public File returnFileOfMapWithFilesIfClient(int fileId){
        HashMap<Integer,File> tempFileMap = new HashMap<>();
        for (int i = 0; i < listOfFilesOfClient.length; i++) {
            if (listOfFilesOfClient[i].isFile()) {
                tempFileMap.put(i+1,listOfFilesOfClient[i]);
            }
        }
        return tempFileMap.get(fileId);
    }

}
