package com.ider.filemanager.presenters;

/**
 * Created by ider-eric on 2016/11/17.
 */

public interface ISmbPresenter {

    public void searchSmb(int startProgress);
    public void stopSearch();
    public boolean isSearching();
    public void clearSavedHost();
    public void getSmbContent(String ip);
    public void getSmbContent(String ip, String username, String password);

    public void initSmbDevices();
    public void refreshHost();
}
