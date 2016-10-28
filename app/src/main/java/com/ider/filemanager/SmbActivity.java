package com.ider.filemanager;

import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.ider.filemanager.smb.ISmbView;
import com.ider.filemanager.smb.SmbPresenter;
import com.ider.filemanager.views.FloatImage;
import com.ider.filemanager.views.ItemCardView;
import com.ider.filemanager.views.SmbProgressbar;

import java.util.ArrayList;

/**
 * Created by ider-eric on 2016/10/21.
 */

public class SmbActivity extends FullscreenActivity implements ISmbView {

    SmbProgressbar progressbar;

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
        smbGroup = (RecyclerView) findViewById(R.id.smb_group);
        searchImage = (FloatImage) findViewById(R.id.smb_searching);
        servers = new ArrayList<>();
        adapter = new SmbAdapter(servers);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        smbGroup.setLayoutManager(layoutManager);
        smbGroup.addItemDecoration(new SmbItemDecorator(this));
        smbGroup.setAdapter(adapter);

        progressbar = (SmbProgressbar) findViewById(R.id.smb_progress);

        smbPresenter.searchSmb();

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
            CardView cardView = new ItemCardView(SmbActivity.this);
            return new SmbViewHolder(cardView);
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            CardView cardView = ((SmbViewHolder) holder).cardView;
            TextView title = ((SmbViewHolder) holder).title;
            title.setText(list.get(position));
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
        progressbar.setMax(max);
        progressbar.setProgress(progress);
        if(progress == max) {
            hideSearchProgress();
        }
    }

    @Override
    public void hideSearchProgress() {
        progressbar.setVisibility(View.GONE);
    }
}
