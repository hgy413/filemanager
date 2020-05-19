package com.gau.utils.net;

import android.content.Context;
import android.os.Build;
import com.gau.utils.net.connector.AbstractConnector;
import com.gau.utils.net.connector.ConnectorFactory;
import com.gau.utils.net.request.THttpRequest;
import com.gau.utils.net.response.IResponse;
import com.gau.utils.net.util.NetLog;
import java.net.URI;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.apache.http.HttpResponse;

public class HttpConnectScheduler implements IConnectListener {
    private ConnectorFactory mConnectorFactory;
    private Context mContext;
    private List<THttpRequest> mCurrentRequestlist = new ArrayList();
    private byte[] mLock = new byte[0];
    private int mMaxConnectThreadNum = 2;
    private List<THttpRequest> mWaitRequestList = new ArrayList();

    public HttpConnectScheduler(Context context) throws IllegalArgumentException {
        if (context == null) {
            throw new IllegalArgumentException("context must have value");
        }
        this.mContext = context;
        this.mConnectorFactory = new ConnectorFactory();
    }

    public void addRequest(THttpRequest request) {
        if (request == null) {
            throw new IllegalArgumentException("Addrequest must have request");
        }
        synchronized (this.mLock) {
            if (request.getIsAsync()) {
                this.mWaitRequestList.add(request);
                if (Build.VERSION.SDK_INT >= 9) {
                    Collections.sort(this.mWaitRequestList, new Comparator<THttpRequest>() {
                        public int compare(THttpRequest object1, THttpRequest object2) {
                            if (object1.getRequestPriority() < object2.getRequestPriority()) {
                                return 1;
                            }
                            if (object1.getRequestPriority() > object2.getRequestPriority()) {
                                return -1;
                            }
                            return 0;
                        }
                    });
                }
                connect();
            } else {
                connect(request);
            }
        }
    }

    public void cleanup() {
        if (this.mConnectorFactory != null) {
            this.mConnectorFactory.cleanup();
        }
    }

    public int getMaxConnectThreadNum() {
        return this.mMaxConnectThreadNum;
    }

    public void setMaxConnectThreadNum(int maxConnectThreadNum) {
        this.mMaxConnectThreadNum = maxConnectThreadNum;
    }

    public String putCommonHeartUrl(String host, String heartUrl) {
        if (this.mConnectorFactory != null) {
            return this.mConnectorFactory.putCommonHeartUrl(host, heartUrl);
        }
        return null;
    }

    public String removeHeartUrl(String host) {
        if (this.mConnectorFactory != null) {
            return this.mConnectorFactory.removeHeartUrl(host);
        }
        return null;
    }

    public String getHeartUrl(String host) {
        if (this.mConnectorFactory != null) {
            return this.mConnectorFactory.getHeartUrl(host);
        }
        return null;
    }

    public Map<String, String> getAllCommonHeartUrl() {
        if (this.mConnectorFactory != null) {
            return this.mConnectorFactory.getAllHeartUrl();
        }
        return null;
    }

    public void setCommonHeartTime(long heartTime) {
        if (this.mConnectorFactory != null) {
            this.mConnectorFactory.setCommonHeartTime(heartTime);
        }
    }

    public long getCommonHeartTime() {
        if (this.mConnectorFactory != null) {
            return this.mConnectorFactory.getCommonHeartTime();
        }
        return -1;
    }

    public THttpRequest isExistEqualRequest(String url, List<THttpRequest> requestQuene) {
        THttpRequest result = null;
        if (url == null || requestQuene == null || requestQuene.size() <= 0) {
            return null;
        }
        Iterator i$ = requestQuene.iterator();
        while (true) {
            if (i$.hasNext()) {
                THttpRequest httpRequest = i$.next();
                URI uri = httpRequest.getUrl();
                if (uri != null && uri.toString() != null && uri.toString().equals(url)) {
                    result = httpRequest;
                    break;
                }
            } else {
                break;
            }
        }
        THttpRequest tHttpRequest = result;
        return result;
    }

    private THttpRequest isExistRequests(String url, List<THttpRequest> requestList) {
        URI uri;
        THttpRequest ret = null;
        if (url == null || requestList == null || requestList.isEmpty()) {
            return null;
        }
        Iterator i$ = requestList.iterator();
        while (true) {
            if (i$.hasNext()) {
                THttpRequest request = i$.next();
                if (request != null && (uri = request.getUrl()) != null && uri.toString() != null && uri.toString().equals(url)) {
                    ret = request;
                    break;
                }
            } else {
                break;
            }
        }
        THttpRequest tHttpRequest = ret;
        return ret;
    }

    /* JADX WARNING: Code restructure failed: missing block: B:35:?, code lost:
        return false;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:36:?, code lost:
        return r2;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public boolean cancelRequest(java.lang.String r7) {
        /*
            r6 = this;
            r5 = 1
            r3 = 0
            if (r7 == 0) goto L_0x000a
            int r4 = r7.length()
            if (r4 >= r5) goto L_0x000c
        L_0x000a:
            r2 = r3
        L_0x000b:
            return r2
        L_0x000c:
            r2 = 0
            byte[] r4 = r6.mLock
            monitor-enter(r4)
            java.util.List<com.gau.utils.net.request.THttpRequest> r5 = r6.mWaitRequestList     // Catch:{ all -> 0x005e }
            if (r5 == 0) goto L_0x002a
            java.util.List<com.gau.utils.net.request.THttpRequest> r5 = r6.mWaitRequestList     // Catch:{ all -> 0x005e }
            boolean r5 = r5.isEmpty()     // Catch:{ all -> 0x005e }
            if (r5 != 0) goto L_0x002a
            java.util.List<com.gau.utils.net.request.THttpRequest> r5 = r6.mWaitRequestList     // Catch:{ all -> 0x005e }
            com.gau.utils.net.request.THttpRequest r1 = r6.isExistRequests(r7, r5)     // Catch:{ all -> 0x005e }
            if (r1 == 0) goto L_0x002a
            java.util.List<com.gau.utils.net.request.THttpRequest> r5 = r6.mWaitRequestList     // Catch:{ all -> 0x005e }
            boolean r2 = r5.remove(r1)     // Catch:{ all -> 0x005e }
        L_0x002a:
            if (r2 != 0) goto L_0x005c
            java.util.List<com.gau.utils.net.request.THttpRequest> r5 = r6.mCurrentRequestlist     // Catch:{ all -> 0x005e }
            if (r5 == 0) goto L_0x005c
            java.util.List<com.gau.utils.net.request.THttpRequest> r5 = r6.mCurrentRequestlist     // Catch:{ all -> 0x005e }
            boolean r5 = r5.isEmpty()     // Catch:{ all -> 0x005e }
            if (r5 != 0) goto L_0x005c
            java.util.List<com.gau.utils.net.request.THttpRequest> r5 = r6.mCurrentRequestlist     // Catch:{ all -> 0x005e }
            com.gau.utils.net.request.THttpRequest r1 = r6.isExistRequests(r7, r5)     // Catch:{ all -> 0x005e }
            if (r1 == 0) goto L_0x0044
            com.gau.utils.net.connector.ConnectorFactory r5 = r6.mConnectorFactory     // Catch:{ all -> 0x005e }
            if (r5 != 0) goto L_0x0047
        L_0x0044:
            monitor-exit(r4)     // Catch:{ all -> 0x005e }
            r2 = r3
            goto L_0x000b
        L_0x0047:
            r3 = 1
            r1.setCanceled(r3)     // Catch:{ all -> 0x005e }
            com.gau.utils.net.connector.ConnectorFactory r3 = r6.mConnectorFactory     // Catch:{ all -> 0x005e }
            com.gau.utils.net.connector.AbstractConnector r0 = r3.isExistConnector(r1)     // Catch:{ all -> 0x005e }
            if (r0 == 0) goto L_0x005c
            r0.cancelCurrentConnect()     // Catch:{ all -> 0x005e }
            com.gau.utils.net.connector.ConnectorFactory r3 = r6.mConnectorFactory     // Catch:{ all -> 0x005e }
            r3.removeConnectorByRequest(r1)     // Catch:{ all -> 0x005e }
            r2 = 1
        L_0x005c:
            monitor-exit(r4)     // Catch:{ all -> 0x005e }
            goto L_0x000b
        L_0x005e:
            r3 = move-exception
            monitor-exit(r4)     // Catch:{ all -> 0x005e }
            throw r3
        */
        throw new UnsupportedOperationException("Method not decompiled: com.gau.utils.net.HttpConnectScheduler.cancelRequest(java.lang.String):boolean");
    }

    public boolean cancelRequest(THttpRequest request) {
        if (request == null) {
            return false;
        }
        boolean ret = false;
        synchronized (this.mLock) {
            if (this.mWaitRequestList != null && !this.mWaitRequestList.isEmpty()) {
                ret = this.mWaitRequestList.remove(request);
            }
            if (!ret && this.mCurrentRequestlist != null && !this.mCurrentRequestlist.isEmpty()) {
                request.setCanceled(true);
                AbstractConnector connector = this.mConnectorFactory.isExistConnector(request);
                if (connector != null) {
                    connector.cancelCurrentConnect();
                    this.mConnectorFactory.removeConnectorByRequest(request);
                    ret = true;
                }
            }
        }
        return ret;
    }

    public void onFinish(THttpRequest request, IResponse response) {
        NetLog.info("schedule onFinish", (Throwable) null);
        NetLog.info("testBattery, Begin HttpConnectScheduler onFinish", (Throwable) null);
        if (request != null) {
            request.getReceiver().onFinish(request, response);
            synchronized (this.mLock) {
                if (this.mCurrentRequestlist != null && !this.mCurrentRequestlist.isEmpty()) {
                    this.mCurrentRequestlist.remove(request);
                    if (this.mConnectorFactory != null) {
                        this.mConnectorFactory.removeConnectorByRequest(request);
                    }
                }
            }
            tick();
            NetLog.info("testBattery, End HttpConnectScheduler onFinish", (Throwable) null);
        }
    }

    public void onStart(THttpRequest request) {
        request.getReceiver().onStart(request);
    }

    public void onException(THttpRequest request, int reason) {
        NetLog.info("schedule onException", (Throwable) null);
        NetLog.info("testBattery, Begin HttpConnectScheduler onException", (Throwable) null);
        if (request != null) {
            IConnectListener receiver = request.getReceiver();
            if (receiver != null) {
                receiver.onException(request, reason);
            }
            synchronized (this.mLock) {
                if (this.mCurrentRequestlist != null && !this.mCurrentRequestlist.isEmpty()) {
                    this.mCurrentRequestlist.remove(request);
                    if (this.mConnectorFactory != null) {
                        this.mConnectorFactory.removeConnectorByRequest(request);
                    }
                }
            }
            tick();
            NetLog.info("testBattery, End HttpConnectScheduler onException", (Throwable) null);
        }
    }

    private void tick() {
        synchronized (this.mLock) {
            NetLog.info("testBattery, Begin HttpConnectScheduler tick", (Throwable) null);
            connect();
            NetLog.info("testBattery, end HttpConnectScheduler tick", (Throwable) null);
        }
    }

    private void connect() {
        int currentSize;
        if (this.mWaitRequestList != null && !this.mWaitRequestList.isEmpty() && this.mCurrentRequestlist != null && (currentSize = this.mCurrentRequestlist.size()) < this.mMaxConnectThreadNum) {
            for (int i = 0; i < this.mMaxConnectThreadNum - currentSize && !this.mWaitRequestList.isEmpty(); i++) {
            }
            THttpRequest request = this.mWaitRequestList.remove(0);
            if (request != null) {
                this.mCurrentRequestlist.add(request);
                connect(request);
            }
        }
    }

    private void connect(THttpRequest request) {
        AbstractConnector connector = this.mConnectorFactory.productConnector(request, this, this.mContext);
        if (connector != null) {
            if (connector.getRequset().getIsAsync()) {
                connector.connectAsynchronous();
            } else {
                connector.connect();
            }
        }
    }

    public void onException(THttpRequest request, HttpResponse response, int reason) {
        onException(request, reason);
    }
}
