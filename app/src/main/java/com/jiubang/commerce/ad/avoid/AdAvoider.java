package com.jiubang.commerce.ad.avoid;

import android.content.Context;

public class AdAvoider implements IAvoidDetector {
    private static AdAvoider sInstance;
    private IAvoidDetector mAvoidDetector;
    private Context mContext;

    private AdAvoider(Context context) {
        this.mContext = context.getApplicationContext();
        this.mAvoidDetector = new CountryDetector(context);
    }

    public static AdAvoider getInstance(Context context) {
        if (sInstance == null) {
            synchronized (AdAvoider.class) {
                if (sInstance == null) {
                    sInstance = new AdAvoider(context);
                }
            }
        }
        return sInstance;
    }

    public void detect(Object... params) {
        this.mAvoidDetector.detect(params);
    }

    public boolean shouldAvoid() {
        return this.mAvoidDetector.shouldAvoid();
    }

    public boolean isNoad() {
        return this.mAvoidDetector.isNoad();
    }
}
