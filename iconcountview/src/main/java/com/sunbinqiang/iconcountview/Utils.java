package com.sunbinqiang.iconcountview;

import android.content.Context;

/**
 * Created by sunbinqiang on 16/10/2017.
 */

public class Utils {
    public static int dip2px(Context context, float dipValue) {
        if (context == null) {
            return (int) dipValue;
        }
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dipValue * scale + 0.5f);
    }
}
