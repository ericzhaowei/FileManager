package com.ider.filemanager.views;

import android.content.Context;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.ider.filemanager.R;

/**
 * Created by ider-eric on 2016/11/2.
 */

public class SmbToolTitle extends FrameLayout {

    String TAG = "SmbToolTitle";
    boolean DEBUG = true;

    TextView title;
    SmbProgressbar progressbar;

    private void LOG(String log) {
        if(DEBUG) Log.i(TAG, log);
    }

    public SmbToolTitle(Context context) {
        this(context, null);
    }

    public SmbToolTitle(Context context, AttributeSet attrs) {
        super(context, attrs);
        initViews();
    }


    public void initViews() {
        LayoutInflater.from(getContext()).inflate(R.layout.smb_tootbar_title_progress, this);
        title = (TextView) findViewById(R.id.smb_toolbar_title);
        progressbar = (SmbProgressbar) findViewById(R.id.smb_toolbar_progress);
    }

    public void showProgress() {
        title.setVisibility(View.GONE);
        progressbar.setVisibility(View.VISIBLE);
    }

    public void hideProgress() {
        title.setVisibility(View.VISIBLE);
        progressbar.setVisibility(View.GONE);
    }

    public void setMax(int max) {
        progressbar.setMax(max);
    }

    public void setProgress(int progress) {
        LOG("setProgress");
        progressbar.setProgress(progress);
    }

}
