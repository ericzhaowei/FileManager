package com.ider.filemanager.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.ider.filemanager.smb.SmbHost;

/**
 * Created by ider-eric on 2016/11/1.
 */

public class DbManager {
    private DbHelper dbHelper;
    private SQLiteDatabase db;
    private static DbManager manager;

    private DbManager(Context context) {
        dbHelper = new DbHelper(context);
        db = dbHelper.getWritableDatabase();
    }

    public static DbManager getInstance(Context context) {
        if(manager == null) {
            manager = new DbManager(context);
        }
        return manager;
    }

    public void insertCifs(SmbHost host) {
        db.beginTransaction();
        ContentValues cv = new ContentValues();
        cv.put("host", host.getHost());
        cv.put("username", host.getUsername());
        cv.put("password", host.getPassword());
        db.insert("cifs", null, cv);
        db.setTransactionSuccessful();
        db.endTransaction();
    }


    public SmbHost queryCifs(String ip) {
        db.beginTransaction();
        Cursor cursor = db.rawQuery("select * from cifs where host=?", new String[] { ip });
        SmbHost smbHost = null;
        if(cursor.moveToNext()) {
            String username = cursor.getString(2);
            String password = cursor.getString(3);
            smbHost = new SmbHost();
            smbHost.setHost(ip);
            smbHost.setUsername(username);
            smbHost.setPassword(password);
        }
        db.setTransactionSuccessful();
        db.endTransaction();
        return smbHost;
    }

    public void deleteCifs(String ip) {
        db.beginTransaction();
        db.delete("cifs", "host=?", new String[]{ ip });
        db.setTransactionSuccessful();
        db.endTransaction();
    }

}
