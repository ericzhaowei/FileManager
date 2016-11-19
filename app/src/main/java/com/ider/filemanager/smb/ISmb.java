package com.ider.filemanager.smb;

import java.util.ArrayList;

import jcifs.smb.SmbFile;

/**
 * Created by ider-eric on 2016/10/22.
 */

public class ISmb {

    public interface ISmbSearchListener {
        public void onSmbUpdate(String server);
        public void onProgressUpdate(int max, int progress);
        public void onSearchInterupt();

    }

    public interface ISmbConnectListener {
        public static final int LOGIN_FALIED_WRONG_USERNAME = 0x000001;
        public static final int LOGIN_FAILED_UNKNOWN = 0x000002;
        public void onLoginFailed(SmbHost smbHost, int message);
        public void onLoginSuccess(SmbHost smbHost, ArrayList<ParcelableSmbFile> sharedFolders);

    }

    public interface ISmbMountListener {
        public static final int MOUNT_FAILED_CANNOT_CREATE_MOUNTPOINT = 100;
        public void onMountFailed(int message);
        public void onMountSuccess();
    }

}


