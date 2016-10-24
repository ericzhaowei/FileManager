package com.ider.filemanager;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.ider.filemanager.FullscreenActivity;

import java.util.ArrayList;

/**
 * Created by ider-eric on 2016/10/21.
 */

public class SmbActivity extends FullscreenActivity {

    RecyclerView smbGroup;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_smb);

        smbGroup = (RecyclerView) findViewById(R.id.smb_group);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        smbGroup.setLayoutManager(layoutManager);
        SmbAdapter adapter = new SmbAdapter(getData());
        smbGroup.setAdapter(adapter);

    }


    public ArrayList<String> getData() {
        ArrayList<String> list = new ArrayList<>();
        list.add("adbc");
        list.add("jack");
        list.add("ider");
        list.add("eric");
        list.add("worker");
        return list;
    }


    class SmbAdapter extends RecyclerView.Adapter {

        ArrayList<String> list;
        private SmbAdapter(ArrayList<String> list) {
            this.list = list;
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            CardView cardView = (CardView) LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.smb_server_item, parent, false);
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

}
