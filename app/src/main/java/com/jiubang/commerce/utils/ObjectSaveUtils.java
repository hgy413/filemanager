package com.jiubang.commerce.utils;

public class ObjectSaveUtils {
    /* JADX WARNING: Removed duplicated region for block: B:20:0x002c A[SYNTHETIC, Splitter:B:20:0x002c] */
    /* JADX WARNING: Removed duplicated region for block: B:23:0x0031 A[SYNTHETIC, Splitter:B:23:0x0031] */
    /* JADX WARNING: Removed duplicated region for block: B:31:0x0042 A[SYNTHETIC, Splitter:B:31:0x0042] */
    /* JADX WARNING: Removed duplicated region for block: B:34:0x0047 A[SYNTHETIC, Splitter:B:34:0x0047] */
    /* JADX WARNING: Removed duplicated region for block: B:48:? A[RETURN, SYNTHETIC] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public static void saveObject(android.content.Context r5, java.lang.String r6, java.lang.Object r7) {
        /*
            r1 = 0
            r2 = 0
            r4 = 0
            java.io.FileOutputStream r1 = r5.openFileOutput(r6, r4)     // Catch:{ Exception -> 0x0026 }
            java.io.ObjectOutputStream r3 = new java.io.ObjectOutputStream     // Catch:{ Exception -> 0x0026 }
            r3.<init>(r1)     // Catch:{ Exception -> 0x0026 }
            r3.writeObject(r7)     // Catch:{ Exception -> 0x0058, all -> 0x0055 }
            if (r1 == 0) goto L_0x0014
            r1.close()     // Catch:{ IOException -> 0x001b }
        L_0x0014:
            if (r3 == 0) goto L_0x005b
            r3.close()     // Catch:{ IOException -> 0x0020 }
            r2 = r3
        L_0x001a:
            return
        L_0x001b:
            r0 = move-exception
            r0.printStackTrace()
            goto L_0x0014
        L_0x0020:
            r0 = move-exception
            r0.printStackTrace()
            r2 = r3
            goto L_0x001a
        L_0x0026:
            r0 = move-exception
        L_0x0027:
            r0.printStackTrace()     // Catch:{ all -> 0x003f }
            if (r1 == 0) goto L_0x002f
            r1.close()     // Catch:{ IOException -> 0x003a }
        L_0x002f:
            if (r2 == 0) goto L_0x001a
            r2.close()     // Catch:{ IOException -> 0x0035 }
            goto L_0x001a
        L_0x0035:
            r0 = move-exception
            r0.printStackTrace()
            goto L_0x001a
        L_0x003a:
            r0 = move-exception
            r0.printStackTrace()
            goto L_0x002f
        L_0x003f:
            r4 = move-exception
        L_0x0040:
            if (r1 == 0) goto L_0x0045
            r1.close()     // Catch:{ IOException -> 0x004b }
        L_0x0045:
            if (r2 == 0) goto L_0x004a
            r2.close()     // Catch:{ IOException -> 0x0050 }
        L_0x004a:
            throw r4
        L_0x004b:
            r0 = move-exception
            r0.printStackTrace()
            goto L_0x0045
        L_0x0050:
            r0 = move-exception
            r0.printStackTrace()
            goto L_0x004a
        L_0x0055:
            r4 = move-exception
            r2 = r3
            goto L_0x0040
        L_0x0058:
            r0 = move-exception
            r2 = r3
            goto L_0x0027
        L_0x005b:
            r2 = r3
            goto L_0x001a
        */
        throw new UnsupportedOperationException("Method not decompiled: com.jiubang.commerce.utils.ObjectSaveUtils.saveObject(android.content.Context, java.lang.String, java.lang.Object):void");
    }

    /* JADX WARNING: Removed duplicated region for block: B:32:0x0042 A[SYNTHETIC, Splitter:B:32:0x0042] */
    /* JADX WARNING: Removed duplicated region for block: B:35:0x0047 A[SYNTHETIC, Splitter:B:35:0x0047] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public static java.lang.Object getObject(android.content.Context r5, java.lang.String r6) {
        /*
            r1 = 0
            r2 = 0
            java.io.FileInputStream r1 = r5.openFileInput(r6)     // Catch:{ Exception -> 0x0025 }
            java.io.ObjectInputStream r3 = new java.io.ObjectInputStream     // Catch:{ Exception -> 0x0025 }
            r3.<init>(r1)     // Catch:{ Exception -> 0x0025 }
            java.lang.Object r4 = r3.readObject()     // Catch:{ Exception -> 0x0058, all -> 0x0055 }
            if (r1 == 0) goto L_0x0014
            r1.close()     // Catch:{ IOException -> 0x001b }
        L_0x0014:
            if (r3 == 0) goto L_0x0019
            r3.close()     // Catch:{ IOException -> 0x0020 }
        L_0x0019:
            r2 = r3
        L_0x001a:
            return r4
        L_0x001b:
            r0 = move-exception
            r0.printStackTrace()
            goto L_0x0014
        L_0x0020:
            r0 = move-exception
            r0.printStackTrace()
            goto L_0x0019
        L_0x0025:
            r0 = move-exception
        L_0x0026:
            r0.printStackTrace()     // Catch:{ all -> 0x003f }
            if (r1 == 0) goto L_0x002e
            r1.close()     // Catch:{ IOException -> 0x0035 }
        L_0x002e:
            if (r2 == 0) goto L_0x0033
            r2.close()     // Catch:{ IOException -> 0x003a }
        L_0x0033:
            r4 = 0
            goto L_0x001a
        L_0x0035:
            r0 = move-exception
            r0.printStackTrace()
            goto L_0x002e
        L_0x003a:
            r0 = move-exception
            r0.printStackTrace()
            goto L_0x0033
        L_0x003f:
            r4 = move-exception
        L_0x0040:
            if (r1 == 0) goto L_0x0045
            r1.close()     // Catch:{ IOException -> 0x004b }
        L_0x0045:
            if (r2 == 0) goto L_0x004a
            r2.close()     // Catch:{ IOException -> 0x0050 }
        L_0x004a:
            throw r4
        L_0x004b:
            r0 = move-exception
            r0.printStackTrace()
            goto L_0x0045
        L_0x0050:
            r0 = move-exception
            r0.printStackTrace()
            goto L_0x004a
        L_0x0055:
            r4 = move-exception
            r2 = r3
            goto L_0x0040
        L_0x0058:
            r0 = move-exception
            r2 = r3
            goto L_0x0026
        */
        throw new UnsupportedOperationException("Method not decompiled: com.jiubang.commerce.utils.ObjectSaveUtils.getObject(android.content.Context, java.lang.String):java.lang.Object");
    }
}
