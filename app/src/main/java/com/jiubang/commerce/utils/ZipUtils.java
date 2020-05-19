package com.jiubang.commerce.utils;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

public class ZipUtils {
    /* JADX WARNING: Removed duplicated region for block: B:17:0x0020  */
    /* JADX WARNING: Removed duplicated region for block: B:19:0x0025  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public static byte[] ungzip(byte[] r6) throws java.lang.Exception {
        /*
            r3 = 0
            r0 = 0
            java.io.ByteArrayInputStream r1 = new java.io.ByteArrayInputStream     // Catch:{ Exception -> 0x001b }
            r1.<init>(r6)     // Catch:{ Exception -> 0x001b }
            java.util.zip.GZIPInputStream r4 = new java.util.zip.GZIPInputStream     // Catch:{ Exception -> 0x0030, all -> 0x0029 }
            r4.<init>(r1)     // Catch:{ Exception -> 0x0030, all -> 0x0029 }
            byte[] r5 = toByteArray(r4)     // Catch:{ Exception -> 0x0033, all -> 0x002c }
            if (r1 == 0) goto L_0x0015
            r1.close()
        L_0x0015:
            if (r4 == 0) goto L_0x001a
            r4.close()
        L_0x001a:
            return r5
        L_0x001b:
            r2 = move-exception
        L_0x001c:
            throw r2     // Catch:{ all -> 0x001d }
        L_0x001d:
            r5 = move-exception
        L_0x001e:
            if (r0 == 0) goto L_0x0023
            r0.close()
        L_0x0023:
            if (r3 == 0) goto L_0x0028
            r3.close()
        L_0x0028:
            throw r5
        L_0x0029:
            r5 = move-exception
            r0 = r1
            goto L_0x001e
        L_0x002c:
            r5 = move-exception
            r0 = r1
            r3 = r4
            goto L_0x001e
        L_0x0030:
            r2 = move-exception
            r0 = r1
            goto L_0x001c
        L_0x0033:
            r2 = move-exception
            r0 = r1
            r3 = r4
            goto L_0x001c
        */
        throw new UnsupportedOperationException("Method not decompiled: com.jiubang.commerce.utils.ZipUtils.ungzip(byte[]):byte[]");
    }

    public static String unzip(InputStream inStream) {
        try {
            return new String(ungzip(toByteArray(inStream)), "utf-8");
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }

    public static byte[] toByteArray(InputStream input) throws Exception {
        ByteArrayOutputStream output = null;
        try {
            ByteArrayOutputStream output2 = new ByteArrayOutputStream();
            try {
                copy(input, output2);
                byte[] byteArray = output2.toByteArray();
                if (output2 != null) {
                    try {
                        output2.close();
                    } catch (Exception e) {
                        throw e;
                    }
                }
                return byteArray;
            } catch (Exception e2) {
                e = e2;
                output = output2;
            } catch (Throwable th) {
                th = th;
                output = output2;
                if (output != null) {
                    try {
                        output.close();
                    } catch (Exception e3) {
                        throw e3;
                    }
                }
                throw th;
            }
        } catch (Exception e4) {
            e = e4;
            try {
                throw e;
            } catch (Throwable th2) {
                th = th2;
            }
        }
    }

    public static int copy(InputStream input, OutputStream output) throws Exception {
        try {
            byte[] buffer = new byte[4096];
            int count = 0;
            while (true) {
                int n = input.read(buffer);
                if (-1 == n) {
                    return count;
                }
                output.write(buffer, 0, n);
                count += n;
            }
        } catch (Exception e) {
            throw e;
        }
    }
}
