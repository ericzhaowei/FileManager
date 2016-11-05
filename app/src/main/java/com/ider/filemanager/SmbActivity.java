package com.ider.filemanager;

import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.ider.filemanager.smb.ISmbView;
import com.ider.filemanager.smb.SmbPresenter;
import com.ider.filemanager.views.FloatImage;
import com.ider.filemanager.views.SmbToolTitle;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;

/**
 * Created by ider-eric on 2016/10/21.
 */

public class SmbActivity extends FullscreenActivity implements ISmbView {

    Toolbar toolbar;
    SmbToolTitle toolTitle;
    TextView description;
    ViewPager viewPager;
    FragmentAdapter fragmentAdapter;


    RecyclerView smbGroup;
    SmbAdapter adapter;
    ArrayList<String> servers;
    FloatImage searchImage;
    SmbPresenter smbPresenter;
    Handler mHandler = new Handler();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_smb);

        smbPresenter = new SmbPresenter(this, mHandler);

        initViews();
        smbPresenter.searchSmb();

    }


    public void initViews() {
        // findViewbyId
        toolbar = (Toolbar) findViewById(R.id.smb_toolbar);
        toolTitle = (SmbToolTitle) toolbar.findViewById(R.id.smb_toolbar_title_view);
        description = (TextView) findViewById(R.id.page_description);
        searchImage = (FloatImage) findViewById(R.id.smb_searching);
        smbGroup = (RecyclerView) findViewById(R.id.smb_group);
        viewPager = (ViewPager) findViewById(R.id.content_pager);

        // setup views
        setupToolbar();
        setupRecyclerView();
        setupViewPager();
    }

    public void setupViewPager() {

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


    public void setupRecyclerView() {
        servers = new ArrayList<>();
        adapter = new SmbAdapter(servers);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        smbGroup.setLayoutManager(layoutManager);
        smbGroup.addItemDecoration(new SmbItemDecorator(this));
        smbGroup.setAdapter(adapter);
    }


    public void refreshClicked(MenuItem item) {
        if(!smbPresenter.isSearching()) {
            servers.clear();
            adapter.notifyDataSetChanged();
            smbPresenter.clearSavedHost();
            smbPresenter.searchSmb();
        } else {
            showToast(R.string.smb_refresh_wait);
        }

    }

    @Override
    public void startSearch() {
        toolTitle.showProgress();
        searchImage.setVisibility(View.VISIBLE);
        searchImage.startFloat(R.string.smb_searching);
        searchImage.setOnClickListener(null);
    }

    @Override
    public void stopSearch() {
        hideSearchProgress();
        if(servers.size() == 0) {
            showNoResult();
        }
    }

    @Override
    public void showNoResult() {

        searchImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                smbPresenter.searchSmb();
            }
        });

        searchImage.stopFloat(R.string.smb_noresult);
    }

    public void addServerItem(String server) {
        servers.add(server);
        adapter.notifyDataSetChanged();
    }


    class SmbAdapter extends RecyclerView.Adapter {

        ArrayList<String> list;

        private SmbAdapter(ArrayList<String> list) {
            this.list = list;
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            CardView cardView = (CardView) LayoutInflater.from(SmbActivity.this).inflate(R.layout.smb_server_item, parent, false);
            return new SmbViewHolder(cardView);
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            final String ip = list.get(position).substring(4);
            CardView cardView = ((SmbViewHolder) holder).cardView;
            TextView title = ((SmbViewHolder) holder).title;
            title.setText(list.get(position));
            cardView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    smbPresenter.getSmbContent(ip);
                }
            });
        }

        @Override
        public int getItemCount() {
            return list.size();
        }
    }

    class SmbViewHolder extends RecyclerView.ViewHolder {
        CardView cardView;
        TextView title;

        SmbViewHolder(CardView itemView) {
            super(itemView);
            this.cardView = itemView;
            this.title = (TextView) cardView.findViewById(R.id.smb_server_title);
        }
    }


    @Override
    public void smbServerUpdate(String server) {
        servers.add(server);
        adapter.notifyDataSetChanged();
    }

    @Override
    public void hideSearchView() {
        searchImage.setVisibility(View.GONE);
    }

    @Override
    public void updateProgress(int max, int progress) {
        toolTitle.setMax(max);
        toolTitle.setProgress(progress);
        if (progress == max) {
            stopSearch();
        }
    }

    public void hideSearchProgress() {
        toolTitle.hideProgress();
    }



    @Override
    public void onBackPressed() {
        if(smbPresenter.isSearching()) {
            smbPresenter.stopSearch();
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public void showToast(String toast) {
        Toast.makeText(this, toast, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void showToast(int resId) {
        Toast.makeText(this, resId, Toast.LENGTH_SHORT).show();
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
    public void showLoginDialog(final String ip) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
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

}
