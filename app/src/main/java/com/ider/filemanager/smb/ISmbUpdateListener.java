package com.ider.filemanager.smb;

/**
 * Created by ider-eric on 2016/10/22.
 */

public interface ISmbUpdateListener {
    public static final int LOGIN_FALIED_WRONG_USERNAME = 0x000001;
    public static final int LOGIN_FAILED_UNKNOWN = 0x000002;
    public void onSmbUpdate(String server);
    public void onProgressUpdate(int max, int progress);
    public void onSearchInterupt();
    public void onLoginFailed(SmbHost smbHost, int message);
    public void onLoginSuccess(SmbHost smbHost);
}
