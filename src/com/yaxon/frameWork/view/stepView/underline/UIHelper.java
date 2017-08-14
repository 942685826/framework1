package com.yaxon.frameWork.view.stepView.underline;

import android.content.Context;

/**
 * Created by gjp on 2017/7/15.
 */

public class UIHelper {
    private UIHelper() throws InstantiationException {
        throw new InstantiationException("This class is not for instantiation");
    }

    /**
     * dip转px
     */
    public static int dipToPx(Context context, float dip) {
        return (int) (dip * context.getResources().getDisplayMetrics().density + 0.5f);
    }

    /**
     * px转dip
     */
    public static int pxToDip(Context context, float pxValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }
}
