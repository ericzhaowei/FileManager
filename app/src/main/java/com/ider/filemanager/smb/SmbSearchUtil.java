package com.ider.filemanager.smb;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import java.io.File;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import jcifs.smb.SmbException;
import jcifs.smb.SmbFile;

/**
 * Created by ider-eric on 2016/10/21.
 */

public class SmbSearchUtil {

    private static boolean DEBUG = true;
    private static String TAG = "SmbUtil";

    public final static int SMB_SEARCH_UPDATE = 100;
    public final static String mDirSmb = "SMB";

    private ISmb.ISmbSearchListener updateListener;
    private boolean searchInterupt;
    private int startThread, overThread;
    private int THREAD_MAX = 16;
    private int THREAD_TOTAL = 0;


    private static void LOG(String str) {
        if (DEBUG) {
            Log.d(TAG, str);
        }
    }

    public SmbSearchUtil(ISmb.ISmbSearchListener updateListener) {
        this.updateListener = updateListener;
    }


    public void searchSmbHost(int startProgress) {
        searchInterupt = false;
        startThread = startProgress;
        overThread = startProgress;
        Vector<Vector<InetAddress>> vectorList = Lan.getSubnetAddress();

        List<InetAddress> addrs = new Vector<>();

        for(int i = 0; i < vectorList.size(); i++) {
            addrs.addAll(vectorList.get(i));
        }
        THREAD_TOTAL = addrs.size();
        LOG("TOTAL is " + THREAD_TOTAL);
        pingInetAddressLists(addrs);

    }

    private void pingInetAddressLists(List<InetAddress> hosts) {
        // 从overThread开始，当屏幕旋转导致对象重建时，可继续从上次的点开始
        for (int i = overThread; i < hosts.size();) {
            if(searchInterupt) {
                return;
            }
            int activeThread = startThread - overThread;
            if(activeThread < THREAD_MAX) {
                LOG("ping : " + hosts.get(i).getHostAddress());
                Thread scan = new Thread(new ConnectHost(hosts.get(i).getHostAddress()));
                scan.setPriority(10);
                scan.start();
                startThread++;
                i++;
            } else {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void pingIpLists(List<String> hostIps) {
        for (int i = 0; i < hostIps.size();) {
            if(searchInterupt) {
                return;
            }
            int activeThread = startThread - overThread;
            if(activeThread < THREAD_MAX) {
                Thread scan = new Thread(new ConnectHost(hostIps.get(i)));
                scan.setPriority(10);
                scan.start();
                startThread++;
                i++;
            } else {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }


    public void checkSavadHost(List<String> savedHosts) {
        searchInterupt = false;
        startThread = 0;
        overThread = 0;
        THREAD_TOTAL = savedHosts.size();
        pingIpLists(savedHosts);
    }


    private class ConnectHost implements Runnable {

        private String hostIp;

        private ConnectHost(String hostIp) {
            this.hostIp = hostIp;
        }

        @Override
        public void run() {
            if(Lan.ping(hostIp)) {
                if(searchInterupt) {
                    return;
                }
                sendAvailableHost(hostIp);
            }
            if(searchInterupt) {
                return;
            }
            addToOverThread();
        }
    }

    private synchronized void addToOverThread() {
        overThread++;
        searchInterupt = overThread == THREAD_TOTAL;
        updateListener.onProgressUpdate(THREAD_TOTAL, overThread);
    }


    private synchronized void sendAvailableHost(String hostIp) {
        String server = mDirSmb + File.separator + hostIp;

        updateListener.onSmbUpdate(server);
    }

    public void interuptSearch() {
        searchInterupt = true;
    }


    public boolean isSearching() {
        return !searchInterupt;
    }


}
