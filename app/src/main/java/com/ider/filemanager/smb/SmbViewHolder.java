package com.ider.filemanager.smb;

import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ider.filemanager.R;

/**
 * Created by ider-eric on 2016/11/17.
 */

public class SmbViewHolder extends RecyclerView.ViewHolder {

    public LinearLayout deviceItem;
    public TextView title;

    public SmbViewHolder(LinearLayout itemView) {
        super(itemView);
        this.deviceItem = itemView;
        this.title = (TextView) deviceItem.findViewById(R.id.smb_server_title);
    }

}
