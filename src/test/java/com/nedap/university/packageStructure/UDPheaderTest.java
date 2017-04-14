package com.nedap.university.packageStructure;

import com.nedap.university.UDPpackageStructure.UDPheader;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Created by yvo.romp on 11/04/2017.
 */
public class UDPheaderTest {

    private com.nedap.university.UDPpackageStructure.UDPheader UDPheader;
    private com.nedap.university.UDPpackageStructure.packageCreator packageCreator;

    @Before
    public void setUp(){
        UDPheader = new UDPheader(2500,5000,666,0,0);
    }

    @Test
    public void testchangeIntToByteArray(){
        byte[] testarray = new byte[4];
        testarray[0] = 0;
        testarray[1] = 0;
        testarray[2] = 0;
        testarray[3] = 2;
        byte[] real = UDPheader.changeIntToByteArray(2);
        byte[] test = UDPheader.arrayToBytes();
        System.out.println(UDPheader.bytesToHex(test));
        assertArrayEquals(testarray,real);

    }



}