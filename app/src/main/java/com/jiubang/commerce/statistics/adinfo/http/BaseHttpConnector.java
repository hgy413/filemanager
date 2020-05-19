package com.jiubang.commerce.statistics.adinfo.http;

import android.content.Context;
import com.gau.utils.net.HttpAdapter;
import com.gau.utils.net.IConnectListener;
import com.gau.utils.net.operator.IHttpOperator;
import com.gau.utils.net.request.THttpRequest;
import com.gau.utils.net.response.BasicResponse;
import com.gau.utils.net.response.IResponse;
import com.jb.ga0.commerce.util.LogUtils;
import com.jb.ga0.commerce.util.NetUtil;
import com.jb.ga0.commerce.util.http.AdvertJsonOperator;
import com.jb.ga0.commerce.util.io.StringUtils;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.HashMap;
import org.apache.http.HttpResponse;

public class BaseHttpConnector {
    private static DefaultHttpOperator sDefaultHttpOperator = new DefaultHttpOperator();
    public static String sGoogleAdId = "";
    private Context mContext;
    private String mHost;
    private HttpAdapter mHttpAdapter;

    public static class NetworkResultConstants {
        public static final int BUILD_REQUEST_ERROR = -3;
        public static final int NETWORK_PARSER_ERROR = -2;
        public static final int NETWORK_UNAVAIBLE = -1;
    }

    public BaseHttpConnector(Context context, String host) {
        HttpAdapter httpAdapter = new HttpAdapter(context);
        httpAdapter.setMaxConnectThreadNum(2);
        this.mHttpAdapter = httpAdapter;
        this.mContext = context.getApplicationContext();
        this.mHost = host;
    }

    public BaseHttpConnector(Context context, String host, HttpAdapter httpAdapter) {
        this.mHttpAdapter = httpAdapter;
        this.mContext = context.getApplicationContext();
        this.mHost = host;
    }

    public void connect(BaseHttpRequest defaultHttpRequest, ConnectListener connectListener, IHttpOperator httpOperator) {
        if (NetUtil.isNetWorkAvailable(this.mContext) || connectListener == null) {
            if (httpOperator != null) {
                defaultHttpRequest.setOperator(httpOperator);
            }
            this.mHttpAdapter.addTask(defaultHttpRequest);
            return;
        }
        connectListener.onFail(-1);
    }

    public void get(String suffixUrl, ConnectListener connectListener) {
        get(this.mHost, suffixUrl, connectListener, sDefaultHttpOperator);
    }

    public void get(String suffixUrl, HashMap<String, String> paramMap, ConnectListener connectListener) {
        get(this.mHost, suffixUrl, paramMap, connectListener, sDefaultHttpOperator);
    }

    public void get(String host, String suffixUrl, ConnectListener connectListener) {
        get(host, suffixUrl, connectListener, sDefaultHttpOperator);
    }

    public void get(String host, String suffixUrl, ConnectListener connectListener, IHttpOperator httpOperator) {
        get(host, suffixUrl, (HashMap<String, String>) null, connectListener, httpOperator);
    }

    public void get(String host, String suffixUrl, HashMap<String, String> paramMap, ConnectListener connectListener, IHttpOperator httpOperator) {
        try {
            GetHttpRequest defaultHttpRequest = new GetHttpRequest(host + suffixUrl, suffixUrl, connectListener);
            if (paramMap != null) {
                defaultHttpRequest.setParamMap(paramMap);
                LogUtils.i("hzw", "paramMap--->" + paramMap.toString());
            }
            LogUtils.i("hzw", "get url--->" + host + suffixUrl);
            connect(defaultHttpRequest, connectListener, httpOperator);
        } catch (URISyntaxException e) {
            e.printStackTrace();
            if (connectListener != null) {
                connectListener.onFail(-3);
            }
        }
    }

    public void post(String suffixUrl, String postData, ConnectListener connectListener) {
        post(this.mHost, suffixUrl, postData, connectListener, sDefaultHttpOperator);
    }

    public void post(String suffixUrl, String postData, HashMap<String, String> paramMap, ConnectListener connectListener) {
        post(this.mHost, suffixUrl, postData, paramMap, connectListener, sDefaultHttpOperator);
    }

    public void post(String host, String suffixUrl, String postData, ConnectListener connectListener) {
        post(host, suffixUrl, postData, connectListener, sDefaultHttpOperator);
    }

    public void post(String host, String suffixUrl, String postData, ConnectListener connectListener, IHttpOperator httpOperator) {
        post(host, suffixUrl, postData, (HashMap<String, String>) null, connectListener, httpOperator);
    }

    public void post(String host, String suffixUrl, String postData, HashMap<String, String> paramMap, ConnectListener connectListener, IHttpOperator httpOperator) {
        try {
            String url = host + suffixUrl;
            LogUtils.i("hzw", "post url--->" + url);
            PostHttpRequest defaultHttpRequest = new PostHttpRequest(postData, url, suffixUrl, connectListener);
            if (paramMap != null) {
                defaultHttpRequest.setParamMap(paramMap);
                LogUtils.i("hzw", "paramMap--->" + paramMap.toString());
            }
            connect(defaultHttpRequest, connectListener, httpOperator);
        } catch (URISyntaxException e) {
            e.printStackTrace();
            if (connectListener != null) {
                connectListener.onFail(-3);
            }
        }
    }

    public static abstract class ConnectListener implements IConnectListener {
        public abstract void onFail(int i);

        public abstract void onSuccess(String str);

        public void onFinish(THttpRequest tHttpRequest, IResponse iResponse) {
            onSuccess(StringUtils.toString(iResponse.getResponse()));
        }

        public void onStart(THttpRequest tHttpRequest) {
        }

        public void onException(THttpRequest tHttpRequest, HttpResponse httpResponse, int i) {
            onFail(i);
        }

        public void onException(THttpRequest tHttpRequest, int i) {
            onException(tHttpRequest, (HttpResponse) null, i);
        }
    }

    private static class GetHttpRequest extends BaseHttpRequest {
        public GetHttpRequest(String url, String urlSuffix, IConnectListener receiver) throws IllegalArgumentException, URISyntaxException {
            super(url, receiver);
            setProtocol(0);
        }
    }

    private static class PostHttpRequest extends BaseHttpRequest {
        public PostHttpRequest(String postData, String url, String urlSuffix, IConnectListener receiver) throws IllegalArgumentException, URISyntaxException {
            super(url, receiver);
            setProtocol(1);
            if (postData != null) {
                setPostData(postData.getBytes());
            }
        }
    }

    private static class BaseHttpRequest extends THttpRequest {
        public BaseHttpRequest(String url, IConnectListener receiver) throws IllegalArgumentException, URISyntaxException {
            super(url, receiver);
            setTimeoutValue(10000);
            setRequestPriority(10);
            setOperator(new AdvertJsonOperator(false, false));
            setCurRetryTime(0);
        }
    }

    private static class DefaultHttpOperator implements IHttpOperator {
        private boolean mIsDecode = false;
        private boolean mIsZipData = false;

        public DefaultHttpOperator() {
        }

        public DefaultHttpOperator(boolean isZipData) {
            this.mIsZipData = isZipData;
        }

        public DefaultHttpOperator(boolean isZipData, boolean isDecode) {
            this.mIsZipData = isZipData;
            this.mIsDecode = isDecode;
        }

        public IResponse operateHttpResponse(THttpRequest request, HttpResponse response) throws IllegalStateException, IOException {
            return new BasicResponse(4, parseData(response.getEntity().getContent(), this.mIsZipData, this.mIsDecode));
        }

        /* JADX WARNING: Code restructure failed: missing block: B:23:0x0035, code lost:
            r8 = move-exception;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:25:?, code lost:
            r8.printStackTrace();
         */
        /* JADX WARNING: Code restructure failed: missing block: B:27:0x003a, code lost:
            if (r12 != null) goto L_0x003c;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:29:?, code lost:
            r12.close();
         */
        /* JADX WARNING: Code restructure failed: missing block: B:31:0x0041, code lost:
            r6 = move-exception;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:32:0x0042, code lost:
            r6.printStackTrace();
         */
        /* JADX WARNING: Code restructure failed: missing block: B:37:0x0056, code lost:
            r10 = move-exception;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:38:0x0057, code lost:
            if (r12 != null) goto L_0x0059;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:40:?, code lost:
            r12.close();
         */
        /* JADX WARNING: Code restructure failed: missing block: B:41:0x005c, code lost:
            throw r10;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:48:0x0069, code lost:
            r6 = move-exception;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:49:0x006a, code lost:
            r6.printStackTrace();
         */
        /* JADX WARNING: Code restructure failed: missing block: B:53:?, code lost:
            return null;
         */
        /* JADX WARNING: Exception block dominator not found, dom blocks: [B:5:0x0008, B:19:0x0029] */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public static java.lang.String parseData(java.io.InputStream r12, boolean r13, boolean r14) {
            /*
                r10 = 0
                if (r12 != 0) goto L_0x0005
                r0 = r10
            L_0x0004:
                return r0
            L_0x0005:
                r0 = 0
                if (r13 == 0) goto L_0x001f
                java.lang.String r0 = com.jb.ga0.commerce.util.zip.ZipUtils.unzip(r12)     // Catch:{ all -> 0x0056 }
            L_0x000c:
                if (r0 == 0) goto L_0x005d
                if (r14 == 0) goto L_0x0014
                java.lang.String r0 = com.jb.ga0.commerce.util.io.StringUtils.encode(r0)     // Catch:{ Exception -> 0x0051 }
            L_0x0014:
                if (r12 == 0) goto L_0x0004
                r12.close()     // Catch:{ IOException -> 0x001a }
                goto L_0x0004
            L_0x001a:
                r6 = move-exception
                r6.printStackTrace()
                goto L_0x0004
            L_0x001f:
                java.io.ByteArrayOutputStream r2 = new java.io.ByteArrayOutputStream     // Catch:{ all -> 0x0056 }
                r2.<init>()     // Catch:{ all -> 0x0056 }
                r11 = 1024(0x400, float:1.435E-42)
                byte[] r3 = new byte[r11]     // Catch:{ all -> 0x0056 }
                r4 = 1
            L_0x0029:
                int r5 = r12.read(r3)     // Catch:{ IOException -> 0x0035 }
                r11 = -1
                if (r5 == r11) goto L_0x0046
                r11 = 0
                r2.write(r3, r11, r5)     // Catch:{ IOException -> 0x0035 }
                goto L_0x0029
            L_0x0035:
                r8 = move-exception
                r8.printStackTrace()     // Catch:{ all -> 0x0056 }
                r9 = 0
                if (r12 == 0) goto L_0x003f
                r12.close()     // Catch:{ IOException -> 0x0041 }
            L_0x003f:
                r0 = r10
                goto L_0x0004
            L_0x0041:
                r6 = move-exception
                r6.printStackTrace()
                goto L_0x003f
            L_0x0046:
                java.lang.String r1 = new java.lang.String     // Catch:{ all -> 0x0056 }
                byte[] r11 = r2.toByteArray()     // Catch:{ all -> 0x0056 }
                r1.<init>(r11)     // Catch:{ all -> 0x0056 }
                r0 = r1
                goto L_0x000c
            L_0x0051:
                r7 = move-exception
                r7.printStackTrace()     // Catch:{ all -> 0x0056 }
                goto L_0x0014
            L_0x0056:
                r10 = move-exception
                if (r12 == 0) goto L_0x005c
                r12.close()     // Catch:{ IOException -> 0x0069 }
            L_0x005c:
                throw r10
            L_0x005d:
                if (r12 == 0) goto L_0x0062
                r12.close()     // Catch:{ IOException -> 0x0064 }
            L_0x0062:
                r0 = r10
                goto L_0x0004
            L_0x0064:
                r6 = move-exception
                r6.printStackTrace()
                goto L_0x0062
            L_0x0069:
                r6 = move-exception
                r6.printStackTrace()
                goto L_0x005c
            */
            throw new UnsupportedOperationException("Method not decompiled: com.jiubang.commerce.statistics.adinfo.http.BaseHttpConnector.DefaultHttpOperator.parseData(java.io.InputStream, boolean, boolean):java.lang.String");
        }
    }
}
