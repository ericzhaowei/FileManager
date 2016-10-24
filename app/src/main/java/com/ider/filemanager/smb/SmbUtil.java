package com.ider.filemanager.smb;

import android.util.Log;

import java.io.File;
import java.net.InetAddress;
import java.util.Vector;

/**
 * Created by ider-eric on 2016/10/21.
 */

public class SmbUtil {

    private static boolean DEBUG = true;
    private static String TAG = "SmbUtil";

    public final static int SMB_SEARCH_UPDATE = 100;
    public final static String mDirSmb = "SMB";

    private ISmbUpdateListener updateListener;

    private boolean searchInterupt;
    private int startThread, overThread;
    private int THREAD_MAX = 16;

    private static void LOG(String str) {
        if (DEBUG) {
            Log.d(TAG, str);
        }
    }

    public SmbUtil(ISmbUpdateListener updateListener) {
        this.updateListener = updateListener;
    }


    public void searchSmbHost() {
        searchInterupt = false;
        startThread = 0;
        overThread = 0;
        Vector<Vector<InetAddress>> vectorList = Lan.getSubnetAddress();
        Log.i("tag", "vectorlist.size = " + vectorList.size());
        for (int i = 0; i < vectorList.size(); i++) {
            if(searchInterupt) return;
            Vector<InetAddress> addrs = vectorList.get(i);
            for (int j = 0; j < addrs.size(); ) {
                if(searchInterupt) return;
                int activeThread = startThread - overThread;
                if(activeThread < THREAD_MAX) {
                    InetAddress addr = addrs.get(j);
                    Thread scan = new Thread(new ConnectHost(addr));
                    scan.setPriority(10);
                    scan.start();
                    startThread++;
                    j++;
                } else {
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }


    private class ConnectHost implements Runnable {

        private InetAddress addr;

        private ConnectHost(InetAddress addr) {
            this.addr = addr;
        }

        @Override
        public void run() {
            if(Lan.ping(addr.getHostAddress())) {
                if(searchInterupt) return;
                sendAvailableHost(addr);
            }
            overThread++;
        }
    }

    private synchronized void sendAvailableHost(InetAddress iNetAddress) {
        LOG("ping OK : " + iNetAddress.getHostAddress());
        String server = mDirSmb + File.separator + iNetAddress.getHostAddress();

        updateListener.onSmbUpdate(server);
    }
}
