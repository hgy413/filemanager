package com.jiubang.commerce.ad.http.bean;

import android.content.Context;
import android.util.Log;
import com.jiubang.commerce.ad.url.AdRedirectUrlUtils;
import java.io.Serializable;

public class ParamsBean implements Serializable {
    private static final long serialVersionUID = 2767195236742574185L;
    private int mFinalGpJump;
    private int mUASwitcher;
    private int mUAType = 0;

    public int getUASwitcher() {
        return this.mUASwitcher;
    }

    public void setUASwitcher(int switcher) {
        this.mUASwitcher = switcher;
    }

    public int getUAType() {
        return this.mUAType;
    }

    public void setUAType(int mUAType2) {
        this.mUAType = mUAType2;
    }

    public boolean isFinalGpJump() {
        return 1 == this.mFinalGpJump;
    }

    public void setFinalGpJump(int mFinalGpJump2) {
        this.mFinalGpJump = mFinalGpJump2;
    }

    public String getUAStr(Context context) {
        switch (this.mUAType) {
            case 0:
                if (this.mUASwitcher == 1) {
                    return AdRedirectUrlUtils.getUserAgent(context);
                }
                return null;
            case 2:
                return AdRedirectUrlUtils.getUserAgent(context);
            case 3:
                return getHalfUA(context);
            case 4:
                return getFakeUA(context);
            default:
                return null;
        }
    }

    public static String getHalfUA(Context context) {
        String fullUA = AdRedirectUrlUtils.getUserAgent(context);
        Log.d("UA", "getHalfUA--fullUA=" + fullUA);
        String afterUA = fullUA.replaceFirst(";[^;]*Build/[^\\)]*\\)", ")");
        Log.d("UA", "getHalfUA=" + afterUA);
        return afterUA;
    }

    public static String getFakeUA(Context context) {
        String fullUA = AdRedirectUrlUtils.getUserAgent(context);
        Log.d("UA", "getFakeUA--fullUA=" + fullUA);
        String afterUA = fullUA.replaceFirst("\\s*\\((\\s|\\S)*Build/[^\\)]*\\)", "");
        Log.d("UA", "getFakeUA=" + afterUA);
        return afterUA;
    }
}
