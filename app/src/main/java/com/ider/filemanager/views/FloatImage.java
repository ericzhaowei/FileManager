package com.ider.filemanager.views;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;

import com.ider.filemanager.DimenUtil;
import com.ider.filemanager.R;

/**
 * Created by ider-eric on 2016/10/27.
 */

public class FloatImage extends ImageView {

    private boolean DEBUG = true;
    private String TAG = "FloatImage";

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
    private int mTextWidth;


    private ValueAnimator animator;
    private String text;
    private Paint textPaint;
    private int paddingLeft, paddingTop, paddingRight, paddingBottom;


    private void LOG(String log) {
        if(DEBUG) Log.i(TAG, log);
    }

    public FloatImage(Context context) {
        this(context, null);
    }

    public FloatImage(Context context, AttributeSet attrs) {
        super(context, attrs);
        TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.FloatImage);
        if (array != null) {
            mRadius = (int) array.getDimension(R.styleable.FloatImage_radius, DimenUtil.dp2px(context.getResources(), DEFAULT_RADIUS));
            mImageWidth = (int) array.getDimension(R.styleable.FloatImage_image_width, DimenUtil.dp2px(context.getResources(), DEFAULT_IMAGE_WIDTH));
            mImageHeight = (int) array.getDimension(R.styleable.FloatImage_image_height, DimenUtil.dp2px(context.getResources(), DEFAULT_IMAGE_HEIGHT));

            int textId = array.getResourceId(R.styleable.FloatImage_text, 0);
            if (textId != 0) {
                text = getResources().getString(textId);
                mTextColor = array.getColor(R.styleable.FloatImage_textColor, DEFAULT_TEXT_COLOR);
                mTextSize = (int) array.getDimension(R.styleable.FloatImage_textSize, DimenUtil.sp2px(context.getResources(), DEFAULT_TEXT_SIZE));
                mTextWidth = (int) array.getDimension(R.styleable.FloatImage_textWidth, mImageWidth + 2 * mRadius);
                setupTextPaint();
            }
            array.recycle();
        }
        setupAnimator();

    }


    private void setupTextPaint() {
        textPaint = new Paint();
        textPaint.setTypeface(Typeface.DEFAULT_BOLD);
        textPaint.setTextSize(mTextSize);
        textPaint.setColor(mTextColor);
    }

    private void setupAnimator() {
        // 0~360代表角度
        animator = ValueAnimator.ofFloat(0, 360);
        animator.setDuration(1000);
        animator.setInterpolator(new LinearInterpolator());
        animator.addUpdateListener(animatorUpdateListener);
        animator.addListener(animatorListener);
        animator.setRepeatCount(ValueAnimator.INFINITE);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int width = Math.max(mImageWidth + 2 * mRadius, mTextWidth);
        int height = (int) (mImageHeight + 2 * mRadius + textPaint.descent() - textPaint.ascent());
        setMeasuredDimension(width, height);
        setupInitPaddings();
    }


    @Override
    protected void onDraw(Canvas canvas) {
        Bitmap bitmap = ((BitmapDrawable)getDrawable()).getBitmap();
        canvas.drawBitmap(bitmap, paddingLeft, paddingTop, textPaint);

        if (textPaint == null) {
            return;
        }

        float width = textPaint.measureText(text);
        float height = textPaint.ascent() + textPaint.descent();

        int x = (int) ((getWidth() - width) / 2);
        int y = getHeight() - (int) textPaint.descent();

        canvas.drawText(text, 0, text.length(), x, y, textPaint);

    }


    ValueAnimator.AnimatorUpdateListener animatorUpdateListener = new ValueAnimator.AnimatorUpdateListener() {
        @Override
        public void onAnimationUpdate(ValueAnimator valueAnimator) {
            float value = (float) valueAnimator.getAnimatedValue();
            // 该值表示默认的左padding，其值为(getWidth() - (mImageWidth + 2*mRadius)) / 2
            // 当textWidth等于mImageWidth + 2*mRadius时，该值为0
            float leftBase = (getWidth() - (mImageWidth + 2 * mRadius)) / 2;
            paddingLeft = (int) (leftBase + mRadius * (1 + (float) Math.sin(Math.toRadians(value))));
            paddingTop = (int) (mRadius * (1 - (float) Math.cos(Math.toRadians(value))));
            paddingRight = getWidth() - mImageWidth - paddingLeft;
            paddingBottom = getHeight() - mImageHeight - paddingTop;
            invalidate();

        }
    };

    Animator.AnimatorListener animatorListener = new Animator.AnimatorListener() {
        @Override
        public void onAnimationStart(Animator animation) {

        }

        @Override
        public void onAnimationEnd(Animator animation) {

        }

        @Override
        public void onAnimationCancel(Animator animation) {
            setupInitPaddings();
        }

        @Override
        public void onAnimationRepeat(Animator animation) {

        }
    };

    public void setupInitPaddings() {
        paddingLeft = (getWidth() - mImageWidth) / 2;
        paddingTop = (getHeight() - mImageHeight) / 2;
        paddingRight = paddingLeft;
        paddingBottom = paddingTop;
        requestLayout();
    }


    public void startFloat(int imageRes, int textRes) {
        animator.start();
        text = getResources().getString(textRes);
        setImageResource(imageRes);
    }

    public void stopFloat(int imageRes, int textRes) {

        animator.cancel();
        text = getResources().getString(textRes);

        setImageResource(imageRes);
    }


}
