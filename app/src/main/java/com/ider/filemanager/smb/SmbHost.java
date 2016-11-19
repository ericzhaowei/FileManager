package com.ider.filemanager.smb;

import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by ider-eric on 2016/11/1.
 */

public class SmbHost implements Parcelable{
    private String host = null;
    private String username = null;
    private String password = null;

    public void setHost(String host) {
        this.host = host;
    }

    public void setPassword(String password) {
        this.password = password;
    }


    public void setUsername(String username) {
        this.username = username;
    }

    public String getHost() {
        return host;
    }

    public String getPassword() {
        return password;
    }

    public String getUsername() {
        return username;
    }

    public boolean isPublic() {
        return password == null;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {

        Bundle bundle = new Bundle();
        bundle.putString("host", host);
        bundle.putString("username", username);
        bundle.putString("password", password);
        dest.writeBundle(bundle);

    }

    public static final Parcelable.Creator<SmbHost> CREATOR = new Parcelable.Creator<SmbHost>() {
        @Override
        public SmbHost createFromParcel(Parcel source) {
            Bundle bundle = source.readBundle(getClass().getClassLoader());
            SmbHost smbHost = new SmbHost();
            smbHost.setHost(bundle.getString("host"));
            smbHost.setPassword(bundle.getString("password"));
            smbHost.setUsername(bundle.getString("username"));

            return smbHost;
        }

        @Override
        public SmbHost[] newArray(int size) {
            return new SmbHost[size];
        }
    };
}


