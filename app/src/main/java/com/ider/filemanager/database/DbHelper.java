package com.ider.filemanager.database;

import android.content.Context;
import android.database.DatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by ider-eric on 2016/11/1.
 */

public class DbHelper extends SQLiteOpenHelper {

    private final static String NAME = "cifs.db";
    private final static int VERSION = 1;


    public DbHelper(Context context) {
        super(context, NAME, null, VERSION);
    }

    public DbHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }


    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table if not exists cifs(_id Integer primary key autoincrement, host text, username text, password text)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
