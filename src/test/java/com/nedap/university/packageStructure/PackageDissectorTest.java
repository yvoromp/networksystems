package com.nedap.university.packageStructure;

import org.junit.Before;
import org.junit.Test;

import java.net.DatagramPacket;

import static org.junit.Assert.*;

/**
 * Created by yvo.romp on 12/04/2017.
 */
public class PackageDissectorTest {

    PackageDissector packageDissector;
    byte[] arr;
    UDPheader UDPh;

    @Before
    public void setUp(){
        packageDissector = new PackageDissector();
        UDPh = new UDPheader();
    }

    @Test
    public void testCheckFlags(){
        arr = UDPh.changeIntToByteArray(10000);
        packageDissector.checkFlags(arr);
        assertTrue(packageDissector.isReqAnswer);
        packageDissector.resetFlags();

        arr = UDPh.changeIntToByteArray(11000);
        packageDissector.checkFlags(arr);
        assertTrue(packageDissector.isReqAnswer);
        assertTrue(packageDissector.isRequest);
        packageDissector.resetFlags();

        arr = UDPh.changeIntToByteArray(1001);
        packageDissector.checkFlags(arr);
        assertTrue(packageDissector.isBC);
        assertTrue(packageDissector.isRequest);
        packageDissector.resetFlags();

        arr = UDPh.changeIntToByteArray(11010);
        packageDissector.checkFlags(arr);
        assertTrue(packageDissector.isReqAnswer);
        assertTrue(packageDissector.isRequest);
        assertFalse(packageDissector.isNewPort);
        assertTrue(packageDissector.isACK);
        assertFalse(packageDissector.isBC);
        packageDissector.resetFlags();

    }



}