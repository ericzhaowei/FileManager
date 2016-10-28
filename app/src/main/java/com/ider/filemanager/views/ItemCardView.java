package com.ider.filemanager.views;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.v7.widget.CardView;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.TextView;

import com.ider.filemanager.R;

/**
 * Created by ider-eric on 2016/10/27.
 */

public class ItemCardView extends CardView {

    public ImageView mainIcon;
    public TextView mainText;
    private String title;

    public ItemCardView(Context context) {
        super(context);
        initViews();
    }

    public ItemCardView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initViews();
        TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.ItemCardView);
        int imageRes = array.getResourceId(R.styleable.ItemCardView_mainImage, 0);
        title = getResources().getString(array.getResourceId(R.styleable.ItemCardView_mainTitle, 0));
        mainIcon.setImageResource(imageRes);
        mainText.setText(title);
    }

    public void initViews() {
        LayoutInflater.from(getContext()).inflate(R.layout.smb_server_item, this);
        mainIcon = (ImageView) findViewById(R.id.smb_server_image);
        mainText = (TextView) findViewById(R.id.smb_server_title);
    }

    public String getTitle() {
        return title;
    }



}
