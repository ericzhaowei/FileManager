package com.ider.filemanager.smb;

/**
 * Created by ider-eric on 2016/11/3.
 */

public class SmbShare {
    private String server;
    private String name;

    public void setServer(String server) {
        this.server = server;
    }

    public String getServer() {
        return server;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
