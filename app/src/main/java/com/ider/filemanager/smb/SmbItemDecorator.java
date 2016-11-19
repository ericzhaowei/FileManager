package com.ider.filemanager.smb;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;

import com.ider.filemanager.R;

/**
 * Created by ider-eric on 2016/10/26.
 */

public class SmbItemDecorator extends RecyclerView.ItemDecoration {

    private Drawable mDiver;

    public SmbItemDecorator(Context context) {
        mDiver = context.getDrawable(R.drawable.line);
    }


    @Override
    public void onDraw(Canvas c, RecyclerView parent, RecyclerView.State state) {
        // draw diver
        for(int i = 0; i < parent.getChildCount(); i++) {
            Log.i("tag", "onDraw itemDecorator");
            View child = parent.getChildAt(i);
            int top = child.getBottom();
            int bottom = top + mDiver.getIntrinsicHeight();
            mDiver.setBounds(0, top, parent.getWidth(), bottom);
            mDiver.draw(c);
        }
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
        outRect.set(0, 0, 0, 0);
    }
}
