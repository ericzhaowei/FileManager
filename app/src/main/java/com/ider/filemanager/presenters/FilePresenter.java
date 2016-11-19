package com.ider.filemanager.presenters;

import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import com.ider.filemanager.FileItemViewHolder;
import com.ider.filemanager.R;
import com.ider.filemanager.fragments.IFileView;
import com.ider.filemanager.smb.ISmb;
import com.ider.filemanager.smb.ParcelableSmbFile;

import java.io.File;
import java.util.ArrayList;

/**
 * Created by ider-eric on 2016/11/19.
 */

public class FilePresenter implements IFilePresenter, ISmb.ISmbMountListener {

    private ArrayList<ParcelableSmbFile> sharedFiles;
    private IFileView fileView;
    SmbFileAdapter fileAdapter;

    public FilePresenter(IFileView fileView) {
        this.fileView = fileView;
    }

    @Override
    public void parseArguments(Bundle bundle) {
        sharedFiles = bundle.getParcelableArrayList("folders");
        fileAdapter = new SmbFileAdapter(sharedFiles);
        fileView.setAdapter(fileAdapter);
    }


    public class SmbFileAdapter extends RecyclerView.Adapter {

        ArrayList<ParcelableSmbFile> list;
        public SmbFileAdapter(ArrayList<ParcelableSmbFile> list) {
            this.list = list;
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            RelativeLayout itemView = (RelativeLayout) LayoutInflater.from(parent.getContext()).inflate(R.layout.file_item, parent, false);
            return new FileItemViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position) {
            FileItemViewHolder holder = (FileItemViewHolder) viewHolder;
            ParcelableSmbFile smbFile = list.get(position);
            holder.name.setText(smbFile.getName());
            holder.info.setText(R.string.smb_shared_folder_subtitle);
            if(smbFile.isDirectory()) {
                holder.icon.setImageResource(R.drawable.ic_folder_orange_500_24dp);
            } else {
                holder.icon.setImageResource(R.drawable.ic_signal_wifi_off_grey_500_48dp);
            }
        }

        @Override
        public int getItemCount() {
            return list.size();
        }

    }

    public class FileAdapter extends RecyclerView.Adapter {
        ArrayList<File> list;
        public FileAdapter(ArrayList<File> list) {
            this.list = list;
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            RelativeLayout itemView = (RelativeLayout) LayoutInflater.from(parent.getContext()).inflate(R.layout.file_item, parent, false);
            return new FileItemViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position) {
            FileItemViewHolder holder = (FileItemViewHolder) viewHolder;
            File file = list.get(position);
            holder.name.setText(file.getName());
            holder.info.setText(createFileInfo(file));
        }

        @Override
        public int getItemCount() {
            return list.size();
        }

        private String createFileInfo(File file) {
            StringBuilder sb = new StringBuilder();
            String createTime = String.valueOf(file.lastModified());
            if(file.isDirectory()) {
                String childCountFormat = fileView.getcontext().getResources().getString(R.string.folder_child_count);
                String childCount = String.format(childCountFormat, file.list().length);
                sb.append(childCount);
            } else {
                String size = String.valueOf(file.length()) + "|";
                sb.append(size);
            }
            sb.append(createTime);
            return sb.toString();

        }
    }

    @Override
    public void onMountFailed(int message) {
        fileView.showToast(R.string.smb_mount_mountpoint_error);
    }

    @Override
    public void onMountSuccess() {

    }
}
