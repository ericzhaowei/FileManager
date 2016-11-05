package com.ider.filemanager;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by ider-eric on 2016/11/1.
 */

public class PreferenceManager {

    private SharedPreferences preferences;
    private SharedPreferences.Editor editor;
    public static PreferenceManager manager;

    private PreferenceManager(Context context) {
        preferences = context.getSharedPreferences("smb_preference", Context.MODE_PRIVATE);
        editor = preferences.edit();
    }

    public static PreferenceManager getInstance(Context context) {
        if(manager == null) {
            manager = new PreferenceManager(context);
        }
        return manager;
    }

    public String getString(String key, String defVaule) {
        return preferences.getString(key, defVaule);
    }

    public void putString(String key, String value) {
        editor.putString(key, value);
        editor.commit();
    }



    public void insertIp(String ip) {
        String ips = getString("host", null);
        StringBuilder sb;
        if(ips == null) {
            sb = new StringBuilder();
            sb.append(ip);
        } else {
            sb = new StringBuilder(ips);
            sb.append(":").append(ip);
        }
        putString("host", sb.toString());
    }

    public List<String> queryHost() {
        String ips = getString("host", null);
        if(ips == null || ips.length() == 0) {
            return null;
        }
        String[] ipArray = ips.split(":");
        return Arrays.asList(ipArray);
    }


    public void removeHost(String ip) {
        List<String> list = queryHost();
        if(list == null) {
            return;
        }
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < list.size(); i++) {
            String hostIp = list.get(i);
            if(!hostIp.equals(ip)) {
                sb.append(":").append(hostIp);
            }
        }
        sb.substring(1);
        putString("host", sb.toString());
    }

    public void clearSavedHost() {
        editor.remove("host");
        editor.commit();
    }

}
