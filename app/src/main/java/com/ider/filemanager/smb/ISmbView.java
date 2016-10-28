package com.ider.filemanager.smb;

import java.net.InetAddress;

/**
 * Created by ider-eric on 2016/10/22.
 */

public interface ISmbView {
    public void smbServerUpdate(String server);
    public void hideSearchView();
    public void updateProgress(int max, int progress);
    public void hideSearchProgress();
}
