package com.nedap.university.FileProtocol;

import org.junit.Before;
import org.junit.Test;

import java.io.IOException;

import static org.junit.Assert.*;

/**
 * Created by yvo.romp on 13/04/2017.
 */
public class FileProberTest {

    private FileProber fileProber;

    @Before
    public void setUp(){
        fileProber = new FileProber();
    }


    @Test
    public void probingTest() throws IOException, ClassNotFoundException{
        System.out.println("out: ");
        byte[] out = fileProber.filenamesToSendIfClient();
        System.out.println("\n");
        System.out.println("in:  ");
        fileProber.filenamesToReceive(out);
    }

    @Test
    public void fileGetNameTest(){
        FileProber fileProber = new FileProber();
        fileProber.printAllFilesOfClient();
        System.out.println("\n");
        String filenr = fileProber.probeForFilenameClientMap(1);
        System.out.println(filenr);
        //assertEquals("Abraham Lincoln.png",filenr);
    }



}