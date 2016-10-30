package com.ider.filemanager.views;

import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;

import com.ider.filemanager.DimenUtil;
import com.ider.filemanager.R;

/**
 * Created by ider-eric on 2016/10/27.
 */

public class FloatImage extends ImageView {

    private int DEFAULT_RADIUS = 33;  //dp
    private int DEFAULT_IMAGE_WIDTH = 0; //dp
    private int DEFAULT_IMAGE_HEIGHT = 0; //dp
    private int DEFAULT_TEXT_COLOR = 0x000000;
    private int DEFAULT_TEXT_SIZE = 13; //sp

    private int mRadius;
    private int mImageWidth;
    private int mImageHeight;
    private int mTextColor;
    private int mTextSize;

    private ValueAnimator animator;
    private String text;
    private Paint textPaint;
    private Rect textBounds;

    public FloatImage(Context context) {
        super(context);
        setupAnimator();

    }

    public FloatImage(Context context, AttributeSet attrs) {
        super(context, attrs);
        TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.FloatImage);
        mRadius = (int) array.getDimension(R.styleable.FloatImage_radius, DimenUtil.dp2px(context.getResources(), DEFAULT_RADIUS));
        mImageWidth = (int) array.getDimension(R.styleable.FloatImage_image_width, DimenUtil.dp2px(context.getResources(), DEFAULT_IMAGE_WIDTH));
        mImageHeight = (int) array.getDimension(R.styleable.FloatImage_image_height, DimenUtil.dp2px(context.getResources(), DEFAULT_IMAGE_HEIGHT));

        int textId = array.getResourceId(R.styleable.FloatImage_text, 0);
        if(textId != 0) {
            text = getResources().getString(textId);
            mTextColor = array.getColor(R.styleable.FloatImage_textColor, DEFAULT_TEXT_COLOR);
            mTextSize = (int) array.getDimension(R.styleable.FloatImage_textSize, DimenUtil.sp2px(context.getResources(), DEFAULT_TEXT_SIZE));
            setupTextPaint();
        }
        setupAnimator();

    }

    private void setupTextPaint() {
        textPaint = new Paint();
        textPaint.setTypeface(Typeface.DEFAULT_BOLD);
        textPaint.setTextSize(mTextSize);
        textPaint.setColor(mTextColor);
        textBounds = new Rect();
        textPaint.getTextBounds(text, 0, text.length(), textBounds);
    }

    private void setupAnimator() {
        // 0~360代表角度
        animator = ValueAnimator.ofFloat(0, 360);
        animator.setDuration(1000);
        animator.setInterpolator(new LinearInterpolator());
        animator.addUpdateListener(animatorUpdateListener);
        animator.setRepeatCount(ValueAnimator.INFINITE);
        startFloat();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

        setMeasuredDimension(mImageWidth + 2*mRadius, mImageHeight + 2*mRadius + textBounds.height());
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if(textPaint == null) {
            return;
        }

        int width = textBounds.width();
        int height = textBounds.height();

        int x = (getWidth() - width) / 2;
        int y = getHeight() - 5;

        canvas.drawText(text, 0, text.length(), x, y, textPaint);

    }

    ValueAnimator.AnimatorUpdateListener animatorUpdateListener = new ValueAnimator.AnimatorUpdateListener() {
        @Override
        public void onAnimationUpdate(ValueAnimator valueAnimator) {
            float value = (float) valueAnimator.getAnimatedValue();

            float left = mRadius * (1 + (float) Math.sin(Math.toRadians(value)));
            float top = mRadius * (1 - (float) Math.cos(Math.toRadians(value)));
            float right = getWidth() - mImageWidth - left;
            float bottom = getHeight() - mImageHeight - top;
            setPadding((int) left, (int) top, (int) right, (int) bottom);
        }
    };


    public void startFloat() {
        animator.start();
    }

    public void stopFloat() {
        animator.pause();
    }

}
