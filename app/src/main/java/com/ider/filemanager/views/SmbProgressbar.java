package com.ider.filemanager.views;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.ProgressBar;

import com.ider.filemanager.DimenUtil;
import com.ider.filemanager.R;

/**
 * Created by ider-eric on 2016/10/28.
 */

public class SmbProgressbar extends ProgressBar {

    private static final int DEFAULT_REACH_HEIGHT = 5; // dp
    private static final int DEFAULT_UNREACH_HEIGHT = 2; // dp
    private static final int DEFAULT_REACH_COLOR = 0xff31ab6b;
    private static final int DEFAULT_UNREACH_COLOR = 0xff31ab6b;
    private static final int DEFAULT_TEXT_COLOR = 0xff31ab6b;
    private static final int DEFAULT_TEXT_SIZE = 15; //sp


    private int mRealWidth;
    private int mRealHeight;
    private int mReachHeight = DimenUtil.dp2px(getResources(), DEFAULT_REACH_HEIGHT);
    private int mUnReachHeight = DimenUtil.dp2px(getResources(), DEFAULT_UNREACH_HEIGHT);
    private int mReachColor = DEFAULT_REACH_COLOR;
    private int mUnReachColor = DEFAULT_UNREACH_COLOR;
    private int mTextColor = DEFAULT_TEXT_COLOR;
    private int mTextSize = DimenUtil.sp2px(getResources(), DEFAULT_TEXT_SIZE);
    private int mTextOffset;

    private Paint mPaint;

    public SmbProgressbar(Context context) {
        this(context, null);
    }

    public SmbProgressbar(Context context, AttributeSet attrs) {
        this(context, attrs, 0);

    }

    public SmbProgressbar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.SmbProgressbar);
        if(array != null) {
            mReachHeight = (int) array.getDimension(R.styleable.SmbProgressbar_reach_height, mReachHeight);
            mUnReachHeight = (int) array.getDimension(R.styleable.SmbProgressbar_unreach_height, mUnReachHeight);
            mReachColor = array.getColor(R.styleable.SmbProgressbar_reach_color, mReachColor);
            mUnReachColor = array.getColor(R.styleable.SmbProgressbar_unreach_color, mUnReachColor);
            mTextColor = array.getColor(R.styleable.SmbProgressbar_text_color, mTextColor);
            mTextSize = (int) array.getDimension(R.styleable.SmbProgressbar_text_size, mTextSize);
            array.recycle();
        }

        setupPaint();
    }

    private void setupPaint() {
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setTextSize(mTextSize);
    }

    @Override
    protected synchronized void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.save();

        canvas.translate(getPaddingLeft(), mRealHeight/2);

        float percent = getProgress() * 1.0f / (float)getMax();
        String text = (int) (percent * 100) + "%";
        int textWidth = (int) mPaint.measureText(text);
        int textHeight = (int) (mPaint.descent() + mPaint.ascent());
        mTextOffset = textWidth + 40;
        // draw reach line
        mPaint.setStrokeWidth(mReachHeight);
        mPaint.setColor(mReachColor);
        int endX = (int) (mRealWidth * percent - mTextOffset / 2);
        endX = Math.max(0, endX);
        canvas.drawLine(0, 0, endX, 0, mPaint);

        // draw text
        mPaint.setTextSize(mTextSize);
        mPaint.setColor(mTextColor);
        int textX = endX + (mTextOffset - textWidth) / 2;
        int textY = -textHeight / 2;
        canvas.drawText(text, textX, textY, mPaint);

        // draw unReach line
        if(endX + mTextOffset < mRealWidth) {
            mPaint.setColor(mUnReachColor);
            mPaint.setStrokeWidth(mUnReachHeight);
            int unReachStartX = endX + mTextOffset;
            canvas.drawLine(unReachStartX, 0, mRealWidth, 0, mPaint);
        }


        canvas.restore();
    }



    @Override
    protected synchronized void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int width = MeasureSpec.getSize(widthMeasureSpec);
        int height = measureHeight(heightMeasureSpec);
        setMeasuredDimension(width, height);
        mRealWidth = getMeasuredWidth() - getPaddingLeft() - getPaddingRight();
        mRealHeight = getMeasuredHeight() - getPaddingTop() - getPaddingBottom();

    }

    public int measureHeight(int heightMeasureSpec) {
        int result = 0;
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int height = MeasureSpec.getSize(heightMeasureSpec);
        if(heightMode == MeasureSpec.EXACTLY) {
            result = height;
        } else {
            int textHeight = (int) (mPaint.descent() + mPaint.ascent());
            result = Math.max(Math.max(mReachHeight, mUnReachHeight), textHeight) + getPaddingTop() + getPaddingBottom();
            if(heightMode == MeasureSpec.AT_MOST) {
                result = Math.min(result, height);
            }
        }
        return result;
    }
}
