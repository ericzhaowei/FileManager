package com.ider.filemanager;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.RecyclerView;
import android.view.View;

/**
 * Created by ider-eric on 2016/10/26.
 */

public class SmbItemDecorator extends RecyclerView.ItemDecoration {

    private Drawable mDiver;

    public SmbItemDecorator(Context context) {
        int[] attrs = new int[]{android.R.attr.listDivider};
        TypedArray ta = context.obtainStyledAttributes(attrs);
        mDiver = ta.getDrawable(0);
        ta.recycle();
    }


    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        /**
         * outRect表示item外围的偏移量
         * left:item左边留出的长度
         * top:item上边留出的长度
         * right:item右边留出的长度
         * bottom:item下边留出的宽度
         */
        outRect.set(30, 20, 30, 20);
    }
}
