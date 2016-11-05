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

public class SmbUtil {

    private static boolean DEBUG = false;
    private static String TAG = "SmbUtil";

    public final static int SMB_SEARCH_UPDATE = 100;
    public final static String mDirSmb = "SMB";

    private ISmbUpdateListener updateListener;
    private boolean searchInterupt;
    private int startThread, overThread;
    private int THREAD_MAX = 16;
    private int THREAD_TOTAL = 0;


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

        THREAD_TOTAL = 0;
        for(int i = 0; i < vectorList.size(); i++) {
            Vector<InetAddress> addrs = vectorList.get(i);
            THREAD_TOTAL += addrs.size();
        }

        for (int i = 0; i < vectorList.size(); i++) {
            if(searchInterupt) {
                return;
            }

            Vector<InetAddress> addrs = vectorList.get(i);

            pingInetAddressLists(addrs);

        }
    }

    private void pingInetAddressLists(List<InetAddress> hosts) {
        for (int i = 0; i < hosts.size();) {
            if(searchInterupt) {
                return;
            }
            int activeThread = startThread - overThread;
            if(activeThread < THREAD_MAX) {
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
//            Log.i("tag", "ping = " + hostIp);
            if(Lan.ping(hostIp)) {
                if(searchInterupt) {
                    return;
                }
                sendAvailableHost(hostIp);
            }
            overThread++;
            searchInterupt = overThread == THREAD_TOTAL;
            updateListener.onProgressUpdate(THREAD_TOTAL, overThread);
        }
    }

    private synchronized void sendAvailableHost(String hostIp) {
        String server = mDirSmb + File.separator + hostIp;

        updateListener.onSmbUpdate(server);
    }

    public void interuptSearch() {
        searchInterupt = true;
        updateListener.onSearchInterupt();
    }


    public boolean isSearching() {
        return !searchInterupt;
    }



    /**
     * 判断指定host地址是否已经挂载
     * @param hostPath 指定host的共享目录,如//192.168.1.100/share
     * @return 如果已挂载，返回挂载点，否则返回null
     */
    public String isMounted(String hostPath) {
        ArrayList<String> mountMsg = CommandExec.execCommand("mount");
        if(mountMsg == null) {
            return null;
        }
        for (String mount : mountMsg) {
            String[] msgs = mount.split(" ");
            if(msgs.length < 3) {
                continue;
            }
            if(msgs[2].equals("cifs")) {
                if(msgs[0].equals(hostPath)) {
                    return msgs[1];
                }
            }
        }
        return null;
    }

    /**
     * 根据指定host获取其共享目录
     * @param smbHost 指定的SmbHost对象
     * @return 该ip下所有的共享目录
     */
    public ArrayList<String> getContentBySmbhost(SmbHost smbHost) {
        String hostIp = smbHost.getHost();
        String username = smbHost.getUsername();
        String password = smbHost.getPassword();
        boolean isPublic = smbHost.isPublic();

        ArrayList<String> contents = new ArrayList<>();
        SmbFile smbFile;
        try{
            if (isPublic) {
                smbFile = new SmbFile(String.format("smb://guest:@%s/", hostIp));
            } else {
                smbFile = new SmbFile(String.format("smb://%s:%s@%s/", username, password, hostIp));

            }
            Log.i("tag", String.format("smb://%s:%s@%s/", username, password, hostIp));
            String[] smbs = smbFile.list();
            for (String smb : smbs) {
                if(!smb.endsWith("$")) {
                    Log.i("tag", "shared : " + smb);
                    contents.add("//" + hostIp + "/" + smb);
                }
            }

            updateListener.onLoginSuccess(smbHost);

            return contents;
        } catch (Exception e) {
            Log.i("tag", e.getMessage());
            if (e.getMessage().contains("unknown user name or bad password")) {
                updateListener.onLoginFailed(smbHost, ISmbUpdateListener.LOGIN_FALIED_WRONG_USERNAME);
            } else {
                updateListener.onLoginFailed(smbHost, ISmbUpdateListener.LOGIN_FAILED_UNKNOWN);
            }
        }

        return null;
    }



}
