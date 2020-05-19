package com.jiubang.commerce.ad.intelligent.api;

import android.content.Context;

public interface IIntelligentPreload {
    void configIntelligentPreload(Context context, boolean z);

    void enableLog();

    void init(Context context, String str, String str2, String str3, String str4, String str5);

    void setServer(boolean z);

    void startNativeAdPresolve(Context context, String str, String str2);

    void startServiceWithCommand(Context context, String str, String[] strArr);
}
