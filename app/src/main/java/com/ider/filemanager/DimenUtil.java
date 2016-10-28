package com.ider.filemanager;

        import android.content.res.Resources;
        import android.util.TypedValue;

/**
 * Created by ider-eric on 2016/10/28.
 */

public class DimenUtil {
    public static int dp2px(Resources res, int dpValue) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dpValue, res.getDisplayMetrics());
    }

    public static int sp2px(Resources res, int spValue) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, spValue, res.getDisplayMetrics());
    }
}
