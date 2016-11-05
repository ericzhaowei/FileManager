package com.ider.filemanager.smb;

/**
 * Created by ider-eric on 2016/11/1.
 */

public class SmbHost {
    private String host = null;
    private String username = null;
    private String password = null;

    public void setHost(String host) {
        this.host = host;
    }

    public void setPassword(String password) {
        this.password = password;
    }


    public void setUsername(String username) {
        this.username = username;
    }

    public String getHost() {
        return host;
    }

    public String getPassword() {
        return password;
    }

    public String getUsername() {
        return username;
    }

    public boolean isPublic() {
        return password == null;
    }

}


