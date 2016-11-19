package com.ider.filemanager.fragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.ider.filemanager.R;
import com.ider.filemanager.activity.SmbActivity;
import com.ider.filemanager.smb.SmbItemDecorator;
import com.ider.filemanager.presenters.ISmbPresenter;
import com.ider.filemanager.smb.ISmbView;
import com.ider.filemanager.presenters.SmbPresenter;
import com.ider.filemanager.views.FloatImage;

/**
 * Created by ider-eric on 2016/11/18.
 */

public class SmbDevicesFragment extends Fragment implements ISmbView{

    private String TAG = "SmbDevicesFragment";
    private boolean DEBUG = true;
    private void LOG(String log) {
        if(DEBUG) Log.i(TAG, log);
    }

    public int currentProgress=0;
    public int maxProgress;

    RecyclerView smbGroup;
    FloatImage searchImage;
    TextView description;

    ISmbPresenter smbPresenter;
    SmbActivity activity;
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = (SmbActivity) getActivity();
        smbPresenter = new SmbPresenter(this, activity.mHandler);

        if(savedInstanceState != null) {
            currentProgress = savedInstanceState.getInt("progress");
            maxProgress = savedInstanceState.getInt("maxProgress");
        }


    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_smb_devices, container, false);
        findViewByIds(view);
        setupRecyclerView();
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();

        activity.toolTitle.setMax(maxProgress);
        activity.toolTitle.setProgress(currentProgress);

        smbPresenter.searchSmb(currentProgress);
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        smbPresenter.stopSearch();
    }


    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putInt("progress", currentProgress);
        outState.putInt("maxProgress", maxProgress);
        super.onSaveInstanceState(outState);
    }

    private void findViewByIds(View root) {
        description = (TextView) root.findViewById(R.id.device_page_description);
        searchImage = (FloatImage) root.findViewById(R.id.smb_searching);
        smbGroup = (RecyclerView) root.findViewById(R.id.smb_group);
    }

    public void setupRecyclerView() {
        smbPresenter.initSmbDevices();
        LinearLayoutManager layoutManager = new LinearLayoutManager(activity);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        smbGroup.setLayoutManager(layoutManager);
        smbGroup.addItemDecoration(new SmbItemDecorator(activity));
    }


    @Override
    public void showToast(String message) {
        Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void showToast(int resId) {
        Toast.makeText(getActivity(), resId, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void hideSearchingFloat() {
        searchImage.setVisibility(View.GONE);
    }

    @Override
    public void showSearchingFloat() {
        searchImage.setVisibility(View.VISIBLE);
        searchImage.startFloat(R.drawable.ic_search_grey_400_48dp, R.string.smb_searching);
        searchImage.setOnClickListener(null);
    }

    @Override
    public void hideSearchProgress() {
        activity.toolTitle.hideProgress();
    }

    @Override
    public void showSearchProgress() {
        activity.toolTitle.showProgress();
    }

    @Override
    public void updateProgress(int max, int progress) {
        this.maxProgress = max;
        this.currentProgress = progress;
        activity.toolTitle.setMax(max);
        activity.toolTitle.setProgress(progress);
        if (progress == max) {
            smbPresenter.stopSearch();
        }
    }

    @Override
    public void showNoResultView() {
        searchImage.setVisibility(View.VISIBLE);
        searchImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                currentProgress = 0;
                smbPresenter.searchSmb(currentProgress);
            }
        });

        searchImage.stopFloat(R.drawable.ic_youtube_searched_for_grey_400_48dp, R.string.smb_noresult);
    }

    @Override
    public void showLoginDialog(final String ip) {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setView(R.layout.smb_login_dialog);

        final AlertDialog dialog = builder.create();
        dialog.show();
        View view = dialog.getWindow().getDecorView();
        final EditText editUser = (EditText) view.findViewById(R.id.edit_username);
        final EditText editPassword = (EditText) view.findViewById(R.id.edit_password);
        Button login = (Button) view.findViewById(R.id.smb_login);
        Button cancel = (Button) view.findViewById(R.id.smb_cancel);

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.cancel();
            }
        });

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = editUser.getText().toString();
                String password = editPassword.getText().toString();
                if(username.length() == 0) {
                    showToast(R.string.smb_invalid_username);
                } else if(password.length() == 0) {
                    showToast(R.string.smb_invalid_password);
                } else {
                    smbPresenter.getSmbContent(ip, username, password);
                    dialog.cancel();
                }
            }
        });
    }

    @Override
    public void setPageDescription(int resId) {
        description.setVisibility(View.VISIBLE);
        description.setText(resId);
    }

    @Override
    public void hidePageDescription() {
        description.setVisibility(View.GONE);
    }

    @Override
    public void showWifiError() {
        searchImage.setVisibility(View.VISIBLE);
        searchImage.stopFloat(R.drawable.ic_signal_wifi_off_grey_500_48dp, R.string.smb_connect_error);
        searchImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(android.provider.Settings.ACTION_SETTINGS);
                startActivity(intent);
            }
        });
    }

    @Override
    public Context getcontext() {
        return activity.getApplicationContext();
    }

    @Override
    public void setSmbAdapter(RecyclerView.Adapter adapter) {
        Log.i("SmbPresenter", "setSmbAdapter , " + adapter.getItemCount());
        smbGroup.setAdapter(adapter);
    }

    @Override
    public void notifyAdapter(RecyclerView.Adapter adapter) {
        adapter.notifyDataSetChanged();
    }

    public void menuRefresh() {
        currentProgress = 0;
        smbPresenter.refreshHost();
    }

    public void menuStopSearch() {
        currentProgress = 0;
        smbPresenter.stopSearch();
    }

    public boolean onBackPressed() {
        if(smbPresenter.isSearching()) {
            menuStopSearch();
            return true;
        }
        return false;
    }

}
