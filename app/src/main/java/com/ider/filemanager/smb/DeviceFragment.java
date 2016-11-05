package com.ider.filemanager.smb;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.ider.filemanager.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ider-eric on 2016/11/3.
 */

public class DeviceFragment extends Fragment {

    private RecyclerView deviceList;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_smbdevice, container, false);
        deviceList = (RecyclerView) view.findViewById(R.id.smb_group);


        return view;

    }

    public void setupRecyclerView() {

    }



    class SmbAdapter extends RecyclerView.Adapter {

        ArrayList<String> list;

        public SmbAdapter(ArrayList<String> list) {
            this.list = list;
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            CardView cardView = (CardView) LayoutInflater.from(parent.getContext()).inflate(R.layout.smb_server_item, parent, false);
            return new SmbViewHolder(cardView);
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            SmbViewHolder smbViewHolder = (SmbViewHolder) holder;
            smbViewHolder.host.setText(list.get(position));
        }

        @Override
        public int getItemCount() {
            return 0;
        }
    }

    class SmbViewHolder extends RecyclerView.ViewHolder {

        TextView host;

        public SmbViewHolder(View itemView) {
            super(itemView);
            host = (TextView) itemView.findViewById(R.id.smb_server_title);
        }
    }

}
