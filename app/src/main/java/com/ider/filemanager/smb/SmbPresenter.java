package com.ider.filemanager.smb;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

import com.ider.filemanager.PreferenceManager;
import com.ider.filemanager.R;
import com.ider.filemanager.database.DbManager;

import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;

import jcifs.smb.SmbFile;
import rx.Observable;
import rx.Scheduler;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;


/**
 * Created by ider-eric on 2016/10/21.
 */

public class SmbPresenter implements ISmbUpdateListener {

    private ISmbView smbView;
    private SmbUtil smbUtil;
    private Handler mHandler;
    private DbManager dbManager;
    private PreferenceManager preferenceManager;
    public static final int MSG_SMB_WRONG_USER = 0X000000;


    public SmbPresenter(ISmbView smbView, Handler mHandler) {
        this.smbView = smbView;
        this.mHandler = mHandler;
        smbUtil = new SmbUtil(this);
        dbManager = DbManager.getInstance((Activity) smbView);
        preferenceManager = PreferenceManager.getInstance((Activity) smbView);
    }


    public void clearSavedHost() {
        preferenceManager.clearSavedHost();
    }

    public void searchSmb() {
        smbView.startSearch();
        smbView.hideSearchView();
        // 首先查询本地缓存的
        List<String> savedHost = preferenceManager.queryHost();
        if (savedHost != null) {
            Log.i("tag", "check for local saved hosts");
            // 清空保存的连接，重新ping后保存可用的连接
            clearSavedHost();
            smbUtil.checkSavadHost(savedHost);

        } else {
            Log.i("tag", "check for remote hosts");
            new Thread() {
                @Override
                public void run() {
                    smbUtil.searchSmbHost();
                }
            }.start();
        }
    }

    @Override
    public void onSmbUpdate(final String server) {
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                smbView.hideSearchView();
                smbView.setPageDescription(R.string.smb_available_devices);
                smbView.smbServerUpdate(server);
                preferenceManager.insertIp(server.substring(4));
            }
        });
    }

    @Override
    public void onProgressUpdate(final int max, final int progress) {
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                smbView.updateProgress(max, progress);
            }
        });
    }

    @Override
    public void onSearchInterupt() {
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                smbView.stopSearch();
            }
        });
    }

    public void stopSearch() {
        smbUtil.interuptSearch();
    }

    public boolean isSearching() {
        return smbUtil.isSearching();
    }

    public void getSmbContent(final String ip) {
        new Thread() {
            @Override
            public void run() {

                SmbHost smbHost = dbManager.queryCifs(ip);
                if (smbHost == null) {
                    smbHost = new SmbHost();
                    smbHost.setHost(ip);
                }
                smbUtil.getContentBySmbhost(smbHost);
            }
        }.start();
    }

    public void getSmbContent(final String ip, final String username, final String password) {
        if (username == null || password == null) {
            getSmbContent(ip);
            return;
        }
        new Thread() {
            @Override
            public void run() {
                SmbHost smbHost = new SmbHost();
                smbHost.setHost(ip);
                smbHost.setUsername(username);
                smbHost.setPassword(password);
                smbUtil.getContentBySmbhost(smbHost);
            }
        }.start();
    }

    @Override
    public void onLoginFailed(final SmbHost smbHost, int message) {
        switch (message) {
            case ISmbUpdateListener.LOGIN_FALIED_WRONG_USERNAME:
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        if (smbHost.getUsername() != null) {
                            smbView.showToast(R.string.smb_wrong_login);
                            dbManager.deleteCifs(smbHost.getHost());
                        }

                        smbView.showLoginDialog(smbHost.getHost());
                    }
                });

                break;
            case ISmbUpdateListener.LOGIN_FAILED_UNKNOWN:
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        smbView.showToast(R.string.smb_connect_error);
                    }
                });
                break;


        }
    }

    @Override
    public void onLoginSuccess(SmbHost smbHost) {
        dbManager.insertCifs(smbHost);
        mHandler.post(new Runnable() {
            @Override
            public void run() {

            }
        });
    }
}
