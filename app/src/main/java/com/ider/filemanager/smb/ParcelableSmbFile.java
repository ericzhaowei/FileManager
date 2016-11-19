package com.ider.filemanager.smb;

import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;

import jcifs.smb.SmbException;
import jcifs.smb.SmbFile;

/**
 * Created by ider-eric on 2016/11/19.
 */

public class ParcelableSmbFile implements Parcelable {

    private String name;
    private long createTime;
    private boolean canRead;
    private boolean canWrite;
    private boolean isDirectory;

    public ParcelableSmbFile() {};

    public ParcelableSmbFile(SmbFile file) {
        this.name = file.getName();
        try {
            this.createTime = file.createTime();
            this.canRead = file.canRead();
            this.canWrite = file.canWrite();
            this.isDirectory = file.isDirectory();
        } catch (SmbException e) {
            e.printStackTrace();
        }
    }

    public String getName() {
        return name;
    }

    public long createTime() {
        return createTime;
    }

    public boolean canRead() {
        return canRead;
    }

    public boolean canWrite() {
        return canWrite;
    }

    public boolean isDirectory() {
        return isDirectory;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        Bundle bundle = new Bundle();
        bundle.putString("name", name);
        bundle.putLong("createtime", createTime);
        bundle.putBoolean("canread", canRead);
        bundle.putBoolean("canwrite", canWrite);
        bundle.putBoolean("isdirectory", isDirectory);
        dest.writeBundle(bundle);

    }

    public static final Parcelable.Creator<ParcelableSmbFile> CREATOR = new Parcelable.Creator<ParcelableSmbFile>() {
        @Override
        public ParcelableSmbFile[] newArray(int size) {
            return new ParcelableSmbFile[size];
        }

        @Override
        public ParcelableSmbFile createFromParcel(Parcel source) {
            Bundle bundle = source.readBundle(getClass().getClassLoader());
            ParcelableSmbFile smbFile = new ParcelableSmbFile();
            smbFile.name = bundle.getString("name");
            smbFile.createTime = bundle.getLong("createtime");
            smbFile.canRead = bundle.getBoolean("canread");
            smbFile.canWrite = bundle.getBoolean("canwrite");
            smbFile.isDirectory = bundle.getBoolean("isdirectory");
            return smbFile;
        }
    };
}
