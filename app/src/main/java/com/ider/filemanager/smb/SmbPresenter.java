package com.ider.filemanager.smb;

import android.os.Handler;

/**
 * Created by ider-eric on 2016/10/21.
 */

public class SmbPresenter implements ISmbUpdateListener {

    private ISmbView smbView;
    private SmbUtil smbUtil;
    private Handler mHandler;

    public SmbPresenter(ISmbView smbView, Handler mHandler) {
        this.smbView = smbView;
        this.mHandler = mHandler;
        smbUtil = new SmbUtil(this);
    }

    public void searchSmb() {
        new Thread() {
            @Override
            public void run() {
                smbUtil.searchSmbHost();
            }
        }.start();
    }

    @Override
    public void onSmbUpdate(final String server) {
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                smbView.smbServerUpdate(server);
            }
        });

    }
}
