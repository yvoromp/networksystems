package com.nedap.university.FileProtocol;

/**
 * Created by yvo.romp on 13/04/2017.
 */

/**
 * possible given input that invokes action
 */

public interface terminalActions {

    //gives the list of files on the pi
    String LS = "ls";

    //gives the list on own computer
    String FILES = "files";

    // download file ex.: download 4
    String DOWNLOAD = "download";

    //upload file ex.: upload 3
    String UPLOAD = "upload";

}
