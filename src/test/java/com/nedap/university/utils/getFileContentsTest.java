package com.nedap.university.utils;

import org.junit.Test;

import java.nio.ByteBuffer;
import java.util.Arrays;

import static org.junit.Assert.*;

/**
 * Created by yvo.romp on 15/04/2017.
 */
public class getFileContentsTest {

    @Test
    public void getFileContentsIfServer() throws Exception {
        getFileContents getFileContents = new getFileContents();
        System.out.println(getFileContents.getFileContentsIfClient(1));
    }


    @Test
    public void testByteToIntegerArray(){
        byte one = 122;
        byte two = 14;
        byte three = -120;
        byte[] byteArray = new byte[] {one,two,three};
        Integer[] intArray = new Integer[byteArray.length];

// converting byteArray to intArray
        for (int i = 0; i < byteArray.length; intArray[i] = Integer.valueOf(byteArray[i++]));
        assertEquals(122, intArray[0].intValue());
        assertEquals(14, intArray[1].intValue());
        assertEquals(-120, intArray[2].intValue());
    }

    @Test
    public void testIntToByteToInt(){
        //test protocol and utils functions
        IntToByteArray intToByteArray = new IntToByteArray();
        ByteToIntArray byteToIntArray = new ByteToIntArray();
        Integer[] pkt = new Integer[3];
        pkt[0] = 122;
        pkt[1] = 14;
        pkt[2] = -120;
        byte[] packetToSend = intToByteArray.changeIntegerArrayToByteArray(pkt);

        //this packetToSend is received at the other end
        Integer[] receivedPacket = byteToIntArray.ByteToIntArray(packetToSend);
        assertTrue(pkt[0] == receivedPacket[0].intValue());
        assertTrue(pkt[1] == receivedPacket[1].intValue());
        assertTrue(pkt[2] == receivedPacket[2].intValue());

        Byte ex = packetToSend[0];
        System.out.println(ex.intValue());
    }
    @Test
    public void testIntToByteArray(){
        int value = -5;
        IntToByteArray intToByteArray = new IntToByteArray();
        byte[] send = intToByteArray.changeIntToByteArray(value);

        ByteToIntArray byteToIntArray = new ByteToIntArray();
        Integer[] recValue = byteToIntArray.ByteToIntArray(send);
        assertEquals(value,recValue[3].intValue());

        byte[] arr = new byte[5];
        arr[0] = -84;
        byteToIntArray.ByteToIntArray(arr);
        System.out.println(arr[0]);


    }

}