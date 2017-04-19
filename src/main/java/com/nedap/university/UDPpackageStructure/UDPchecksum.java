package com.nedap.university.UDPpackageStructure;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

/**
 * Created by yvo.romp on 10/04/2017.
 */
public class UDPchecksum {

    private byte[] totalChecksum;

    public byte[] checkSum(byte[] datapart){
        MessageDigest messageDigest;
        try{
            messageDigest = MessageDigest.getInstance("MD5");
            byte[] digest= messageDigest.digest(datapart);
            totalChecksum = Arrays.copyOfRange(digest,0,4);
            return totalChecksum;
        } catch (NoSuchAlgorithmException e){
            System.out.println("problem occurred with checkSum...");
        }
        return null;
    }

    public byte[] getTotalChecksum(byte[] datapart){
        return checkSum(datapart);
    }


}
