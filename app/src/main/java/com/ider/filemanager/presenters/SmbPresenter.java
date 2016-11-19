package com.ider.filemanager.presenters;

import android.app.Service;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ider.filemanager.PreferenceManager;
import com.ider.filemanager.R;
import com.ider.filemanager.activity.SmbActivity;
import com.ider.filemanager.database.DbManager;
import com.ider.filemanager.presenters.ISmbPresenter;
import com.ider.filemanager.smb.ISmb;
import com.ider.filemanager.smb.ISmbView;
import com.ider.filemanager.smb.ParcelableSmbFile;
import com.ider.filemanager.smb.SmbConnector;
import com.ider.filemanager.smb.SmbHost;
import com.ider.filemanager.smb.SmbSearchUtil;
import com.ider.filemanager.smb.SmbViewHolder;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by ider-eric on 2016/10/21.
 */

public class SmbPresenter implements ISmb.ISmbSearchListener, ISmb.ISmbConnectListener, ISmbPresenter {

    String TAG = "SmbPresenter";
    boolean DEBUG = true;

    SmbAdapter adapter;
    ArrayList<String> servers;

    private ISmbView smbView;
    private SmbSearchUtil smbUtil;
    private SmbConnector connector;
    private Handler mHandler;
    private DbManager dbManager;
    private PreferenceManager preferenceManager;
    public static final int MSG_SMB_WRONG_USER = 0X000000;

    private void LOG(String log) {
        if(DEBUG) Log.i(TAG, log);
    }

    public SmbPresenter(ISmbView smbView, Handler mHandler) {
        this.smbView = smbView;
        this.mHandler = mHandler;
        smbUtil = new SmbSearchUtil(this);
        connector = new SmbConnector(this);
        dbManager = DbManager.getInstance(smbView.getcontext());
        preferenceManager = PreferenceManager.getInstance(smbView.getcontext());
    }

    @Override
    public void initSmbDevices() {
        servers = new ArrayList<>();
        adapter = new SmbAdapter(servers);
        smbView.setSmbAdapter(adapter);
    }

    @Override
    public void clearSavedHost() {
        preferenceManager.clearSavedHost();
    }

    @Override
    public void searchSmb(final int startProgress) {

        if(!isWifiConnected()) {
            LOG("WiFi not connected");
            smbUtil.interuptSearch();
            smbView.showWifiError();
            return;
        }

        smbView.showSearchProgress();
        smbView.showSearchingFloat();

        // 首先查询本地缓存的
        List<String> savedHost = preferenceManager.queryHost();
        if (savedHost != null) {
            // 清空保存的连接，重新ping后保存可用的连接
            clearSavedHost();
            smbUtil.checkSavadHost(savedHost);

        } else {
            new Thread() {
                @Override
                public void run() {
                    smbUtil.searchSmbHost(startProgress);
                }
            }.start();
        }
    }

    @Override
    public void onSmbUpdate(final String server) {
        mHandler.post(new Runnable() {
            @Override
            public void run() {
//                smbView.hideSearchingFloat();
                smbView.setPageDescription(R.string.smb_available_devices);
                adapter.add(server);
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
                stopSearch();
            }
        });
    }

    @Override
    public void stopSearch() {

        smbView.hideSearchProgress();
        if(servers.size() == 0) {
            smbView.showNoResultView();
        } else {
            smbView.hideSearchingFloat();
        }
        smbUtil.interuptSearch();
    }

    @Override
    public boolean isSearching() {
        return smbUtil.isSearching();
    }

    @Override
    public void getSmbContent(final String ip) {
        new Thread() {
            @Override
            public void run() {

                SmbHost smbHost = dbManager.queryCifs(ip);
                if (smbHost == null) {
                    smbHost = new SmbHost();
                    smbHost.setHost(ip);
                }
                connector.getContentBySmbhost(smbHost);
            }
        }.start();
    }

    @Override
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
                connector.getContentBySmbhost(smbHost);
            }
        }.start();
    }

    @Override
    public void onLoginFailed(final SmbHost smbHost, int message) {
        switch (message) {
            case ISmb.ISmbConnectListener.LOGIN_FALIED_WRONG_USERNAME:
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
            case ISmb.ISmbConnectListener.LOGIN_FAILED_UNKNOWN:
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
    public void onLoginSuccess(final SmbHost smbHost, final ArrayList<ParcelableSmbFile> sharedFolders) {
        dbManager.insertCifs(smbHost);
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                if(sharedFolders.size() == 0) {
                    smbView.showToast(R.string.smb_device_no_share);
                } else {
                    Intent intent = new Intent();
                    intent.setAction(SmbActivity.ACTION_LOGIN_SUCCESS);
                    intent.putParcelableArrayListExtra("folders", sharedFolders);
                    intent.putExtra("smbhost", smbHost);
                    smbView.getcontext().sendBroadcast(intent);
                }


            }
        });
    }

    private boolean isWifiConnected() {
        ConnectivityManager cm = (ConnectivityManager) (smbView.getcontext()).getSystemService(Service.CONNECTIVITY_SERVICE);
        NetworkInfo info = cm.getActiveNetworkInfo();
        if (info == null) {
            return false;
        }
        return info.getType() == ConnectivityManager.TYPE_WIFI;

    }



    public class SmbAdapter extends RecyclerView.Adapter {

        ArrayList<String> list;

        private SmbAdapter(ArrayList<String> list) {
            LOG("new Adapter : " + list.size());
            this.list = list;
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LinearLayout deviceItem = (LinearLayout) LayoutInflater.from(smbView.getcontext()).inflate(R.layout.smb_server_item, parent, false);
            LOG("onCreateViewHolder");
            return new SmbViewHolder(deviceItem);
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            final String ip = list.get(position).substring(4);
            LinearLayout deviceItem = ((SmbViewHolder) holder).deviceItem;
            TextView title = ((SmbViewHolder) holder).title;
            title.setText(list.get(position));
            deviceItem.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    getSmbContent(ip);
                }
            });
        }

        @Override
        public int getItemCount() {
            return list.size();
        }

        public void add(String server) {
            list.add(server);
            notifyDataSetChanged();
        }

    }

    @Override
    public void refreshHost() {
        if(!isWifiConnected()) {
            smbView.showWifiError();
            smbView.showToast(R.string.smb_connect_error_toast);
            return;
        }

        if(!isSearching()) {
            LOG("refresh clicked");
            servers.clear();
            adapter.notifyDataSetChanged();
            clearSavedHost();
            searchSmb(0);
        } else {
            smbView.showToast(R.string.smb_refresh_wait);
        }
    }
}
