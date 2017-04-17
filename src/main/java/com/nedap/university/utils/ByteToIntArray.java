package com.nedap.university.utils;

/**
 * Created by yvo.romp on 16/04/2017.
 */
public class ByteToIntArray {

    public Integer[] ByteToIntArray(byte[] byteArray){
        Integer[] intArray = new Integer[byteArray.length];

        // converting byteArray to intArray
        for (int i = 0; i < byteArray.length; intArray[i] = Integer.valueOf(byteArray[i++]));
        return intArray;
    }
}
