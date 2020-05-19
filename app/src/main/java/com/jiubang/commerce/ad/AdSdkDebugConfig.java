package com.jiubang.commerce.ad;

import android.text.TextUtils;
import com.jb.ga0.commerce.util.LogUtils;

public class AdSdkDebugConfig {
    private static AdSdkDebugConfig sInstance = null;

    private AdSdkDebugConfig() {
        init();
    }

    public static AdSdkDebugConfig getInstance() {
        if (sInstance == null) {
            sInstance = new AdSdkDebugConfig();
        }
        return sInstance;
    }

    public int[] getSupportAdObjectTypeArray(int virtualModuleId) {
        return toIntArray(getValue(virtualModuleId + "_supportAds", (String) null));
    }

    /* JADX WARNING: Removed duplicated region for block: B:16:0x003d A[SYNTHETIC, Splitter:B:16:0x003d] */
    /* JADX WARNING: Removed duplicated region for block: B:22:0x0049 A[SYNTHETIC, Splitter:B:22:0x0049] */
    /* JADX WARNING: Removed duplicated region for block: B:35:? A[RETURN, SYNTHETIC] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private void init() {
        /*
            r6 = this;
            java.lang.String r4 = com.jiubang.commerce.ad.AdSdkContants.getDEBUG_CONFIG_FILEPATH()
            boolean r4 = com.jiubang.commerce.utils.FileUtils.isFileExist(r4)
            if (r4 != 0) goto L_0x0031
            java.lang.String r4 = com.jiubang.commerce.ad.AdSdkContants.getDEBUG_CONFIG_FILEPATH()
            r5 = 1
            com.jiubang.commerce.utils.FileUtils.createNewFile(r4, r5)
            r1 = 0
            java.io.FileOutputStream r2 = new java.io.FileOutputStream     // Catch:{ IOException -> 0x0037 }
            java.lang.String r4 = com.jiubang.commerce.ad.AdSdkContants.getDEBUG_CONFIG_FILEPATH()     // Catch:{ IOException -> 0x0037 }
            r2.<init>(r4)     // Catch:{ IOException -> 0x0037 }
            java.util.Properties r3 = new java.util.Properties     // Catch:{ IOException -> 0x0055, all -> 0x0052 }
            r3.<init>()     // Catch:{ IOException -> 0x0055, all -> 0x0052 }
            java.lang.String r4 = "virtualmoduleId_supportAds"
            java.lang.String r5 = "0,1,2,3,4,5,6,7,8,9"
            r3.setProperty(r4, r5)     // Catch:{ IOException -> 0x0055, all -> 0x0052 }
            r4 = 0
            r3.store(r2, r4)     // Catch:{ IOException -> 0x0055, all -> 0x0052 }
            if (r2 == 0) goto L_0x0031
            r2.close()     // Catch:{ IOException -> 0x0032 }
        L_0x0031:
            return
        L_0x0032:
            r0 = move-exception
            r0.printStackTrace()
            goto L_0x0031
        L_0x0037:
            r0 = move-exception
        L_0x0038:
            r0.printStackTrace()     // Catch:{ all -> 0x0046 }
            if (r1 == 0) goto L_0x0031
            r1.close()     // Catch:{ IOException -> 0x0041 }
            goto L_0x0031
        L_0x0041:
            r0 = move-exception
            r0.printStackTrace()
            goto L_0x0031
        L_0x0046:
            r4 = move-exception
        L_0x0047:
            if (r1 == 0) goto L_0x004c
            r1.close()     // Catch:{ IOException -> 0x004d }
        L_0x004c:
            throw r4
        L_0x004d:
            r0 = move-exception
            r0.printStackTrace()
            goto L_0x004c
        L_0x0052:
            r4 = move-exception
            r1 = r2
            goto L_0x0047
        L_0x0055:
            r0 = move-exception
            r1 = r2
            goto L_0x0038
        */
        throw new UnsupportedOperationException("Method not decompiled: com.jiubang.commerce.ad.AdSdkDebugConfig.init():void");
    }

    /* JADX WARNING: Removed duplicated region for block: B:22:0x0034 A[SYNTHETIC, Splitter:B:22:0x0034] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private java.lang.String getValue(java.lang.String r6, java.lang.String r7) {
        /*
            r5 = this;
            java.util.Properties r3 = new java.util.Properties
            r3.<init>()
            r1 = 0
            java.io.FileInputStream r2 = new java.io.FileInputStream     // Catch:{ IOException -> 0x0022 }
            java.lang.String r4 = com.jiubang.commerce.ad.AdSdkContants.getDEBUG_CONFIG_FILEPATH()     // Catch:{ IOException -> 0x0022 }
            r2.<init>(r4)     // Catch:{ IOException -> 0x0022 }
            r3.load(r2)     // Catch:{ IOException -> 0x0040, all -> 0x003d }
            java.lang.String r7 = r3.getProperty(r6, r7)     // Catch:{ IOException -> 0x0040, all -> 0x003d }
            if (r2 == 0) goto L_0x001b
            r2.close()     // Catch:{ IOException -> 0x001d }
        L_0x001b:
            r1 = r2
        L_0x001c:
            return r7
        L_0x001d:
            r0 = move-exception
            r0.printStackTrace()
            goto L_0x001b
        L_0x0022:
            r0 = move-exception
        L_0x0023:
            r0.printStackTrace()     // Catch:{ all -> 0x0031 }
            if (r1 == 0) goto L_0x001c
            r1.close()     // Catch:{ IOException -> 0x002c }
            goto L_0x001c
        L_0x002c:
            r0 = move-exception
            r0.printStackTrace()
            goto L_0x001c
        L_0x0031:
            r4 = move-exception
        L_0x0032:
            if (r1 == 0) goto L_0x0037
            r1.close()     // Catch:{ IOException -> 0x0038 }
        L_0x0037:
            throw r4
        L_0x0038:
            r0 = move-exception
            r0.printStackTrace()
            goto L_0x0037
        L_0x003d:
            r4 = move-exception
            r1 = r2
            goto L_0x0032
        L_0x0040:
            r0 = move-exception
            r1 = r2
            goto L_0x0023
        */
        throw new UnsupportedOperationException("Method not decompiled: com.jiubang.commerce.ad.AdSdkDebugConfig.getValue(java.lang.String, java.lang.String):java.lang.String");
    }

    public static int[] toIntArray(String value) {
        if (TextUtils.isEmpty(value)) {
            return null;
        }
        String[] items = value.split(",");
        if (items == null || items.length == 0) {
            return null;
        }
        try {
            int[] result = new int[items.length];
            for (int i = 0; i < items.length; i++) {
                result[i] = Integer.parseInt(items[i]);
            }
            return result;
        } catch (NumberFormatException e) {
            e.printStackTrace();
            if (LogUtils.isShowLog()) {
                LogUtils.w("Ad_SDK", "AdSdkDebugConfig::toIntArray<>NumberFormatException error, by value:" + value);
            }
            return null;
        }
    }
}
