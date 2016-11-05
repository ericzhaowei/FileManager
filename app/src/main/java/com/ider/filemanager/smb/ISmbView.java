package com.ider.filemanager.smb;

import java.net.InetAddress;

/**
 * Created by ider-eric on 2016/10/22.
 */

public interface ISmbView {
    public void showToast(String message);
    public void showToast(int resId);
    public void smbServerUpdate(String server);
    public void hideSearchView();
    public void updateProgress(int max, int progress);
    public void showNoResult();
    public void stopSearch();
    public void showLoginDialog(String ip);
    public void startSearch();
    public void setPageDescription(int resId);
    public void hidePageDescription();
}
