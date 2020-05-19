package com.jiubang.commerce.ad.url;

import android.content.Context;
import android.os.Build;
import android.text.TextUtils;
import com.jb.ga0.commerce.util.LogUtils;
import com.jiubang.commerce.ad.http.bean.ParamsBean;
import com.jiubang.commerce.thread.AdSdkThreadExecutorProxy;
import com.jiubang.commerce.utils.GoogleMarketUtils;
import com.jiubang.commerce.utils.SystemUtils;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpParams;

public class AdRedirectUrlUtils {
    /* access modifiers changed from: private */
    public static String sUserAgent;

    /* JADX WARNING: Code restructure failed: missing block: B:16:?, code lost:
        r5 = new com.jiubang.commerce.ad.url.ParseAdUrlResponseBean(r6, 1, (java.lang.String) null, "url is null", java.lang.System.currentTimeMillis() - r20);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:46:0x01f1, code lost:
        r13 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:48:0x01fa, code lost:
        if (isInValidUrlErrorMsg(r13.getMessage()) != false) goto L_0x01fc;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:49:0x01fc, code lost:
        r5 = new com.jiubang.commerce.ad.url.ParseAdUrlResponseBean(r6, 3, r28, "parse success: invalid url", java.lang.System.currentTimeMillis() - r20);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:50:0x020c, code lost:
        r13.printStackTrace();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:54:0x023f, code lost:
        r13 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:55:0x0240, code lost:
        r5 = new com.jiubang.commerce.ad.url.ParseAdUrlResponseBean(r6, 2, (java.lang.String) null, "ConnectTimeoutException:" + r13.getMessage(), java.lang.System.currentTimeMillis() - r20);
        r13.printStackTrace();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:58:?, code lost:
        r5 = new com.jiubang.commerce.ad.url.ParseAdUrlResponseBean(r6, 1, (java.lang.String) null, "headers is null", java.lang.System.currentTimeMillis() - r20);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:60:0x0280, code lost:
        if (r6 != 200) goto L_0x02dc;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:62:?, code lost:
        r14 = r18.getEntity();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:63:0x0286, code lost:
        if (r14 == null) goto L_0x02d9;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:64:0x0288, code lost:
        r19 = com.jiubang.commerce.utils.FileUtils.readInputStreamWithLength(r14.getContent(), "UTF-8", 50);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:66:0x0298, code lost:
        if (android.text.TextUtils.isEmpty(r19) != false) goto L_0x02c8;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:68:0x02a2, code lost:
        if (r19.equals("repeat click") == false) goto L_0x02c8;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:69:0x02a4, code lost:
        com.jb.ga0.commerce.util.LogUtils.d("Ad_SDK", "getHttpRedirectUrlFromLocation(repeat click, " + r12 + ")");
     */
    /* JADX WARNING: Code restructure failed: missing block: B:70:0x02c4, code lost:
        r13 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:72:?, code lost:
        r13.printStackTrace();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:74:0x02d9, code lost:
        r19 = "";
     */
    /* JADX WARNING: Code restructure failed: missing block: B:76:?, code lost:
        r5 = new com.jiubang.commerce.ad.url.ParseAdUrlResponseBean(r6, 1, (java.lang.String) null, "network statusCode is not 200, 301, 302, 303::->[" + r6 + "]", java.lang.System.currentTimeMillis() - r20);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:77:0x0304, code lost:
        r5 = new com.jiubang.commerce.ad.url.ParseAdUrlResponseBean(r6, 2, (java.lang.String) null, "IllegalArgumentException:" + r13.getMessage(), java.lang.System.currentTimeMillis() - r20);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:78:0x032a, code lost:
        r13 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:79:0x032b, code lost:
        r5 = new com.jiubang.commerce.ad.url.ParseAdUrlResponseBean(r6, 2, (java.lang.String) null, "SocketTimeoutException:" + r13.getMessage(), java.lang.System.currentTimeMillis() - r20);
        r13.printStackTrace();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:80:0x0354, code lost:
        r13 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:81:0x0355, code lost:
        r5 = new com.jiubang.commerce.ad.url.ParseAdUrlResponseBean(r6, 2, (java.lang.String) null, "ClientProtocolException:" + r13.getMessage(), java.lang.System.currentTimeMillis() - r20);
        r13.printStackTrace();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:82:0x037e, code lost:
        r13 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:83:0x037f, code lost:
        r5 = new com.jiubang.commerce.ad.url.ParseAdUrlResponseBean(r6, 2, (java.lang.String) null, "IOException:" + r13.getMessage(), java.lang.System.currentTimeMillis() - r20);
        r13.printStackTrace();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:93:?, code lost:
        return r12;
     */
    /* JADX WARNING: Exception block dominator not found, dom blocks: [B:15:0x00b5, B:61:0x0282] */
    /* JADX WARNING: Removed duplicated region for block: B:46:0x01f1 A[EDGE_INSN: B:58:?->B:46:0x01f1 ?: BREAK  , ExcHandler: IllegalArgumentException (r13v5 'e' java.lang.IllegalArgumentException A[CUSTOM_DECLARE]), PHI: r6 
  PHI: (r6v7 'finalStatusCode' int) = (r6v1 'finalStatusCode' int), (r6v1 'finalStatusCode' int), (r6v8 'finalStatusCode' int), (r6v8 'finalStatusCode' int), (r6v8 'finalStatusCode' int), (r6v8 'finalStatusCode' int), (r6v8 'finalStatusCode' int), (r6v8 'finalStatusCode' int), (r6v1 'finalStatusCode' int) binds: [B:52:0x0213, B:53:?, B:57:0x026d, B:58:?, B:75:0x02dc, B:76:?, B:61:0x0282, B:71:0x02c5, B:15:0x00b5] A[DONT_GENERATE, DONT_INLINE], Splitter:B:15:0x00b5] */
    /* JADX WARNING: Removed duplicated region for block: B:4:0x001e  */
    /* JADX WARNING: Removed duplicated region for block: B:54:0x023f A[ExcHandler: ConnectTimeoutException (r13v4 'e' org.apache.http.conn.ConnectTimeoutException A[CUSTOM_DECLARE]), PHI: r6 
  PHI: (r6v6 'finalStatusCode' int) = (r6v1 'finalStatusCode' int), (r6v1 'finalStatusCode' int), (r6v8 'finalStatusCode' int), (r6v8 'finalStatusCode' int), (r6v8 'finalStatusCode' int), (r6v8 'finalStatusCode' int), (r6v8 'finalStatusCode' int), (r6v8 'finalStatusCode' int), (r6v1 'finalStatusCode' int) binds: [B:52:0x0213, B:53:?, B:57:0x026d, B:58:?, B:75:0x02dc, B:76:?, B:61:0x0282, B:71:0x02c5, B:15:0x00b5] A[DONT_GENERATE, DONT_INLINE], Splitter:B:15:0x00b5] */
    /* JADX WARNING: Removed duplicated region for block: B:78:0x032a A[ExcHandler: SocketTimeoutException (r13v3 'e' java.net.SocketTimeoutException A[CUSTOM_DECLARE]), PHI: r6 
  PHI: (r6v5 'finalStatusCode' int) = (r6v1 'finalStatusCode' int), (r6v1 'finalStatusCode' int), (r6v8 'finalStatusCode' int), (r6v8 'finalStatusCode' int), (r6v8 'finalStatusCode' int), (r6v8 'finalStatusCode' int), (r6v8 'finalStatusCode' int), (r6v8 'finalStatusCode' int), (r6v1 'finalStatusCode' int) binds: [B:52:0x0213, B:53:?, B:57:0x026d, B:58:?, B:75:0x02dc, B:76:?, B:61:0x0282, B:71:0x02c5, B:15:0x00b5] A[DONT_GENERATE, DONT_INLINE], Splitter:B:15:0x00b5] */
    /* JADX WARNING: Removed duplicated region for block: B:80:0x0354 A[ExcHandler: ClientProtocolException (r13v2 'e' org.apache.http.client.ClientProtocolException A[CUSTOM_DECLARE]), PHI: r6 
  PHI: (r6v4 'finalStatusCode' int) = (r6v1 'finalStatusCode' int), (r6v1 'finalStatusCode' int), (r6v8 'finalStatusCode' int), (r6v8 'finalStatusCode' int), (r6v8 'finalStatusCode' int), (r6v8 'finalStatusCode' int), (r6v8 'finalStatusCode' int), (r6v8 'finalStatusCode' int), (r6v1 'finalStatusCode' int) binds: [B:52:0x0213, B:53:?, B:57:0x026d, B:58:?, B:75:0x02dc, B:76:?, B:61:0x0282, B:71:0x02c5, B:15:0x00b5] A[DONT_GENERATE, DONT_INLINE], Splitter:B:15:0x00b5] */
    /* JADX WARNING: Removed duplicated region for block: B:82:0x037e A[ExcHandler: IOException (r13v1 'e' java.io.IOException A[CUSTOM_DECLARE]), PHI: r6 
  PHI: (r6v3 'finalStatusCode' int) = (r6v1 'finalStatusCode' int), (r6v1 'finalStatusCode' int), (r6v8 'finalStatusCode' int), (r6v8 'finalStatusCode' int), (r6v8 'finalStatusCode' int), (r6v8 'finalStatusCode' int), (r6v8 'finalStatusCode' int), (r6v8 'finalStatusCode' int), (r6v1 'finalStatusCode' int) binds: [B:52:0x0213, B:53:?, B:57:0x026d, B:58:?, B:75:0x02dc, B:76:?, B:61:0x0282, B:71:0x02c5, B:15:0x00b5] A[DONT_GENERATE, DONT_INLINE], Splitter:B:15:0x00b5] */
    /* JADX WARNING: Removed duplicated region for block: B:86:0x03bf A[RETURN, SYNTHETIC] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public static java.lang.String getHttpRedirectUrlFromLocation(android.content.Context r23, com.jiubang.commerce.ad.http.bean.ParamsBean r24, java.lang.String r25, java.lang.String r26, java.lang.String r27, java.lang.String r28) {
        /*
            r5 = 0
            r6 = 0
            long r20 = java.lang.System.currentTimeMillis()
            boolean r7 = com.jiubang.commerce.utils.NetworkUtils.isNetworkOK(r23)
            if (r7 != 0) goto L_0x002e
            com.jiubang.commerce.ad.url.ParseAdUrlResponseBean r5 = new com.jiubang.commerce.ad.url.ParseAdUrlResponseBean
            r7 = 2
            java.lang.String r9 = "network is not ok"
            long r10 = java.lang.System.currentTimeMillis()
            long r10 = r10 - r20
            r8 = r28
            r5.<init>(r6, r7, r8, r9, r10)
        L_0x001c:
            if (r5 == 0) goto L_0x03bf
            r0 = r23
            r1 = r25
            r2 = r26
            r3 = r27
            r5.uploadParseUrlStatusStatistic(r0, r1, r2, r3)
            java.lang.String r12 = r5.getParsedAdUrl()
        L_0x002d:
            return r12
        L_0x002e:
            r0 = r23
            r1 = r24
            r2 = r28
            boolean r7 = judgeUrl(r0, r1, r2)
            if (r7 == 0) goto L_0x004b
            com.jiubang.commerce.ad.url.ParseAdUrlResponseBean r5 = new com.jiubang.commerce.ad.url.ParseAdUrlResponseBean
            r7 = 3
            java.lang.String r9 = "srcUrl is market url"
            long r10 = java.lang.System.currentTimeMillis()
            long r10 = r10 - r20
            r8 = r28
            r5.<init>(r6, r7, r8, r9, r10)
            goto L_0x001c
        L_0x004b:
            com.jiubang.commerce.ad.manager.PreloadingControlManager r7 = com.jiubang.commerce.ad.manager.PreloadingControlManager.getInstance(r23)
            boolean r7 = r7.canPreloading()
            if (r7 != 0) goto L_0x00b1
            com.jiubang.commerce.ad.url.ParseAdUrlResponseBean r5 = new com.jiubang.commerce.ad.url.ParseAdUrlResponseBean
            r7 = 1
            java.lang.String r9 = "close to pre load"
            long r10 = java.lang.System.currentTimeMillis()
            long r10 = r10 - r20
            r8 = r28
            r5.<init>(r6, r7, r8, r9, r10)
            boolean r7 = com.jb.ga0.commerce.util.LogUtils.isShowLog()
            if (r7 == 0) goto L_0x001c
            java.lang.String r7 = "Ad_SDK"
            java.lang.StringBuilder r8 = new java.lang.StringBuilder
            r8.<init>()
            java.lang.String r9 = "getHttpRedirectUrlFromLocation(close to pre load, moduleId:"
            java.lang.StringBuilder r8 = r8.append(r9)
            r0 = r25
            java.lang.StringBuilder r8 = r8.append(r0)
            java.lang.String r9 = ", mapId:"
            java.lang.StringBuilder r8 = r8.append(r9)
            r0 = r26
            java.lang.StringBuilder r8 = r8.append(r0)
            java.lang.String r9 = ", aId:"
            java.lang.StringBuilder r8 = r8.append(r9)
            r0 = r27
            java.lang.StringBuilder r8 = r8.append(r0)
            java.lang.String r9 = ", adUrl:"
            java.lang.StringBuilder r8 = r8.append(r9)
            r0 = r28
            java.lang.StringBuilder r8 = r8.append(r0)
            java.lang.String r9 = ")"
            java.lang.StringBuilder r8 = r8.append(r9)
            java.lang.String r8 = r8.toString()
            com.jb.ga0.commerce.util.LogUtils.d(r7, r8)
            goto L_0x001c
        L_0x00b1:
            r12 = r28
        L_0x00b3:
            if (r12 != 0) goto L_0x00c6
            com.jiubang.commerce.ad.url.ParseAdUrlResponseBean r5 = new com.jiubang.commerce.ad.url.ParseAdUrlResponseBean     // Catch:{ IllegalArgumentException -> 0x01f1, ConnectTimeoutException -> 0x023f, SocketTimeoutException -> 0x032a, ClientProtocolException -> 0x0354, IOException -> 0x037e, Exception -> 0x03a8 }
            r7 = 1
            r8 = 0
            java.lang.String r9 = "url is null"
            long r10 = java.lang.System.currentTimeMillis()     // Catch:{ IllegalArgumentException -> 0x01f1, ConnectTimeoutException -> 0x023f, SocketTimeoutException -> 0x032a, ClientProtocolException -> 0x0354, IOException -> 0x037e, Exception -> 0x03a8 }
            long r10 = r10 - r20
            r5.<init>(r6, r7, r8, r9, r10)     // Catch:{ IllegalArgumentException -> 0x01f1, ConnectTimeoutException -> 0x023f, SocketTimeoutException -> 0x032a, ClientProtocolException -> 0x0354, IOException -> 0x037e, Exception -> 0x03a8 }
            goto L_0x001c
        L_0x00c6:
            r0 = r23
            r1 = r24
            boolean r7 = judgeUrl(r0, r1, r12)     // Catch:{ IllegalArgumentException -> 0x01f1, ConnectTimeoutException -> 0x023f, SocketTimeoutException -> 0x032a, ClientProtocolException -> 0x0354, IOException -> 0x037e, Exception -> 0x03a8 }
            if (r7 == 0) goto L_0x00e1
            com.jiubang.commerce.ad.url.ParseAdUrlResponseBean r5 = new com.jiubang.commerce.ad.url.ParseAdUrlResponseBean     // Catch:{ IllegalArgumentException -> 0x01f1, ConnectTimeoutException -> 0x023f, SocketTimeoutException -> 0x032a, ClientProtocolException -> 0x0354, IOException -> 0x037e, Exception -> 0x03a8 }
            r7 = 3
            java.lang.String r9 = "parse success"
            long r10 = java.lang.System.currentTimeMillis()     // Catch:{ IllegalArgumentException -> 0x01f1, ConnectTimeoutException -> 0x023f, SocketTimeoutException -> 0x032a, ClientProtocolException -> 0x0354, IOException -> 0x037e, Exception -> 0x03a8 }
            long r10 = r10 - r20
            r8 = r12
            r5.<init>(r6, r7, r8, r9, r10)     // Catch:{ IllegalArgumentException -> 0x01f1, ConnectTimeoutException -> 0x023f, SocketTimeoutException -> 0x032a, ClientProtocolException -> 0x0354, IOException -> 0x037e, Exception -> 0x03a8 }
            goto L_0x001c
        L_0x00e1:
            org.apache.http.client.methods.HttpGet r16 = new org.apache.http.client.methods.HttpGet     // Catch:{ IllegalArgumentException -> 0x01f1, ConnectTimeoutException -> 0x023f, SocketTimeoutException -> 0x032a, ClientProtocolException -> 0x0354, IOException -> 0x037e, Exception -> 0x03a8 }
            r0 = r16
            r0.<init>(r12)     // Catch:{ IllegalArgumentException -> 0x01f1, ConnectTimeoutException -> 0x023f, SocketTimeoutException -> 0x032a, ClientProtocolException -> 0x0354, IOException -> 0x037e, Exception -> 0x03a8 }
            if (r24 == 0) goto L_0x0211
            r0 = r24
            r1 = r23
            java.lang.String r22 = r0.getUAStr(r1)     // Catch:{ IllegalArgumentException -> 0x01f1, ConnectTimeoutException -> 0x023f, SocketTimeoutException -> 0x032a, ClientProtocolException -> 0x0354, IOException -> 0x037e, Exception -> 0x03a8 }
            if (r22 == 0) goto L_0x01c3
            java.lang.String r7 = "UA"
            java.lang.StringBuilder r8 = new java.lang.StringBuilder     // Catch:{ IllegalArgumentException -> 0x01f1, ConnectTimeoutException -> 0x023f, SocketTimeoutException -> 0x032a, ClientProtocolException -> 0x0354, IOException -> 0x037e, Exception -> 0x03a8 }
            r8.<init>()     // Catch:{ IllegalArgumentException -> 0x01f1, ConnectTimeoutException -> 0x023f, SocketTimeoutException -> 0x032a, ClientProtocolException -> 0x0354, IOException -> 0x037e, Exception -> 0x03a8 }
            java.lang.String r9 = "模块ID:"
            java.lang.StringBuilder r8 = r8.append(r9)     // Catch:{ IllegalArgumentException -> 0x01f1, ConnectTimeoutException -> 0x023f, SocketTimeoutException -> 0x032a, ClientProtocolException -> 0x0354, IOException -> 0x037e, Exception -> 0x03a8 }
            r0 = r25
            java.lang.StringBuilder r8 = r8.append(r0)     // Catch:{ IllegalArgumentException -> 0x01f1, ConnectTimeoutException -> 0x023f, SocketTimeoutException -> 0x032a, ClientProtocolException -> 0x0354, IOException -> 0x037e, Exception -> 0x03a8 }
            java.lang.String r9 = " 需要解析的URL:"
            java.lang.StringBuilder r8 = r8.append(r9)     // Catch:{ IllegalArgumentException -> 0x01f1, ConnectTimeoutException -> 0x023f, SocketTimeoutException -> 0x032a, ClientProtocolException -> 0x0354, IOException -> 0x037e, Exception -> 0x03a8 }
            r0 = r28
            java.lang.StringBuilder r8 = r8.append(r0)     // Catch:{ IllegalArgumentException -> 0x01f1, ConnectTimeoutException -> 0x023f, SocketTimeoutException -> 0x032a, ClientProtocolException -> 0x0354, IOException -> 0x037e, Exception -> 0x03a8 }
            java.lang.String r9 = " 设置User-Agent"
            java.lang.StringBuilder r8 = r8.append(r9)     // Catch:{ IllegalArgumentException -> 0x01f1, ConnectTimeoutException -> 0x023f, SocketTimeoutException -> 0x032a, ClientProtocolException -> 0x0354, IOException -> 0x037e, Exception -> 0x03a8 }
            java.lang.String r9 = " UA="
            java.lang.StringBuilder r8 = r8.append(r9)     // Catch:{ IllegalArgumentException -> 0x01f1, ConnectTimeoutException -> 0x023f, SocketTimeoutException -> 0x032a, ClientProtocolException -> 0x0354, IOException -> 0x037e, Exception -> 0x03a8 }
            r0 = r22
            java.lang.StringBuilder r8 = r8.append(r0)     // Catch:{ IllegalArgumentException -> 0x01f1, ConnectTimeoutException -> 0x023f, SocketTimeoutException -> 0x032a, ClientProtocolException -> 0x0354, IOException -> 0x037e, Exception -> 0x03a8 }
            java.lang.String r8 = r8.toString()     // Catch:{ IllegalArgumentException -> 0x01f1, ConnectTimeoutException -> 0x023f, SocketTimeoutException -> 0x032a, ClientProtocolException -> 0x0354, IOException -> 0x037e, Exception -> 0x03a8 }
            com.jb.ga0.commerce.util.LogUtils.d(r7, r8)     // Catch:{ IllegalArgumentException -> 0x01f1, ConnectTimeoutException -> 0x023f, SocketTimeoutException -> 0x032a, ClientProtocolException -> 0x0354, IOException -> 0x037e, Exception -> 0x03a8 }
            java.lang.String r7 = "User-Agent"
            r0 = r16
            r1 = r22
            r0.addHeader(r7, r1)     // Catch:{ IllegalArgumentException -> 0x01f1, ConnectTimeoutException -> 0x023f, SocketTimeoutException -> 0x032a, ClientProtocolException -> 0x0354, IOException -> 0x037e, Exception -> 0x03a8 }
        L_0x0135:
            org.apache.http.params.BasicHttpParams r17 = new org.apache.http.params.BasicHttpParams     // Catch:{ IllegalArgumentException -> 0x01f1, ConnectTimeoutException -> 0x023f, SocketTimeoutException -> 0x032a, ClientProtocolException -> 0x0354, IOException -> 0x037e, Exception -> 0x03a8 }
            r17.<init>()     // Catch:{ IllegalArgumentException -> 0x01f1, ConnectTimeoutException -> 0x023f, SocketTimeoutException -> 0x032a, ClientProtocolException -> 0x0354, IOException -> 0x037e, Exception -> 0x03a8 }
            java.lang.String r7 = "http.protocol.handle-redirects"
            r8 = 0
            java.lang.Boolean r8 = java.lang.Boolean.valueOf(r8)     // Catch:{ IllegalArgumentException -> 0x01f1, ConnectTimeoutException -> 0x023f, SocketTimeoutException -> 0x032a, ClientProtocolException -> 0x0354, IOException -> 0x037e, Exception -> 0x03a8 }
            r0 = r17
            r0.setParameter(r7, r8)     // Catch:{ IllegalArgumentException -> 0x01f1, ConnectTimeoutException -> 0x023f, SocketTimeoutException -> 0x032a, ClientProtocolException -> 0x0354, IOException -> 0x037e, Exception -> 0x03a8 }
            java.lang.String r7 = "http.connection.timeout"
            r8 = 15000(0x3a98, float:2.102E-41)
            java.lang.Integer r8 = java.lang.Integer.valueOf(r8)     // Catch:{ IllegalArgumentException -> 0x01f1, ConnectTimeoutException -> 0x023f, SocketTimeoutException -> 0x032a, ClientProtocolException -> 0x0354, IOException -> 0x037e, Exception -> 0x03a8 }
            r0 = r17
            r0.setParameter(r7, r8)     // Catch:{ IllegalArgumentException -> 0x01f1, ConnectTimeoutException -> 0x023f, SocketTimeoutException -> 0x032a, ClientProtocolException -> 0x0354, IOException -> 0x037e, Exception -> 0x03a8 }
            java.lang.String r7 = "http.socket.timeout"
            r8 = 15000(0x3a98, float:2.102E-41)
            java.lang.Integer r8 = java.lang.Integer.valueOf(r8)     // Catch:{ IllegalArgumentException -> 0x01f1, ConnectTimeoutException -> 0x023f, SocketTimeoutException -> 0x032a, ClientProtocolException -> 0x0354, IOException -> 0x037e, Exception -> 0x03a8 }
            r0 = r17
            r0.setParameter(r7, r8)     // Catch:{ IllegalArgumentException -> 0x01f1, ConnectTimeoutException -> 0x023f, SocketTimeoutException -> 0x032a, ClientProtocolException -> 0x0354, IOException -> 0x037e, Exception -> 0x03a8 }
            r16.setParams(r17)     // Catch:{ IllegalArgumentException -> 0x01f1, ConnectTimeoutException -> 0x023f, SocketTimeoutException -> 0x032a, ClientProtocolException -> 0x0354, IOException -> 0x037e, Exception -> 0x03a8 }
            org.apache.http.impl.client.DefaultHttpClient r4 = new org.apache.http.impl.client.DefaultHttpClient     // Catch:{ IllegalArgumentException -> 0x01f1, ConnectTimeoutException -> 0x023f, SocketTimeoutException -> 0x032a, ClientProtocolException -> 0x0354, IOException -> 0x037e, Exception -> 0x03a8 }
            r4.<init>()     // Catch:{ IllegalArgumentException -> 0x01f1, ConnectTimeoutException -> 0x023f, SocketTimeoutException -> 0x032a, ClientProtocolException -> 0x0354, IOException -> 0x037e, Exception -> 0x03a8 }
            r0 = r16
            org.apache.http.HttpResponse r18 = r4.execute(r0)     // Catch:{ IllegalArgumentException -> 0x01f1, ConnectTimeoutException -> 0x023f, SocketTimeoutException -> 0x032a, ClientProtocolException -> 0x0354, IOException -> 0x037e, Exception -> 0x03a8 }
            org.apache.http.StatusLine r7 = r18.getStatusLine()     // Catch:{ IllegalArgumentException -> 0x01f1, ConnectTimeoutException -> 0x023f, SocketTimeoutException -> 0x032a, ClientProtocolException -> 0x0354, IOException -> 0x037e, Exception -> 0x03a8 }
            int r6 = r7.getStatusCode()     // Catch:{ IllegalArgumentException -> 0x01f1, ConnectTimeoutException -> 0x023f, SocketTimeoutException -> 0x032a, ClientProtocolException -> 0x0354, IOException -> 0x037e, Exception -> 0x03a8 }
            r7 = 302(0x12e, float:4.23E-43)
            if (r6 == r7) goto L_0x0182
            r7 = 301(0x12d, float:4.22E-43)
            if (r6 == r7) goto L_0x0182
            r7 = 303(0x12f, float:4.25E-43)
            if (r6 != r7) goto L_0x027e
        L_0x0182:
            java.lang.String r7 = "Location"
            r0 = r18
            org.apache.http.Header[] r15 = r0.getHeaders(r7)     // Catch:{ IllegalArgumentException -> 0x01f1, ConnectTimeoutException -> 0x023f, SocketTimeoutException -> 0x032a, ClientProtocolException -> 0x0354, IOException -> 0x037e, Exception -> 0x03a8 }
            if (r15 == 0) goto L_0x0269
            int r7 = r15.length     // Catch:{ IllegalArgumentException -> 0x01f1, ConnectTimeoutException -> 0x023f, SocketTimeoutException -> 0x032a, ClientProtocolException -> 0x0354, IOException -> 0x037e, Exception -> 0x03a8 }
            if (r7 <= 0) goto L_0x0269
            r7 = 0
            r7 = r15[r7]     // Catch:{ IllegalArgumentException -> 0x01f1, ConnectTimeoutException -> 0x023f, SocketTimeoutException -> 0x032a, ClientProtocolException -> 0x0354, IOException -> 0x037e, Exception -> 0x03a8 }
            if (r7 == 0) goto L_0x0269
            r7 = 0
            r7 = r15[r7]     // Catch:{ IllegalArgumentException -> 0x01f1, ConnectTimeoutException -> 0x023f, SocketTimeoutException -> 0x032a, ClientProtocolException -> 0x0354, IOException -> 0x037e, Exception -> 0x03a8 }
            java.lang.String r12 = r7.getValue()     // Catch:{ IllegalArgumentException -> 0x01f1, ConnectTimeoutException -> 0x023f, SocketTimeoutException -> 0x032a, ClientProtocolException -> 0x0354, IOException -> 0x037e, Exception -> 0x03a8 }
        L_0x019b:
            boolean r7 = android.text.TextUtils.isEmpty(r12)     // Catch:{ IllegalArgumentException -> 0x01f1, ConnectTimeoutException -> 0x023f, SocketTimeoutException -> 0x032a, ClientProtocolException -> 0x0354, IOException -> 0x037e, Exception -> 0x03a8 }
            if (r7 != 0) goto L_0x026d
            java.lang.String r7 = "{"
            int r7 = r12.indexOf(r7)     // Catch:{ IllegalArgumentException -> 0x01f1, ConnectTimeoutException -> 0x023f, SocketTimeoutException -> 0x032a, ClientProtocolException -> 0x0354, IOException -> 0x037e, Exception -> 0x03a8 }
            if (r7 > 0) goto L_0x01b1
            java.lang.String r7 = "}"
            int r7 = r12.indexOf(r7)     // Catch:{ IllegalArgumentException -> 0x01f1, ConnectTimeoutException -> 0x023f, SocketTimeoutException -> 0x032a, ClientProtocolException -> 0x0354, IOException -> 0x037e, Exception -> 0x03a8 }
            if (r7 <= 0) goto L_0x00b3
        L_0x01b1:
            java.lang.String r7 = "\\{"
            java.lang.String r8 = ""
            java.lang.String r12 = r12.replaceAll(r7, r8)     // Catch:{ IllegalArgumentException -> 0x01f1, ConnectTimeoutException -> 0x023f, SocketTimeoutException -> 0x032a, ClientProtocolException -> 0x0354, IOException -> 0x037e, Exception -> 0x03a8 }
            java.lang.String r7 = "\\}"
            java.lang.String r8 = ""
            java.lang.String r12 = r12.replaceAll(r7, r8)     // Catch:{ IllegalArgumentException -> 0x01f1, ConnectTimeoutException -> 0x023f, SocketTimeoutException -> 0x032a, ClientProtocolException -> 0x0354, IOException -> 0x037e, Exception -> 0x03a8 }
            goto L_0x00b3
        L_0x01c3:
            java.lang.String r7 = "UA"
            java.lang.StringBuilder r8 = new java.lang.StringBuilder     // Catch:{ IllegalArgumentException -> 0x01f1, ConnectTimeoutException -> 0x023f, SocketTimeoutException -> 0x032a, ClientProtocolException -> 0x0354, IOException -> 0x037e, Exception -> 0x03a8 }
            r8.<init>()     // Catch:{ IllegalArgumentException -> 0x01f1, ConnectTimeoutException -> 0x023f, SocketTimeoutException -> 0x032a, ClientProtocolException -> 0x0354, IOException -> 0x037e, Exception -> 0x03a8 }
            java.lang.String r9 = "模块ID:"
            java.lang.StringBuilder r8 = r8.append(r9)     // Catch:{ IllegalArgumentException -> 0x01f1, ConnectTimeoutException -> 0x023f, SocketTimeoutException -> 0x032a, ClientProtocolException -> 0x0354, IOException -> 0x037e, Exception -> 0x03a8 }
            r0 = r25
            java.lang.StringBuilder r8 = r8.append(r0)     // Catch:{ IllegalArgumentException -> 0x01f1, ConnectTimeoutException -> 0x023f, SocketTimeoutException -> 0x032a, ClientProtocolException -> 0x0354, IOException -> 0x037e, Exception -> 0x03a8 }
            java.lang.String r9 = " 需要解析的URL:"
            java.lang.StringBuilder r8 = r8.append(r9)     // Catch:{ IllegalArgumentException -> 0x01f1, ConnectTimeoutException -> 0x023f, SocketTimeoutException -> 0x032a, ClientProtocolException -> 0x0354, IOException -> 0x037e, Exception -> 0x03a8 }
            r0 = r28
            java.lang.StringBuilder r8 = r8.append(r0)     // Catch:{ IllegalArgumentException -> 0x01f1, ConnectTimeoutException -> 0x023f, SocketTimeoutException -> 0x032a, ClientProtocolException -> 0x0354, IOException -> 0x037e, Exception -> 0x03a8 }
            java.lang.String r9 = " 不设置User-Agent"
            java.lang.StringBuilder r8 = r8.append(r9)     // Catch:{ IllegalArgumentException -> 0x01f1, ConnectTimeoutException -> 0x023f, SocketTimeoutException -> 0x032a, ClientProtocolException -> 0x0354, IOException -> 0x037e, Exception -> 0x03a8 }
            java.lang.String r8 = r8.toString()     // Catch:{ IllegalArgumentException -> 0x01f1, ConnectTimeoutException -> 0x023f, SocketTimeoutException -> 0x032a, ClientProtocolException -> 0x0354, IOException -> 0x037e, Exception -> 0x03a8 }
            com.jb.ga0.commerce.util.LogUtils.d(r7, r8)     // Catch:{ IllegalArgumentException -> 0x01f1, ConnectTimeoutException -> 0x023f, SocketTimeoutException -> 0x032a, ClientProtocolException -> 0x0354, IOException -> 0x037e, Exception -> 0x03a8 }
            goto L_0x0135
        L_0x01f1:
            r13 = move-exception
            java.lang.String r7 = r13.getMessage()
            boolean r7 = isInValidUrlErrorMsg(r7)
            if (r7 == 0) goto L_0x0304
            com.jiubang.commerce.ad.url.ParseAdUrlResponseBean r5 = new com.jiubang.commerce.ad.url.ParseAdUrlResponseBean
            r7 = 3
            java.lang.String r9 = "parse success: invalid url"
            long r10 = java.lang.System.currentTimeMillis()
            long r10 = r10 - r20
            r8 = r28
            r5.<init>(r6, r7, r8, r9, r10)
        L_0x020c:
            r13.printStackTrace()
            goto L_0x001c
        L_0x0211:
            java.lang.String r7 = "UA"
            java.lang.StringBuilder r8 = new java.lang.StringBuilder     // Catch:{ IllegalArgumentException -> 0x01f1, ConnectTimeoutException -> 0x023f, SocketTimeoutException -> 0x032a, ClientProtocolException -> 0x0354, IOException -> 0x037e, Exception -> 0x03a8 }
            r8.<init>()     // Catch:{ IllegalArgumentException -> 0x01f1, ConnectTimeoutException -> 0x023f, SocketTimeoutException -> 0x032a, ClientProtocolException -> 0x0354, IOException -> 0x037e, Exception -> 0x03a8 }
            java.lang.String r9 = "模块ID:"
            java.lang.StringBuilder r8 = r8.append(r9)     // Catch:{ IllegalArgumentException -> 0x01f1, ConnectTimeoutException -> 0x023f, SocketTimeoutException -> 0x032a, ClientProtocolException -> 0x0354, IOException -> 0x037e, Exception -> 0x03a8 }
            r0 = r25
            java.lang.StringBuilder r8 = r8.append(r0)     // Catch:{ IllegalArgumentException -> 0x01f1, ConnectTimeoutException -> 0x023f, SocketTimeoutException -> 0x032a, ClientProtocolException -> 0x0354, IOException -> 0x037e, Exception -> 0x03a8 }
            java.lang.String r9 = " 需要解析的URL:"
            java.lang.StringBuilder r8 = r8.append(r9)     // Catch:{ IllegalArgumentException -> 0x01f1, ConnectTimeoutException -> 0x023f, SocketTimeoutException -> 0x032a, ClientProtocolException -> 0x0354, IOException -> 0x037e, Exception -> 0x03a8 }
            r0 = r28
            java.lang.StringBuilder r8 = r8.append(r0)     // Catch:{ IllegalArgumentException -> 0x01f1, ConnectTimeoutException -> 0x023f, SocketTimeoutException -> 0x032a, ClientProtocolException -> 0x0354, IOException -> 0x037e, Exception -> 0x03a8 }
            java.lang.String r9 = " 不设置User-Agent"
            java.lang.StringBuilder r8 = r8.append(r9)     // Catch:{ IllegalArgumentException -> 0x01f1, ConnectTimeoutException -> 0x023f, SocketTimeoutException -> 0x032a, ClientProtocolException -> 0x0354, IOException -> 0x037e, Exception -> 0x03a8 }
            java.lang.String r8 = r8.toString()     // Catch:{ IllegalArgumentException -> 0x01f1, ConnectTimeoutException -> 0x023f, SocketTimeoutException -> 0x032a, ClientProtocolException -> 0x0354, IOException -> 0x037e, Exception -> 0x03a8 }
            com.jb.ga0.commerce.util.LogUtils.d(r7, r8)     // Catch:{ IllegalArgumentException -> 0x01f1, ConnectTimeoutException -> 0x023f, SocketTimeoutException -> 0x032a, ClientProtocolException -> 0x0354, IOException -> 0x037e, Exception -> 0x03a8 }
            goto L_0x0135
        L_0x023f:
            r13 = move-exception
            com.jiubang.commerce.ad.url.ParseAdUrlResponseBean r5 = new com.jiubang.commerce.ad.url.ParseAdUrlResponseBean
            r7 = 2
            r8 = 0
            java.lang.StringBuilder r9 = new java.lang.StringBuilder
            r9.<init>()
            java.lang.String r10 = "ConnectTimeoutException:"
            java.lang.StringBuilder r9 = r9.append(r10)
            java.lang.String r10 = r13.getMessage()
            java.lang.StringBuilder r9 = r9.append(r10)
            java.lang.String r9 = r9.toString()
            long r10 = java.lang.System.currentTimeMillis()
            long r10 = r10 - r20
            r5.<init>(r6, r7, r8, r9, r10)
            r13.printStackTrace()
            goto L_0x001c
        L_0x0269:
            java.lang.String r12 = ""
            goto L_0x019b
        L_0x026d:
            com.jiubang.commerce.ad.url.ParseAdUrlResponseBean r5 = new com.jiubang.commerce.ad.url.ParseAdUrlResponseBean     // Catch:{ IllegalArgumentException -> 0x01f1, ConnectTimeoutException -> 0x023f, SocketTimeoutException -> 0x032a, ClientProtocolException -> 0x0354, IOException -> 0x037e, Exception -> 0x03a8 }
            r7 = 1
            r8 = 0
            java.lang.String r9 = "headers is null"
            long r10 = java.lang.System.currentTimeMillis()     // Catch:{ IllegalArgumentException -> 0x01f1, ConnectTimeoutException -> 0x023f, SocketTimeoutException -> 0x032a, ClientProtocolException -> 0x0354, IOException -> 0x037e, Exception -> 0x03a8 }
            long r10 = r10 - r20
            r5.<init>(r6, r7, r8, r9, r10)     // Catch:{ IllegalArgumentException -> 0x01f1, ConnectTimeoutException -> 0x023f, SocketTimeoutException -> 0x032a, ClientProtocolException -> 0x0354, IOException -> 0x037e, Exception -> 0x03a8 }
            goto L_0x001c
        L_0x027e:
            r7 = 200(0xc8, float:2.8E-43)
            if (r6 != r7) goto L_0x02dc
            org.apache.http.HttpEntity r14 = r18.getEntity()     // Catch:{ Exception -> 0x02c4, IllegalArgumentException -> 0x01f1, ConnectTimeoutException -> 0x023f, SocketTimeoutException -> 0x032a, ClientProtocolException -> 0x0354, IOException -> 0x037e }
            if (r14 == 0) goto L_0x02d9
            java.io.InputStream r7 = r14.getContent()     // Catch:{ Exception -> 0x02c4, IllegalArgumentException -> 0x01f1, ConnectTimeoutException -> 0x023f, SocketTimeoutException -> 0x032a, ClientProtocolException -> 0x0354, IOException -> 0x037e }
            java.lang.String r8 = "UTF-8"
            r9 = 50
            java.lang.String r19 = com.jiubang.commerce.utils.FileUtils.readInputStreamWithLength(r7, r8, r9)     // Catch:{ Exception -> 0x02c4, IllegalArgumentException -> 0x01f1, ConnectTimeoutException -> 0x023f, SocketTimeoutException -> 0x032a, ClientProtocolException -> 0x0354, IOException -> 0x037e }
        L_0x0294:
            boolean r7 = android.text.TextUtils.isEmpty(r19)     // Catch:{ Exception -> 0x02c4, IllegalArgumentException -> 0x01f1, ConnectTimeoutException -> 0x023f, SocketTimeoutException -> 0x032a, ClientProtocolException -> 0x0354, IOException -> 0x037e }
            if (r7 != 0) goto L_0x02c8
            java.lang.String r7 = "repeat click"
            r0 = r19
            boolean r7 = r0.equals(r7)     // Catch:{ Exception -> 0x02c4, IllegalArgumentException -> 0x01f1, ConnectTimeoutException -> 0x023f, SocketTimeoutException -> 0x032a, ClientProtocolException -> 0x0354, IOException -> 0x037e }
            if (r7 == 0) goto L_0x02c8
            java.lang.String r7 = "Ad_SDK"
            java.lang.StringBuilder r8 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x02c4, IllegalArgumentException -> 0x01f1, ConnectTimeoutException -> 0x023f, SocketTimeoutException -> 0x032a, ClientProtocolException -> 0x0354, IOException -> 0x037e }
            r8.<init>()     // Catch:{ Exception -> 0x02c4, IllegalArgumentException -> 0x01f1, ConnectTimeoutException -> 0x023f, SocketTimeoutException -> 0x032a, ClientProtocolException -> 0x0354, IOException -> 0x037e }
            java.lang.String r9 = "getHttpRedirectUrlFromLocation(repeat click, "
            java.lang.StringBuilder r8 = r8.append(r9)     // Catch:{ Exception -> 0x02c4, IllegalArgumentException -> 0x01f1, ConnectTimeoutException -> 0x023f, SocketTimeoutException -> 0x032a, ClientProtocolException -> 0x0354, IOException -> 0x037e }
            java.lang.StringBuilder r8 = r8.append(r12)     // Catch:{ Exception -> 0x02c4, IllegalArgumentException -> 0x01f1, ConnectTimeoutException -> 0x023f, SocketTimeoutException -> 0x032a, ClientProtocolException -> 0x0354, IOException -> 0x037e }
            java.lang.String r9 = ")"
            java.lang.StringBuilder r8 = r8.append(r9)     // Catch:{ Exception -> 0x02c4, IllegalArgumentException -> 0x01f1, ConnectTimeoutException -> 0x023f, SocketTimeoutException -> 0x032a, ClientProtocolException -> 0x0354, IOException -> 0x037e }
            java.lang.String r8 = r8.toString()     // Catch:{ Exception -> 0x02c4, IllegalArgumentException -> 0x01f1, ConnectTimeoutException -> 0x023f, SocketTimeoutException -> 0x032a, ClientProtocolException -> 0x0354, IOException -> 0x037e }
            com.jb.ga0.commerce.util.LogUtils.d(r7, r8)     // Catch:{ Exception -> 0x02c4, IllegalArgumentException -> 0x01f1, ConnectTimeoutException -> 0x023f, SocketTimeoutException -> 0x032a, ClientProtocolException -> 0x0354, IOException -> 0x037e }
            goto L_0x002d
        L_0x02c4:
            r13 = move-exception
            r13.printStackTrace()     // Catch:{ IllegalArgumentException -> 0x01f1, ConnectTimeoutException -> 0x023f, SocketTimeoutException -> 0x032a, ClientProtocolException -> 0x0354, IOException -> 0x037e, Exception -> 0x03a8 }
        L_0x02c8:
            com.jiubang.commerce.ad.url.ParseAdUrlResponseBean r5 = new com.jiubang.commerce.ad.url.ParseAdUrlResponseBean     // Catch:{ IllegalArgumentException -> 0x01f1, ConnectTimeoutException -> 0x023f, SocketTimeoutException -> 0x032a, ClientProtocolException -> 0x0354, IOException -> 0x037e, Exception -> 0x03a8 }
            r7 = 3
            java.lang.String r9 = "parse success: no gp url"
            long r10 = java.lang.System.currentTimeMillis()     // Catch:{ IllegalArgumentException -> 0x01f1, ConnectTimeoutException -> 0x023f, SocketTimeoutException -> 0x032a, ClientProtocolException -> 0x0354, IOException -> 0x037e, Exception -> 0x03a8 }
            long r10 = r10 - r20
            r8 = r12
            r5.<init>(r6, r7, r8, r9, r10)     // Catch:{ IllegalArgumentException -> 0x01f1, ConnectTimeoutException -> 0x023f, SocketTimeoutException -> 0x032a, ClientProtocolException -> 0x0354, IOException -> 0x037e, Exception -> 0x03a8 }
            goto L_0x001c
        L_0x02d9:
            java.lang.String r19 = ""
            goto L_0x0294
        L_0x02dc:
            com.jiubang.commerce.ad.url.ParseAdUrlResponseBean r5 = new com.jiubang.commerce.ad.url.ParseAdUrlResponseBean     // Catch:{ IllegalArgumentException -> 0x01f1, ConnectTimeoutException -> 0x023f, SocketTimeoutException -> 0x032a, ClientProtocolException -> 0x0354, IOException -> 0x037e, Exception -> 0x03a8 }
            r7 = 1
            r8 = 0
            java.lang.StringBuilder r9 = new java.lang.StringBuilder     // Catch:{ IllegalArgumentException -> 0x01f1, ConnectTimeoutException -> 0x023f, SocketTimeoutException -> 0x032a, ClientProtocolException -> 0x0354, IOException -> 0x037e, Exception -> 0x03a8 }
            r9.<init>()     // Catch:{ IllegalArgumentException -> 0x01f1, ConnectTimeoutException -> 0x023f, SocketTimeoutException -> 0x032a, ClientProtocolException -> 0x0354, IOException -> 0x037e, Exception -> 0x03a8 }
            java.lang.String r10 = "network statusCode is not 200, 301, 302, 303::->["
            java.lang.StringBuilder r9 = r9.append(r10)     // Catch:{ IllegalArgumentException -> 0x01f1, ConnectTimeoutException -> 0x023f, SocketTimeoutException -> 0x032a, ClientProtocolException -> 0x0354, IOException -> 0x037e, Exception -> 0x03a8 }
            java.lang.StringBuilder r9 = r9.append(r6)     // Catch:{ IllegalArgumentException -> 0x01f1, ConnectTimeoutException -> 0x023f, SocketTimeoutException -> 0x032a, ClientProtocolException -> 0x0354, IOException -> 0x037e, Exception -> 0x03a8 }
            java.lang.String r10 = "]"
            java.lang.StringBuilder r9 = r9.append(r10)     // Catch:{ IllegalArgumentException -> 0x01f1, ConnectTimeoutException -> 0x023f, SocketTimeoutException -> 0x032a, ClientProtocolException -> 0x0354, IOException -> 0x037e, Exception -> 0x03a8 }
            java.lang.String r9 = r9.toString()     // Catch:{ IllegalArgumentException -> 0x01f1, ConnectTimeoutException -> 0x023f, SocketTimeoutException -> 0x032a, ClientProtocolException -> 0x0354, IOException -> 0x037e, Exception -> 0x03a8 }
            long r10 = java.lang.System.currentTimeMillis()     // Catch:{ IllegalArgumentException -> 0x01f1, ConnectTimeoutException -> 0x023f, SocketTimeoutException -> 0x032a, ClientProtocolException -> 0x0354, IOException -> 0x037e, Exception -> 0x03a8 }
            long r10 = r10 - r20
            r5.<init>(r6, r7, r8, r9, r10)     // Catch:{ IllegalArgumentException -> 0x01f1, ConnectTimeoutException -> 0x023f, SocketTimeoutException -> 0x032a, ClientProtocolException -> 0x0354, IOException -> 0x037e, Exception -> 0x03a8 }
            goto L_0x001c
        L_0x0304:
            com.jiubang.commerce.ad.url.ParseAdUrlResponseBean r5 = new com.jiubang.commerce.ad.url.ParseAdUrlResponseBean
            r7 = 2
            r8 = 0
            java.lang.StringBuilder r9 = new java.lang.StringBuilder
            r9.<init>()
            java.lang.String r10 = "IllegalArgumentException:"
            java.lang.StringBuilder r9 = r9.append(r10)
            java.lang.String r10 = r13.getMessage()
            java.lang.StringBuilder r9 = r9.append(r10)
            java.lang.String r9 = r9.toString()
            long r10 = java.lang.System.currentTimeMillis()
            long r10 = r10 - r20
            r5.<init>(r6, r7, r8, r9, r10)
            goto L_0x020c
        L_0x032a:
            r13 = move-exception
            com.jiubang.commerce.ad.url.ParseAdUrlResponseBean r5 = new com.jiubang.commerce.ad.url.ParseAdUrlResponseBean
            r7 = 2
            r8 = 0
            java.lang.StringBuilder r9 = new java.lang.StringBuilder
            r9.<init>()
            java.lang.String r10 = "SocketTimeoutException:"
            java.lang.StringBuilder r9 = r9.append(r10)
            java.lang.String r10 = r13.getMessage()
            java.lang.StringBuilder r9 = r9.append(r10)
            java.lang.String r9 = r9.toString()
            long r10 = java.lang.System.currentTimeMillis()
            long r10 = r10 - r20
            r5.<init>(r6, r7, r8, r9, r10)
            r13.printStackTrace()
            goto L_0x001c
        L_0x0354:
            r13 = move-exception
            com.jiubang.commerce.ad.url.ParseAdUrlResponseBean r5 = new com.jiubang.commerce.ad.url.ParseAdUrlResponseBean
            r7 = 2
            r8 = 0
            java.lang.StringBuilder r9 = new java.lang.StringBuilder
            r9.<init>()
            java.lang.String r10 = "ClientProtocolException:"
            java.lang.StringBuilder r9 = r9.append(r10)
            java.lang.String r10 = r13.getMessage()
            java.lang.StringBuilder r9 = r9.append(r10)
            java.lang.String r9 = r9.toString()
            long r10 = java.lang.System.currentTimeMillis()
            long r10 = r10 - r20
            r5.<init>(r6, r7, r8, r9, r10)
            r13.printStackTrace()
            goto L_0x001c
        L_0x037e:
            r13 = move-exception
            com.jiubang.commerce.ad.url.ParseAdUrlResponseBean r5 = new com.jiubang.commerce.ad.url.ParseAdUrlResponseBean
            r7 = 2
            r8 = 0
            java.lang.StringBuilder r9 = new java.lang.StringBuilder
            r9.<init>()
            java.lang.String r10 = "IOException:"
            java.lang.StringBuilder r9 = r9.append(r10)
            java.lang.String r10 = r13.getMessage()
            java.lang.StringBuilder r9 = r9.append(r10)
            java.lang.String r9 = r9.toString()
            long r10 = java.lang.System.currentTimeMillis()
            long r10 = r10 - r20
            r5.<init>(r6, r7, r8, r9, r10)
            r13.printStackTrace()
            goto L_0x001c
        L_0x03a8:
            r13 = move-exception
            com.jiubang.commerce.ad.url.ParseAdUrlResponseBean r5 = new com.jiubang.commerce.ad.url.ParseAdUrlResponseBean
            r7 = 2
            r8 = 0
            java.lang.String r9 = r13.getMessage()
            long r10 = java.lang.System.currentTimeMillis()
            long r10 = r10 - r20
            r5.<init>(r6, r7, r8, r9, r10)
            r13.printStackTrace()
            goto L_0x001c
        L_0x03bf:
            java.lang.String r12 = ""
            goto L_0x002d
        */
        throw new UnsupportedOperationException("Method not decompiled: com.jiubang.commerce.ad.url.AdRedirectUrlUtils.getHttpRedirectUrlFromLocation(android.content.Context, com.jiubang.commerce.ad.http.bean.ParamsBean, java.lang.String, java.lang.String, java.lang.String, java.lang.String):java.lang.String");
    }

    public static String getUserAgent(Context context) {
        if (sUserAgent == null) {
            sUserAgent = getDefaultUserAgent(context);
            if (sUserAgent == null) {
                sUserAgent = getCurrentUserAgent(context);
            }
        }
        return sUserAgent;
    }

    private static synchronized String getDefaultUserAgent(final Context context) {
        String str;
        synchronized (AdRedirectUrlUtils.class) {
            if (sUserAgent == null) {
                AdSdkThreadExecutorProxy.runOnMainThread(new Runnable() {
                    /* JADX WARNING: No exception handlers in catch block: Catch:{  } */
                    /* Code decompiled incorrectly, please refer to instructions dump. */
                    public void run() {
                        /*
                            r8 = this;
                            java.lang.Class<android.webkit.WebSettings> r4 = android.webkit.WebSettings.class
                            r5 = 2
                            java.lang.Class[] r5 = new java.lang.Class[r5]     // Catch:{ Throwable -> 0x003b }
                            r6 = 0
                            java.lang.Class<android.content.Context> r7 = android.content.Context.class
                            r5[r6] = r7     // Catch:{ Throwable -> 0x003b }
                            r6 = 1
                            java.lang.Class<android.webkit.WebView> r7 = android.webkit.WebView.class
                            r5[r6] = r7     // Catch:{ Throwable -> 0x003b }
                            java.lang.reflect.Constructor r0 = r4.getDeclaredConstructor(r5)     // Catch:{ Throwable -> 0x003b }
                            r4 = 1
                            r0.setAccessible(r4)     // Catch:{ Throwable -> 0x003b }
                            r4 = 2
                            java.lang.Object[] r4 = new java.lang.Object[r4]     // Catch:{ all -> 0x0035 }
                            r5 = 0
                            android.content.Context r6 = r2     // Catch:{ all -> 0x0035 }
                            r4[r5] = r6     // Catch:{ all -> 0x0035 }
                            r5 = 1
                            r6 = 0
                            r4[r5] = r6     // Catch:{ all -> 0x0035 }
                            java.lang.Object r3 = r0.newInstance(r4)     // Catch:{ all -> 0x0035 }
                            android.webkit.WebSettings r3 = (android.webkit.WebSettings) r3     // Catch:{ all -> 0x0035 }
                            java.lang.String r4 = r3.getUserAgentString()     // Catch:{ all -> 0x0035 }
                            java.lang.String unused = com.jiubang.commerce.ad.url.AdRedirectUrlUtils.sUserAgent = r4     // Catch:{ all -> 0x0035 }
                            r4 = 0
                            r0.setAccessible(r4)     // Catch:{ Throwable -> 0x003b }
                        L_0x0034:
                            return
                        L_0x0035:
                            r4 = move-exception
                            r5 = 0
                            r0.setAccessible(r5)     // Catch:{ Throwable -> 0x003b }
                            throw r4     // Catch:{ Throwable -> 0x003b }
                        L_0x003b:
                            r1 = move-exception
                            android.webkit.WebView r4 = new android.webkit.WebView     // Catch:{ Throwable -> 0x004f }
                            android.content.Context r5 = r2     // Catch:{ Throwable -> 0x004f }
                            r4.<init>(r5)     // Catch:{ Throwable -> 0x004f }
                            android.webkit.WebSettings r4 = r4.getSettings()     // Catch:{ Throwable -> 0x004f }
                            java.lang.String r4 = r4.getUserAgentString()     // Catch:{ Throwable -> 0x004f }
                            java.lang.String unused = com.jiubang.commerce.ad.url.AdRedirectUrlUtils.sUserAgent = r4     // Catch:{ Throwable -> 0x004f }
                            goto L_0x0034
                        L_0x004f:
                            r2 = move-exception
                            android.content.Context r4 = r2     // Catch:{ Throwable -> 0x005a }
                            java.lang.String r4 = android.webkit.WebSettings.getDefaultUserAgent(r4)     // Catch:{ Throwable -> 0x005a }
                            java.lang.String unused = com.jiubang.commerce.ad.url.AdRedirectUrlUtils.sUserAgent = r4     // Catch:{ Throwable -> 0x005a }
                            goto L_0x0034
                        L_0x005a:
                            r4 = move-exception
                            goto L_0x0034
                        */
                        throw new UnsupportedOperationException("Method not decompiled: com.jiubang.commerce.ad.url.AdRedirectUrlUtils.AnonymousClass1.run():void");
                    }
                });
            }
            str = sUserAgent;
        }
        return str;
    }

    private static synchronized String getCurrentUserAgent(Context context) {
        String format;
        synchronized (AdRedirectUrlUtils.class) {
            StringBuffer buffer = new StringBuffer();
            String version = Build.VERSION.RELEASE;
            if (version.length() > 0) {
                buffer.append(version);
            } else {
                buffer.append("1.0");
            }
            buffer.append("; ");
            String language = SystemUtils.getLanguage(context);
            if (language != null) {
                if ("en_us".equals(language.toLowerCase())) {
                    language = "en";
                }
                buffer.append(language.toLowerCase());
                String country = SystemUtils.getLocal(context);
                if (country != null) {
                    buffer.append("-");
                    buffer.append(country.toLowerCase());
                }
            } else {
                buffer.append("en");
            }
            if ("REL".equals(Build.VERSION.CODENAME)) {
                String model = Build.MODEL;
                if (model.length() > 0) {
                    buffer.append("; ");
                    buffer.append(model);
                }
            }
            String id = Build.ID;
            if (id.length() > 0) {
                buffer.append(" Build/");
                buffer.append(id);
            }
            format = String.format("Mozilla/5.0 (Linux; U; Android %s) AppleWebKit/533.1 (KHTML, like Gecko) Version/4.0 Mobile Safari/533.1", new Object[]{buffer});
        }
        return format;
    }

    private static boolean isInValidUrlErrorMsg(String msg) {
        if (!TextUtils.isEmpty(msg) && msg.contains("character in query at index")) {
            return true;
        }
        return false;
    }

    public static String handlePreloadAdUrl(String adUrl) {
        return (TextUtils.isEmpty(adUrl) || adUrl.endsWith("&preclick=1")) ? adUrl : adUrl + "&preclick=1";
    }

    static boolean judgeUrl(Context context, ParamsBean pb, String url) {
        if (!pb.isFinalGpJump()) {
            return GoogleMarketUtils.isMarketUrl(url);
        }
        GoogleMarketUtils.GPMarketUrlResult r = GoogleMarketUtils.isAbsoMarketUrl(url);
        accessUrl(context, pb, r.getBrowserUrl());
        return r.isGPUrl();
    }

    static void accessUrl(Context context, ParamsBean paramsBean, String desUrl) {
        if (!TextUtils.isEmpty(desUrl)) {
            try {
                HttpGet httpGet = new HttpGet(desUrl);
                if (paramsBean != null) {
                    String uaStr = paramsBean.getUAStr(context);
                    if (uaStr != null) {
                        LogUtils.d("UA", "accessUrl: 需要解析的URL:" + desUrl + " 设置User-Agent" + " UA=" + uaStr);
                        httpGet.addHeader("User-Agent", uaStr);
                    } else {
                        LogUtils.d("UA", "accessUrl: 需要解析的URL:" + desUrl + " 不设置User-Agent");
                    }
                } else {
                    LogUtils.d("UA", "accessUrl: 需要解析的URL:" + desUrl + " 不设置User-Agent");
                }
                HttpParams params = new BasicHttpParams();
                params.setParameter("http.protocol.handle-redirects", false);
                params.setParameter("http.connection.timeout", 15000);
                params.setParameter("http.socket.timeout", 15000);
                httpGet.setParams(params);
                new DefaultHttpClient().execute(httpGet);
            } catch (Throwable e) {
                e.printStackTrace();
            }
        }
    }
}
