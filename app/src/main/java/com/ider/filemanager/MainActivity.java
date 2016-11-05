package com.ider.filemanager;

import android.content.Intent;
import android.os.Handler;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.ider.filemanager.smb.ISmbView;
import com.ider.filemanager.smb.SmbHost;
import com.ider.filemanager.smb.SmbPresenter;

import java.net.MalformedURLException;

import jcifs.smb.SmbFile;
import rx.Observable;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

public class MainActivity extends FullscreenActivity implements View.OnClickListener {

    Button samba;
    SmbPresenter smbPresenter;
    Handler mHandler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initViews();
        setListeners();

    }

    public void initViews() {
        samba = (Button) findViewById(R.id.main_samba);

    }

    public void setListeners() {
        samba.setOnClickListener(this);
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.main_samba:
                Intent intent = new Intent(MainActivity.this, SmbActivity.class);
                startActivity(intent);
                break;
        }
    }

}
