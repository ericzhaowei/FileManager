package com.ider.filemanager.smb;

import android.os.Environment;
import android.util.Log;

import com.ider.filemanager.CommandExec;
import com.ider.filemanager.database.DbManager;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import jcifs.smb.SmbFile;

/**
 * Created by ider-eric on 2016/11/18.
 */

public class SmbConnector {

    private boolean DEBUG = true;
    private String TAG = "SmbConnector";
    private void LOG(String log) {
        Log.i(TAG, log);
    }

    private ISmb.ISmbConnectListener connectListener;

    public SmbConnector(ISmb.ISmbConnectListener connectListener) {
        this.connectListener = connectListener;
    }


    /**
     * 根据指定host获取其共享目录
     * @param smbHost 指定的SmbHost对象
     */
    public void getContentBySmbhost(SmbHost smbHost) {
        String hostIp = smbHost.getHost();
        String username = smbHost.getUsername();
        String password = smbHost.getPassword();
        boolean isPublic = smbHost.isPublic();

        ArrayList<ParcelableSmbFile> contents = new ArrayList<>();
        SmbFile smbFile;
        try{
            if (isPublic) {
                smbFile = new SmbFile(String.format("smb://guest:@%s/", hostIp));
            } else {
                smbFile = new SmbFile(String.format("smb://%s:%s@%s/", username, password, hostIp));

            }
            Log.i("tag", String.format("smb://%s:%s@%s/", username, password, hostIp));
            String[] smbs = smbFile.list();
            for(int i = 0; i < smbs.length; i++) {
                if(!smbs[i].endsWith("$")) {
                    contents.add(new ParcelableSmbFile(smbFile.listFiles()[i]));
                }
            }
            // contents is a list of ParcelableSmbFile
            connectListener.onLoginSuccess(smbHost, contents);

        } catch (Exception e) {
            Log.i("tag", e.getMessage());
            if (e.getMessage().contains("unknown user name or bad password")) {
                connectListener.onLoginFailed(smbHost, ISmb.ISmbConnectListener.LOGIN_FALIED_WRONG_USERNAME);
            } else {
                connectListener.onLoginFailed(smbHost, ISmb.ISmbConnectListener.LOGIN_FAILED_UNKNOWN);
            }
        }

    }

}
