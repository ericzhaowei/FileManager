package com.ider.filemanager.smb;

/**
 * Created by ider-eric on 2016/10/22.
 */

public interface ISmbUpdateListener {
    public void onSmbUpdate(String server);
    public void onProgressUpdate(int max, int progress);
    public void onSearchInterupt();
}
