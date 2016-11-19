package com.ider.filemanager;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

/**
 * Created by ider-eric on 2016/11/19.
 */

public class FileItemViewHolder extends RecyclerView.ViewHolder{

    RelativeLayout itemView;
    public ImageView icon;
    public TextView name;
    public TextView info;


    public FileItemViewHolder(View itemView) {
        super(itemView);
        this.itemView = (RelativeLayout) itemView;
        this.icon = (ImageView) itemView.findViewById(R.id.file_icon);
        this.name = (TextView) itemView.findViewById(R.id.file_name);
        this.info = (TextView) itemView.findViewById(R.id.file_info);
    }
}
