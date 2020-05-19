package com.gau.utils.net.connector;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import com.gau.utils.net.IConnectListener;
import com.gau.utils.net.INetRecord;
import com.gau.utils.net.NetException;
import com.gau.utils.net.asrFiltier.IAsrFilter;
import com.gau.utils.net.operator.IHttpOperator;
import com.gau.utils.net.request.THttpRequest;
import com.gau.utils.net.response.IResponse;
import com.gau.utils.net.util.HeartSetting;
import com.gau.utils.net.util.NetLog;
import com.gau.utils.net.util.NetUtil;
import java.io.IOException;
import java.net.SocketTimeoutException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import org.apache.http.Header;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.conn.ConnectTimeoutException;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.impl.client.DefaultHttpClient;

public class HttpConnectorAlive extends AbstractConnector {
    private static final long ALIVE_LIFT_TIME = 180000;
    /* access modifiers changed from: private */
    public Thread mConnectThread;
    private List<IConnectListener> mConnectorListenerList;
    /* access modifiers changed from: private */
    public IConnectorDestroyListener mDestroyListener;
    /* access modifiers changed from: private */
    public Handler mHandler;
    private IConnectListener mHeartListener;
    private THttpRequest mHeartRequest;
    /* access modifiers changed from: private */
    public Runnable mHeartRunnable = new Runnable() {
        public void run() {
            HttpConnectorAlive.this.addHeartRequest();
            if (HttpConnectorAlive.this.mHandler != null) {
                HttpConnectorAlive.this.mHandler.removeCallbacks(this);
            }
        }
    };
    private HeartSetting mHeartSetting;
    private HttpClient mHttpClient = null;
    /* access modifiers changed from: private */
    public boolean mIsCloseConnect = false;
    /* access modifiers changed from: private */
    public List<THttpRequest> mRequestList;
    private int mSwitchUriTimes = 0;
    /* access modifiers changed from: private */
    public Object mSyncObject = new Object();
    private Object mTag;
    private long mTimeStamp = 0;

    public interface IConnectorDestroyListener {
        void onConnectorDestroy(AbstractConnector abstractConnector);
    }

    public HttpConnectorAlive(THttpRequest httpRequest, Context context, IConnectorDestroyListener destroyListener) {
        super(httpRequest, context);
        this.mDestroyListener = destroyListener;
        init();
    }

    public HttpConnectorAlive(THttpRequest httpRequest, IConnectListener connectReceiver, Context context, IConnectorDestroyListener destroyListener) throws IllegalArgumentException {
        super(httpRequest, connectReceiver, context);
        this.mDestroyListener = destroyListener;
        init();
    }

    public void setHeartSetting(HeartSetting setting) {
        this.mHeartSetting = setting;
    }

    /* JADX WARNING: Code restructure failed: missing block: B:30:?, code lost:
        return;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void addRequest(com.gau.utils.net.request.THttpRequest r9, com.gau.utils.net.IConnectListener r10) {
        /*
            r8 = this;
            java.lang.Object r3 = r8.mSyncObject
            monitor-enter(r3)
            long r0 = java.lang.System.currentTimeMillis()     // Catch:{ all -> 0x007b }
            com.gau.utils.net.request.THttpRequest r2 = r8.mHeartRequest     // Catch:{ all -> 0x007b }
            boolean r2 = r9.equals(r2)     // Catch:{ all -> 0x007b }
            if (r2 != 0) goto L_0x0062
            r8.mTimeStamp = r0     // Catch:{ all -> 0x007b }
            java.util.List<com.gau.utils.net.request.THttpRequest> r2 = r8.mRequestList     // Catch:{ all -> 0x007b }
            com.gau.utils.net.request.THttpRequest r4 = r8.mHeartRequest     // Catch:{ all -> 0x007b }
            boolean r2 = r2.contains(r4)     // Catch:{ all -> 0x007b }
            if (r2 == 0) goto L_0x0022
            java.util.List<com.gau.utils.net.request.THttpRequest> r2 = r8.mRequestList     // Catch:{ all -> 0x007b }
            com.gau.utils.net.request.THttpRequest r4 = r8.mHeartRequest     // Catch:{ all -> 0x007b }
            r2.remove(r4)     // Catch:{ all -> 0x007b }
        L_0x0022:
            android.os.Handler r2 = r8.mHandler     // Catch:{ all -> 0x007b }
            if (r2 == 0) goto L_0x002d
            android.os.Handler r2 = r8.mHandler     // Catch:{ all -> 0x007b }
            java.lang.Runnable r4 = r8.mHeartRunnable     // Catch:{ all -> 0x007b }
            r2.removeCallbacks(r4)     // Catch:{ all -> 0x007b }
        L_0x002d:
            java.util.List<com.gau.utils.net.request.THttpRequest> r2 = r8.mRequestList     // Catch:{ all -> 0x007b }
            r2.add(r9)     // Catch:{ all -> 0x007b }
            java.util.List<com.gau.utils.net.request.THttpRequest> r2 = r8.mRequestList     // Catch:{ all -> 0x007b }
            com.gau.utils.net.connector.HttpConnectorAlive$1 r4 = new com.gau.utils.net.connector.HttpConnectorAlive$1     // Catch:{ all -> 0x007b }
            r4.<init>()     // Catch:{ all -> 0x007b }
            java.util.Collections.sort(r2, r4)     // Catch:{ all -> 0x007b }
            java.util.List<com.gau.utils.net.IConnectListener> r2 = r8.mConnectorListenerList     // Catch:{ all -> 0x007b }
            java.util.List<com.gau.utils.net.request.THttpRequest> r4 = r8.mRequestList     // Catch:{ all -> 0x007b }
            int r4 = r4.indexOf(r9)     // Catch:{ all -> 0x007b }
            r2.add(r4, r10)     // Catch:{ all -> 0x007b }
            boolean r2 = r8.mIsCloseConnect     // Catch:{ all -> 0x007b }
            if (r2 == 0) goto L_0x004e
            r2 = 0
            r8.mIsCloseConnect = r2     // Catch:{ all -> 0x007b }
        L_0x004e:
            com.gau.utils.net.request.THttpRequest r2 = r8.mHeartRequest     // Catch:{ all -> 0x007b }
            if (r2 == 0) goto L_0x005b
            com.gau.utils.net.request.THttpRequest r2 = r8.mHeartRequest     // Catch:{ all -> 0x007b }
            long r4 = r9.getHeartTime()     // Catch:{ all -> 0x007b }
            r2.setHeartTime(r4)     // Catch:{ all -> 0x007b }
        L_0x005b:
            java.lang.Object r2 = r8.mSyncObject     // Catch:{ all -> 0x007b }
            r2.notifyAll()     // Catch:{ all -> 0x007b }
            monitor-exit(r3)     // Catch:{ all -> 0x007b }
        L_0x0061:
            return
        L_0x0062:
            long r4 = r8.mTimeStamp     // Catch:{ all -> 0x007b }
            long r4 = r0 - r4
            r6 = 180000(0x2bf20, double:8.8932E-319)
            int r2 = (r4 > r6 ? 1 : (r4 == r6 ? 0 : -1))
            if (r2 <= 0) goto L_0x002d
            r8.closeConnect()     // Catch:{ all -> 0x007b }
            com.gau.utils.net.connector.HttpConnectorAlive$IConnectorDestroyListener r2 = r8.mDestroyListener     // Catch:{ all -> 0x007b }
            if (r2 == 0) goto L_0x0079
            com.gau.utils.net.connector.HttpConnectorAlive$IConnectorDestroyListener r2 = r8.mDestroyListener     // Catch:{ all -> 0x007b }
            r2.onConnectorDestroy(r8)     // Catch:{ all -> 0x007b }
        L_0x0079:
            monitor-exit(r3)     // Catch:{ all -> 0x007b }
            goto L_0x0061
        L_0x007b:
            r2 = move-exception
            monitor-exit(r3)     // Catch:{ all -> 0x007b }
            throw r2
        */
        throw new UnsupportedOperationException("Method not decompiled: com.gau.utils.net.connector.HttpConnectorAlive.addRequest(com.gau.utils.net.request.THttpRequest, com.gau.utils.net.IConnectListener):void");
    }

    public List<THttpRequest> getRequestList() {
        return this.mRequestList;
    }

    public int getRequestSize() {
        if (this.mRequestList != null) {
            return this.mRequestList.size();
        }
        return 0;
    }

    public void setTag(Object object) {
        this.mTag = object;
    }

    public Object getTag() {
        return this.mTag;
    }

    public long getIdleTime() {
        return System.currentTimeMillis() - this.mTimeStamp;
    }

    public void removeRequest(THttpRequest httpRequest, IConnectListener connectReceiver) {
        synchronized (this.mSyncObject) {
            if (this.mRequestList != null && this.mRequestList.size() > 0) {
                this.mRequestList.remove(httpRequest);
            }
            if (this.mConnectorListenerList != null && this.mConnectorListenerList.size() > 0) {
                this.mConnectorListenerList.remove(connectReceiver);
            }
            this.mSyncObject.notifyAll();
        }
    }

    private void init() {
        this.mTimeStamp = System.currentTimeMillis();
        this.mHttpClient = new DefaultHttpClient();
        this.mRequestList = new ArrayList();
        this.mConnectorListenerList = new ArrayList();
        addRequest(this.mRequest, this.mConnectObserver);
        initHeartListener();
        initHeartRequest(this.mRequest);
        configClient(this.mRequest, this.mHttpClient);
    }

    private void initHeartListener() {
        if (this.mHeartListener == null) {
            this.mHeartListener = new IConnectListener() {
                public void onStart(THttpRequest request) {
                }

                public void onFinish(THttpRequest request, IResponse response) {
                }

                public void onException(THttpRequest request, int reason) {
                    Log.i("ABEN", "HttpConnectorAlive initHeartListener onException reason = " + reason);
                    HttpConnectorAlive.this.closeConnect();
                    if (HttpConnectorAlive.this.mDestroyListener != null) {
                        HttpConnectorAlive.this.mDestroyListener.onConnectorDestroy(HttpConnectorAlive.this);
                    }
                }

                public void onException(THttpRequest request, HttpResponse response, int reason) {
                    onException(request, reason);
                }
            };
        }
    }

    private String selectHeartUrl(THttpRequest request) {
        String heartUrl = null;
        if (this.mRequest.getHeartUrl() != null) {
            heartUrl = this.mRequest.getHeartUrl().toString();
        }
        if (!(heartUrl != null || this.mHeartSetting == null || this.mRequest.getCurrentUrl() == null)) {
            heartUrl = this.mHeartSetting.getHeartUrl(this.mRequest.getCurrentUrl().getHost());
        }
        if (heartUrl == null) {
            return this.mRequest.getUrl().toString();
        }
        return heartUrl;
    }

    /* access modifiers changed from: private */
    public void addHeartRequest() {
        if (!this.mRequestList.contains(this.mHeartRequest)) {
            String heartUrl = selectHeartUrl(this.mRequest);
            if (heartUrl != null) {
                try {
                    this.mHeartRequest.setUrl(heartUrl);
                } catch (URISyntaxException e) {
                    e.printStackTrace();
                }
            }
            addRequest(this.mHeartRequest, this.mHeartListener);
        }
    }

    private void initHeartRequest(THttpRequest request) {
        if (this.mHeartRequest == null) {
            try {
                this.mHeartRequest = new THttpRequest(selectHeartUrl(request), (byte[]) null, this.mHeartListener);
                this.mHeartRequest.setOperator(new IHttpOperator() {
                    public IResponse operateHttpResponse(THttpRequest request, HttpResponse response) throws IllegalStateException, IOException {
                        return null;
                    }
                });
                this.mHeartRequest.setNetRecord(new INetRecord() {
                    public void onTransFinish(THttpRequest request, Object arg1, Object arg2) {
                    }

                    public void onStartConnect(THttpRequest request, Object arg1, Object arg2) {
                    }

                    public void onException(Exception e, Object arg1, Object arg2) {
                    }

                    public void onConnectSuccess(THttpRequest request, Object arg1, Object arg2) {
                    }
                });
                this.mHeartRequest.setHeartTime(request.getHeartTime());
                this.mHeartRequest.setRequestPriority(1);
            } catch (Exception e) {
            }
        }
    }

    private void configClient(THttpRequest request, HttpClient client) {
        if (request != null && client != null) {
            if (2 == NetUtil.getNetWorkType(this.mContext)) {
                client.getParams().setParameter("http.route.default-proxy", NetUtil.getProxy(this.mContext));
            }
            client.getParams().setParameter("http.socket.timeout", Integer.valueOf(request.getSocketTimeoutValue()));
            client.getParams().setParameter("http.connection.timeout", Integer.valueOf(request.getTimeoutValue()));
        }
    }

    private void setConnect() {
        NetLog.info("testBattery, Begin HttpConnector run", (Throwable) null);
        try {
            this.mRequest.setCurrentUrl(this.mRequest.getUrl());
            this.mConnectObserver.onStart(this.mRequest);
            this.mConnectObserver.onFinish(this.mRequest, connectSynchronous(this.mRequest));
        } catch (ClientProtocolException e) {
            NetLog.erro("ClientProtocolException", e);
            e.printStackTrace();
            this.mConnectObserver.onException(this.mRequest, 0);
        } catch (NetException e2) {
            NetLog.erro("IOException", e2);
            e2.printStackTrace();
            this.mConnectObserver.onException(this.mRequest, e2.mErrorCode);
        } catch (IOException e3) {
            NetLog.erro("IOException", e3);
            e3.printStackTrace();
            this.mConnectObserver.onException(this.mRequest, 1);
        } catch (IllegalAccessException e4) {
            NetLog.erro("can't find netWork", e4);
            e4.printStackTrace();
            this.mConnectObserver.onException(this.mRequest, 2);
        } catch (OutOfMemoryError e5) {
            e5.printStackTrace();
            NetLog.erro("unkown exception ", e5);
            System.gc();
            this.mConnectObserver.onException(this.mRequest, 4);
        } catch (Throwable e6) {
            e6.printStackTrace();
            NetLog.erro("unkown exception ", e6);
            this.mConnectObserver.onException(this.mRequest, 5);
        }
        NetLog.info("testBattery, Begin HttpConnector run", (Throwable) null);
    }

    public void connect() {
    }

    /* access modifiers changed from: private */
    public void handleConnect() {
        if (!this.mRequestList.isEmpty() && !this.mConnectorListenerList.isEmpty()) {
            this.mRequest = this.mRequestList.remove(0);
            this.mConnectObserver = this.mConnectorListenerList.remove(0);
            if (this.mRequest != null && this.mConnectObserver != null) {
                if (!this.mRequest.equals(this.mHeartRequest)) {
                    this.mIsIdle = false;
                } else {
                    this.mIsIdle = true;
                }
                setConnect();
                this.mIsIdle = true;
            }
        }
    }

    public void connectAsynchronous() {
        if (this.mHandler == null) {
            this.mHandler = new Handler(Looper.getMainLooper());
        }
        if (this.mConnectThread == null) {
            this.mConnectThread = new Thread(new Runnable() {
                public void run() {
                    while (!HttpConnectorAlive.this.mIsCloseConnect) {
                        synchronized (HttpConnectorAlive.this.mSyncObject) {
                            if (HttpConnectorAlive.this.mRequestList.isEmpty()) {
                                HttpConnectorAlive.this.mHandler.postDelayed(HttpConnectorAlive.this.mHeartRunnable, HttpConnectorAlive.this.selectHeartTime());
                                try {
                                    HttpConnectorAlive.this.mSyncObject.wait();
                                } catch (InterruptedException e) {
                                }
                            }
                        }
                        HttpConnectorAlive.this.handleConnect();
                    }
                    Thread unused = HttpConnectorAlive.this.mConnectThread = null;
                }
            }, "AliveConnectorConnectAsynchronous");
            this.mConnectThread.start();
        }
    }

    /* access modifiers changed from: private */
    public long selectHeartTime() {
        long heartTime = this.mRequest.getHeartTime();
        if (heartTime == -1) {
            heartTime = this.mHeartSetting.getCommonHeartTime();
        }
        if (heartTime == -1) {
            return HeartSetting.DEFAULT_HEART_TIME_INTERVAL;
        }
        return heartTime;
    }

    public void closeConnect() {
        if (this.mHttpClient != null) {
            try {
                this.mHttpClient.getConnectionManager().shutdown();
            } catch (Exception e) {
                e.printStackTrace();
            }
            this.mHttpClient = null;
        }
        if (this.mHandler != null) {
            this.mHandler.removeCallbacks(this.mHeartRunnable);
        }
        this.mIsCloseConnect = true;
    }

    public void cancelCurrentConnect() {
    }

    private IResponse connectSynchronous(THttpRequest request) throws ClientProtocolException, IOException, IllegalAccessException, Exception {
        IResponse ret;
        IResponse ret2;
        HttpResponse response;
        NetLog.info("StartConnect url= " + request.getUrl(), (Throwable) null);
        NetLog.info("testBattery, Begin HttpConnector connectSynchronous url = " + request.getUrl(), (Throwable) null);
        INetRecord record = request.getNetRecord();
        try {
            URI uri = selectURI(request);
            if (uri == null) {
                throw new NetException(6);
            }
            request.setCurrentUrl(uri);
            HttpHost httphost = new HttpHost(uri.getHost(), uri.getPort());
            if (this.mHttpClient == null) {
                this.mHttpClient = new DefaultHttpClient();
            }
            configClient(request, this.mHttpClient);
            if (request.getPostData() == null) {
                HttpGet get = new HttpGet(uri);
                configHttpHeader(get);
                configAliveHeader(get);
                if (record != null) {
                    record.onStartConnect(request, (Object) null, (Object) null);
                }
                response = this.mHttpClient.execute(httphost, get);
                if (record != null) {
                    record.onConnectSuccess(request, (Object) null, (Object) null);
                }
            } else {
                HttpPost httppost = new HttpPost(uri);
                configHttpHeader(httppost);
                configAliveHeader(httppost);
                ByteArrayEntity reqEntity = new ByteArrayEntity(request.getPostData());
                reqEntity.setChunked(false);
                httppost.setEntity(reqEntity);
                if (record != null) {
                    record.onStartConnect(request, (Object) null, (Object) null);
                }
                response = this.mHttpClient.execute(httphost, httppost);
                if (record != null) {
                    record.onConnectSuccess(request, (Object) null, (Object) null);
                }
            }
            int responseCode = response.getStatusLine().getStatusCode();
            if (responseCode == 200) {
                IAsrFilter filter = request.getAsrFilter();
                if (filter == null || !filter.isAsrResponse(response)) {
                    IResponse operateHttpResponse = request.getOperator().operateHttpResponse(request, response);
                    response.getEntity().consumeContent();
                    if (record != null) {
                        record.onTransFinish(request, (Object) null, (Object) null);
                    }
                    this.mSwitchUriTimes = 0;
                    return operateHttpResponse;
                }
                NetLog.debug("find AsrResponse", (Throwable) null);
                int retryTime = request.getCurRetryTime();
                if (retryTime > 0) {
                    request.setCurRetryTime(retryTime - 1);
                    ret = connectSynchronous(request);
                } else {
                    this.mSwitchUriTimes++;
                    if (this.mSwitchUriTimes < request.getAllUrl().size()) {
                        ret = connectSynchronous(request);
                    } else {
                        throw new NetException(10);
                    }
                }
            } else {
                if (responseCode == 503) {
                    request.setCurRetryTime(0);
                }
                int retryTime2 = request.getCurRetryTime();
                if (retryTime2 > 0) {
                    request.setCurRetryTime(retryTime2 - 1);
                    ret = connectSynchronous(request);
                } else {
                    this.mSwitchUriTimes++;
                    if (this.mSwitchUriTimes < request.getAllUrl().size()) {
                        ret = connectSynchronous(request);
                    } else {
                        throw new NetException(responseCode);
                    }
                }
            }
            NetLog.info("testBattery, end HttpConnector connectSynchronous url = " + request.getUrl(), (Throwable) null);
            IResponse iResponse = ret;
            return ret;
        } catch (Exception e) {
            if (record != null) {
                record.onException(e, (Object) null, (Object) null);
            }
            int retryTime3 = request.getCurRetryTime();
            if (retryTime3 > 0) {
                request.setCurRetryTime(retryTime3 - 1);
                ret2 = connectSynchronous(request);
            } else {
                this.mSwitchUriTimes++;
                if (this.mSwitchUriTimes < request.getAllUrl().size()) {
                    ret2 = connectSynchronous(request);
                } else if (e instanceof SocketTimeoutException) {
                    throw new NetException(11);
                } else if (e instanceof ConnectTimeoutException) {
                    throw new NetException(12);
                } else {
                    throw e;
                }
            }
        } finally {
            this.mSwitchUriTimes = 0;
        }
    }

    private void configHttpHeader(HttpRequestBase httpRequest) {
        List<Header> list = this.mRequest.getHeader();
        if (list != null) {
            int listSize = list.size();
            for (int i = 0; i < listSize; i++) {
                httpRequest.addHeader(list.get(i));
            }
        }
    }

    private void configAliveHeader(HttpRequestBase httpRequest) {
        httpRequest.addHeader("Connection", "keep-alive");
    }

    private URI selectURI(THttpRequest request) {
        if (request != null && this.mSwitchUriTimes == 0 && request.getAllUrl() != null && this.mSwitchUriTimes < request.getAllUrl().size()) {
            return request.getAllUrl().get(this.mSwitchUriTimes);
        }
        return null;
    }
}
