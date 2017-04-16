package com.nedap.university.utils;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Created by yvo.romp on 15/04/2017.
 */
public class getFileContentsTest {

    @Test
    public void getFileContentsIfServer() throws Exception {
        getFileContents getFileContents = new getFileContents();
        System.out.println(getFileContents.getFileContentsIfClient(1));
        System.out.println(getFileContents.getFileContentsIfClient(1));
    }

}