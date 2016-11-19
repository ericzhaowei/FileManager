package com.ider.filemanager.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.ider.filemanager.FragmentAdapter;
import com.ider.filemanager.R;
import com.ider.filemanager.activity.FullscreenActivity;
import com.ider.filemanager.fragments.FileFragment;
import com.ider.filemanager.fragments.SmbDevicesFragment;
import com.ider.filemanager.smb.ParcelableSmbFile;
import com.ider.filemanager.smb.SmbHost;
import com.ider.filemanager.views.SmbToolTitle;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by ider-eric on 2016/10/21.
 */

public class SmbActivity extends FullscreenActivity {

    String TAG = "SmbActivity";
    boolean DEBUG = true;

    public static final String ACTION_LOGIN_SUCCESS = "samba_login_success";

    public Toolbar toolbar;
    public SmbToolTitle toolTitle;

    public ViewPager viewPager;
    public FragmentAdapter fragmentAdapter;
    public SmbDevicesFragment devicesFragment;
    public FileFragment fileFragment;
    public List<Fragment> fragments;
    public SmbPagerAdapter adapter;

    public Handler mHandler = new Handler();

    private void LOG(String log) {
        if(DEBUG) Log.i(TAG, log);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_smb);
        initViews();

        registReceivers();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unRegistReceivers();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }


    public void registReceivers() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(ACTION_LOGIN_SUCCESS);
        registerReceiver(loginReceiver, filter);
    }

    public void unRegistReceivers() {
        unregisterReceiver(loginReceiver);
    }


    public void initViews() {
        toolbar = (Toolbar) findViewById(R.id.smb_toolbar);
        toolTitle = (SmbToolTitle) toolbar.findViewById(R.id.smb_toolbar_title_view);
        viewPager = (ViewPager) findViewById(R.id.content_pager);

        setupToolbar();
        setupViewPager();
    }

    public void setupViewPager() {
        fragments = new ArrayList<>();
        devicesFragment = new SmbDevicesFragment();
        fragments.add(devicesFragment);
        adapter = new SmbPagerAdapter(getSupportFragmentManager(), fragments);
        viewPager.setAdapter(adapter);
    }

    public void setupToolbar() {
        toolbar.setNavigationIcon(R.drawable.ic_format_list_bulleted_white_24dp);
        toolbar.inflateMenu(R.menu.smb_toolbar_menu);
        setMenuEnable(toolbar.getMenu());
    }

    public void setMenuEnable(Menu menu) {
        try {

            Class cls = Class.forName("android.support.v7.view.menu.MenuBuilder");
            Method method = cls.getDeclaredMethod("setOptionalIconsVisible", boolean.class);
            method.setAccessible(true);
            method.invoke(menu, true);

        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }


    class SmbPagerAdapter extends FragmentPagerAdapter {

        List<Fragment> list;

        public SmbPagerAdapter(FragmentManager fm, List<Fragment> list) {
            super(fm);
            this.list = list;
        }

        @Override
        public Fragment getItem(int position) {
            return list.get(position);
        }

        @Override
        public int getCount() {
            return list.size();
        }
    }

    public void menuRefreshClicked(MenuItem item) {
        devicesFragment.menuRefresh();
    }

    public void menuStopClicked(MenuItem item) {
        devicesFragment.menuStopSearch();
    }


    @Override
    public void onBackPressed() {
        if(!devicesFragment.onBackPressed()) {
            super.onBackPressed();
        }
    }

    BroadcastReceiver loginReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            SmbHost smbHost = intent.getParcelableExtra("smbhost");
            ArrayList<ParcelableSmbFile> sharedFolders = intent.getParcelableArrayListExtra("folders");
            LOG(sharedFolders.get(0).getName());
            if(fileFragment == null) {
                fileFragment = new FileFragment();
                Bundle bundle = new Bundle();
                bundle.putParcelable("smbhost", smbHost);
                bundle.putParcelableArrayList("folders", sharedFolders);
                fileFragment.setArguments(bundle);
                fragments.add(fileFragment);
            }
            adapter.notifyDataSetChanged();
            viewPager.setCurrentItem(1, true);

        }
    };

}
