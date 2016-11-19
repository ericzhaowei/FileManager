package com.ider.filemanager.smb;

import android.content.Context;
import android.support.v7.widget.RecyclerView;

import java.net.InetAddress;

/**
 * Created by ider-eric on 2016/10/22.
 */

public interface ISmbView {

    public void showToast(String message);
    public void showToast(int resId);

    public void hideSearchingFloat();
    public void showSearchingFloat();

    public void hideSearchProgress();
    public void showSearchProgress();

    public void updateProgress(int max, int progress);
    public void showNoResultView();
    public void showLoginDialog(String ip);
    public void setPageDescription(int resId);
    public void hidePageDescription();
    public void showWifiError();
    public Context getcontext();

    public void setSmbAdapter(RecyclerView.Adapter adapter);
    public void notifyAdapter(RecyclerView.Adapter adapter);

}
