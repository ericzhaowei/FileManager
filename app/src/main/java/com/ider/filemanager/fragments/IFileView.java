package com.ider.filemanager.fragments;

import android.content.Context;
import android.support.v7.widget.RecyclerView;

/**
 * Created by ider-eric on 2016/11/19.
 */

public interface IFileView {

    public void setAdapter(RecyclerView.Adapter adapter);
    public Context getcontext();
    public void showToast(int resId);

}
