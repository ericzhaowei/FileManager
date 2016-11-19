package com.ider.filemanager.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.ider.filemanager.R;
import com.ider.filemanager.activity.SmbActivity;
import com.ider.filemanager.presenters.FilePresenter;
import com.ider.filemanager.presenters.IFilePresenter;
import com.ider.filemanager.smb.SmbHost;
import com.ider.filemanager.smb.SmbItemDecorator;

/**
 * Created by ider-eric on 2016/11/18.
 */

public class FileFragment extends Fragment implements IFileView {

    private boolean DEBUG = true;
    private String TAG = "FileFragment";
    private void LOG(String log) {
        Log.i(TAG, log);
    }

    private IFilePresenter filePresenter;

    private SmbHost smbHost;
    private TextView pageTitle;
    private RecyclerView fileGroup;
    private SmbActivity activity;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = (SmbActivity) getActivity();
        smbHost = getArguments().getParcelable("smbhost");
        filePresenter = new FilePresenter(this);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_file_list, container, false);

        findViewByIds(view);

        filePresenter.parseArguments(getArguments());

        return view;

    }

    public void findViewByIds(View view) {
        pageTitle = (TextView) view.findViewById(R.id.file_page_description);
        pageTitle.setText(smbHost.getHost());
        fileGroup = (RecyclerView) view.findViewById(R.id.file_group);
        setupRecyclerView();
    }

    public void setupRecyclerView() {
        LinearLayoutManager manager = new LinearLayoutManager(activity);
        manager.setOrientation(LinearLayoutManager.VERTICAL);
        fileGroup.setLayoutManager(manager);
        fileGroup.addItemDecoration(new SmbItemDecorator(activity));
    }


    @Override
    public void setAdapter(RecyclerView.Adapter adapter) {
        fileGroup.setAdapter(adapter);
    }

    @Override
    public Context getcontext() {
        return activity;
    }

    @Override
    public void showToast(int resId) {
        Toast.makeText(activity, resId, Toast.LENGTH_SHORT).show();
    }
}
