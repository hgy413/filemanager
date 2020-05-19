package com.jiubang.commerce.utils;

import android.os.Environment;
import android.text.TextUtils;
import com.jb.ga0.commerce.util.LogUtils;
import com.jiubang.commerce.ad.AdSdkContants;

public class FileCacheUtils {
    /* JADX WARNING: Removed duplicated region for block: B:32:0x0076 A[SYNTHETIC, Splitter:B:32:0x0076] */
    /* JADX WARNING: Removed duplicated region for block: B:38:0x0085 A[SYNTHETIC, Splitter:B:38:0x0085] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public static boolean saveCacheDataToSdcard(java.lang.String r9, java.lang.String r10, boolean r11) {
        /*
            r6 = 0
            boolean r7 = isSDCardExist()
            if (r7 == 0) goto L_0x0013
            boolean r7 = android.text.TextUtils.isEmpty(r9)
            if (r7 != 0) goto L_0x0013
            boolean r7 = android.text.TextUtils.isEmpty(r10)
            if (r7 == 0) goto L_0x0014
        L_0x0013:
            return r6
        L_0x0014:
            if (r11 == 0) goto L_0x006e
            java.lang.String r2 = com.jiubang.commerce.utils.SimpleCryptoUtils.toHex((java.lang.String) r10)
        L_0x001a:
            java.io.File r5 = new java.io.File
            java.lang.String r7 = com.jiubang.commerce.ad.AdSdkContants.getADVERT_DATA_CACHE_FILE_PATH()
            r5.<init>(r7)
            boolean r7 = r5.exists()
            if (r7 != 0) goto L_0x002c
            r5.mkdirs()
        L_0x002c:
            java.io.File r1 = new java.io.File
            java.lang.StringBuilder r7 = new java.lang.StringBuilder
            r7.<init>()
            java.lang.String r8 = com.jiubang.commerce.ad.AdSdkContants.getADVERT_DATA_CACHE_FILE_PATH()
            java.lang.StringBuilder r7 = r7.append(r8)
            java.lang.StringBuilder r7 = r7.append(r9)
            java.lang.String r7 = r7.toString()
            r1.<init>(r7)
            r3 = 0
            boolean r7 = r1.exists()     // Catch:{ Exception -> 0x0070 }
            if (r7 != 0) goto L_0x0050
            r1.createNewFile()     // Catch:{ Exception -> 0x0070 }
        L_0x0050:
            java.io.FileOutputStream r4 = new java.io.FileOutputStream     // Catch:{ Exception -> 0x0070 }
            r7 = 0
            r4.<init>(r1, r7)     // Catch:{ Exception -> 0x0070 }
            java.lang.String r7 = "UTF-8"
            byte[] r7 = r2.getBytes(r7)     // Catch:{ Exception -> 0x0094, all -> 0x0091 }
            r4.write(r7)     // Catch:{ Exception -> 0x0094, all -> 0x0091 }
            r6 = 1
            if (r4 == 0) goto L_0x0013
            r4.flush()     // Catch:{ IOException -> 0x0069 }
            r4.close()     // Catch:{ IOException -> 0x0069 }
            goto L_0x0013
        L_0x0069:
            r0 = move-exception
            r0.printStackTrace()
            goto L_0x0013
        L_0x006e:
            r2 = r10
            goto L_0x001a
        L_0x0070:
            r0 = move-exception
        L_0x0071:
            r0.printStackTrace()     // Catch:{ all -> 0x0082 }
            if (r3 == 0) goto L_0x0013
            r3.flush()     // Catch:{ IOException -> 0x007d }
            r3.close()     // Catch:{ IOException -> 0x007d }
            goto L_0x0013
        L_0x007d:
            r0 = move-exception
            r0.printStackTrace()
            goto L_0x0013
        L_0x0082:
            r6 = move-exception
        L_0x0083:
            if (r3 == 0) goto L_0x008b
            r3.flush()     // Catch:{ IOException -> 0x008c }
            r3.close()     // Catch:{ IOException -> 0x008c }
        L_0x008b:
            throw r6
        L_0x008c:
            r0 = move-exception
            r0.printStackTrace()
            goto L_0x008b
        L_0x0091:
            r6 = move-exception
            r3 = r4
            goto L_0x0083
        L_0x0094:
            r0 = move-exception
            r3 = r4
            goto L_0x0071
        */
        throw new UnsupportedOperationException("Method not decompiled: com.jiubang.commerce.utils.FileCacheUtils.saveCacheDataToSdcard(java.lang.String, java.lang.String, boolean):boolean");
    }

    /* JADX WARNING: Removed duplicated region for block: B:23:0x005a A[SYNTHETIC, Splitter:B:23:0x005a] */
    /* JADX WARNING: Removed duplicated region for block: B:26:0x005f A[SYNTHETIC, Splitter:B:26:0x005f] */
    /* JADX WARNING: Removed duplicated region for block: B:51:0x0097 A[SYNTHETIC, Splitter:B:51:0x0097] */
    /* JADX WARNING: Removed duplicated region for block: B:54:0x009c A[SYNTHETIC, Splitter:B:54:0x009c] */
    /* JADX WARNING: Removed duplicated region for block: B:62:0x00af A[SYNTHETIC, Splitter:B:62:0x00af] */
    /* JADX WARNING: Removed duplicated region for block: B:65:0x00b4 A[SYNTHETIC, Splitter:B:65:0x00b4] */
    /* JADX WARNING: Unknown top exception splitter block from list: {B:48:0x0092=Splitter:B:48:0x0092, B:20:0x0055=Splitter:B:20:0x0055} */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public static java.lang.String readCacheDataToString(java.lang.String r14, boolean r15) {
        /*
            r11 = 0
            boolean r12 = isSDCardExist()
            if (r12 == 0) goto L_0x000d
            boolean r12 = android.text.TextUtils.isEmpty(r14)
            if (r12 == 0) goto L_0x000e
        L_0x000d:
            return r11
        L_0x000e:
            java.io.File r2 = new java.io.File
            java.lang.StringBuilder r12 = new java.lang.StringBuilder
            r12.<init>()
            java.lang.String r13 = com.jiubang.commerce.ad.AdSdkContants.getADVERT_DATA_CACHE_FILE_PATH()
            java.lang.StringBuilder r12 = r12.append(r13)
            java.lang.StringBuilder r12 = r12.append(r14)
            java.lang.String r12 = r12.toString()
            r2.<init>(r12)
            boolean r12 = r2.exists()
            if (r12 == 0) goto L_0x000d
            r3 = 0
            r5 = 0
            java.io.FileInputStream r4 = new java.io.FileInputStream     // Catch:{ Exception -> 0x00d0, OutOfMemoryError -> 0x0091 }
            r4.<init>(r2)     // Catch:{ Exception -> 0x00d0, OutOfMemoryError -> 0x0091 }
            java.io.StringWriter r10 = new java.io.StringWriter     // Catch:{ Exception -> 0x00d2, OutOfMemoryError -> 0x00c9, all -> 0x00c2 }
            r10.<init>()     // Catch:{ Exception -> 0x00d2, OutOfMemoryError -> 0x00c9, all -> 0x00c2 }
            java.io.InputStreamReader r6 = new java.io.InputStreamReader     // Catch:{ Exception -> 0x00d2, OutOfMemoryError -> 0x00c9, all -> 0x00c2 }
            java.lang.String r12 = "UTF-8"
            r6.<init>(r4, r12)     // Catch:{ Exception -> 0x00d2, OutOfMemoryError -> 0x00c9, all -> 0x00c2 }
            r12 = 4096(0x1000, float:5.74E-42)
            char[] r0 = new char[r12]     // Catch:{ Exception -> 0x0052, OutOfMemoryError -> 0x00cc, all -> 0x00c5 }
            r8 = 0
        L_0x0046:
            r12 = -1
            int r8 = r6.read(r0)     // Catch:{ Exception -> 0x0052, OutOfMemoryError -> 0x00cc, all -> 0x00c5 }
            if (r12 == r8) goto L_0x0068
            r12 = 0
            r10.write(r0, r12, r8)     // Catch:{ Exception -> 0x0052, OutOfMemoryError -> 0x00cc, all -> 0x00c5 }
            goto L_0x0046
        L_0x0052:
            r1 = move-exception
            r5 = r6
            r3 = r4
        L_0x0055:
            r1.printStackTrace()     // Catch:{ all -> 0x00ac }
            if (r5 == 0) goto L_0x005d
            r5.close()     // Catch:{ IOException -> 0x008c }
        L_0x005d:
            if (r3 == 0) goto L_0x000d
            r3.close()     // Catch:{ Exception -> 0x0063 }
            goto L_0x000d
        L_0x0063:
            r1 = move-exception
            r1.printStackTrace()
            goto L_0x000d
        L_0x0068:
            if (r15 == 0) goto L_0x0082
            java.lang.String r12 = r10.toString()     // Catch:{ Exception -> 0x0052, OutOfMemoryError -> 0x00cc, all -> 0x00c5 }
            java.lang.String r11 = com.jiubang.commerce.utils.SimpleCryptoUtils.fromHex(r12)     // Catch:{ Exception -> 0x0052, OutOfMemoryError -> 0x00cc, all -> 0x00c5 }
        L_0x0072:
            if (r6 == 0) goto L_0x0077
            r6.close()     // Catch:{ IOException -> 0x0087 }
        L_0x0077:
            if (r4 == 0) goto L_0x000d
            r4.close()     // Catch:{ Exception -> 0x007d }
            goto L_0x000d
        L_0x007d:
            r1 = move-exception
            r1.printStackTrace()
            goto L_0x000d
        L_0x0082:
            java.lang.String r11 = r10.toString()     // Catch:{ Exception -> 0x0052, OutOfMemoryError -> 0x00cc, all -> 0x00c5 }
            goto L_0x0072
        L_0x0087:
            r7 = move-exception
            r7.printStackTrace()
            goto L_0x0077
        L_0x008c:
            r7 = move-exception
            r7.printStackTrace()
            goto L_0x005d
        L_0x0091:
            r9 = move-exception
        L_0x0092:
            r9.printStackTrace()     // Catch:{ all -> 0x00ac }
            if (r5 == 0) goto L_0x009a
            r5.close()     // Catch:{ IOException -> 0x00a7 }
        L_0x009a:
            if (r3 == 0) goto L_0x000d
            r3.close()     // Catch:{ Exception -> 0x00a1 }
            goto L_0x000d
        L_0x00a1:
            r1 = move-exception
            r1.printStackTrace()
            goto L_0x000d
        L_0x00a7:
            r7 = move-exception
            r7.printStackTrace()
            goto L_0x009a
        L_0x00ac:
            r11 = move-exception
        L_0x00ad:
            if (r5 == 0) goto L_0x00b2
            r5.close()     // Catch:{ IOException -> 0x00b8 }
        L_0x00b2:
            if (r3 == 0) goto L_0x00b7
            r3.close()     // Catch:{ Exception -> 0x00bd }
        L_0x00b7:
            throw r11
        L_0x00b8:
            r7 = move-exception
            r7.printStackTrace()
            goto L_0x00b2
        L_0x00bd:
            r1 = move-exception
            r1.printStackTrace()
            goto L_0x00b7
        L_0x00c2:
            r11 = move-exception
            r3 = r4
            goto L_0x00ad
        L_0x00c5:
            r11 = move-exception
            r5 = r6
            r3 = r4
            goto L_0x00ad
        L_0x00c9:
            r9 = move-exception
            r3 = r4
            goto L_0x0092
        L_0x00cc:
            r9 = move-exception
            r5 = r6
            r3 = r4
            goto L_0x0092
        L_0x00d0:
            r1 = move-exception
            goto L_0x0055
        L_0x00d2:
            r1 = move-exception
            r3 = r4
            goto L_0x0055
        */
        throw new UnsupportedOperationException("Method not decompiled: com.jiubang.commerce.utils.FileCacheUtils.readCacheDataToString(java.lang.String, boolean):java.lang.String");
    }

    public static boolean deleteCacheFile(String fileName) {
        if (TextUtils.isEmpty(fileName)) {
            return false;
        }
        return FileUtils.deleteFile(AdSdkContants.getADVERT_DATA_CACHE_FILE_PATH() + fileName);
    }

    public static boolean isSDCardExist() {
        try {
            if (Environment.getExternalStorageState().equals("mounted")) {
                return true;
            }
            return false;
        } catch (Throwable thr) {
            LogUtils.e("Ad_SDK", "FileCacheUtils--isSDCardExist Exception!", thr);
            return false;
        }
    }
}
