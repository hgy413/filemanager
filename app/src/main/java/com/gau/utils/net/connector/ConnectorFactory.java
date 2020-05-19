package com.gau.utils.net.connector;

import android.content.Context;
import com.gau.utils.net.IConnectListener;
import com.gau.utils.net.connector.HttpConnectorAlive;
import com.gau.utils.net.request.THttpRequest;
import com.gau.utils.net.util.HeartSetting;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ConnectorFactory implements HttpConnectorAlive.IConnectorDestroyListener {
    private static final int DEFAULT_MAX_ALIVE_CONNECTOR_NUM = 2;
    private Map<String, HttpConnectorAlive> mAliveConnectorMap;
    private HeartSetting mHeartSetting = new HeartSetting();
    private Map<THttpRequest, HttpConnector> mHttpConnectorMap;
    private int mMaxAliveConnectorNum = 2;

    public AbstractConnector productConnector(THttpRequest request, IConnectListener connectReceiver, Context context) {
        AbstractConnector connector;
        if (!request.getIsKeepAlive()) {
            return productHttpConnector(request, connectReceiver, context);
        }
        request.setIsAsync(true);
        synchronized (this) {
            connector = productAliveConnector(request, connectReceiver, context);
        }
        return connector;
    }

    private AbstractConnector productHttpConnector(THttpRequest request, IConnectListener connectReceiver, Context context) {
        if (this.mHttpConnectorMap == null) {
            this.mHttpConnectorMap = new ConcurrentHashMap();
        }
        HttpConnector httpConnector = new HttpConnector(request, connectReceiver, context);
        this.mHttpConnectorMap.put(request, httpConnector);
        return httpConnector;
    }

    /* JADX WARNING: Code restructure failed: missing block: B:33:0x00cb, code lost:
        r4 = r4;
     */
    /* JADX WARNING: Multi-variable type inference failed */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private com.gau.utils.net.connector.AbstractConnector productAliveConnector(com.gau.utils.net.request.THttpRequest r19, com.gau.utils.net.IConnectListener r20, android.content.Context r21) {
        /*
            r18 = this;
            r4 = 0
            r0 = r18
            java.util.Map<java.lang.String, com.gau.utils.net.connector.HttpConnectorAlive> r0 = r0.mAliveConnectorMap
            r16 = r0
            if (r16 != 0) goto L_0x0014
            java.util.concurrent.ConcurrentHashMap r16 = new java.util.concurrent.ConcurrentHashMap
            r16.<init>()
            r0 = r16
            r1 = r18
            r1.mAliveConnectorMap = r0
        L_0x0014:
            java.net.URI r16 = r19.getUrl()
            java.lang.String r7 = r16.getHost()
            r0 = r18
            java.util.Map<java.lang.String, com.gau.utils.net.connector.HttpConnectorAlive> r0 = r0.mAliveConnectorMap
            r16 = r0
            r0 = r16
            boolean r16 = r0.containsKey(r7)
            if (r16 == 0) goto L_0x0070
            r0 = r18
            java.util.Map<java.lang.String, com.gau.utils.net.connector.HttpConnectorAlive> r0 = r0.mAliveConnectorMap
            r16 = r0
            r0 = r16
            java.lang.Object r15 = r0.get(r7)
            com.gau.utils.net.connector.HttpConnectorAlive r15 = (com.gau.utils.net.connector.HttpConnectorAlive) r15
            boolean r16 = r19.getIsMustAlive()
            if (r16 == 0) goto L_0x0053
            r16 = 1
            r0 = r19
            r1 = r16
            r0.setIsKeepAlive(r1)
            r0 = r19
            r1 = r20
            r15.addRequest(r0, r1)
            r4 = r15
        L_0x004f:
            r5 = r4
            r16 = r4
        L_0x0052:
            return r16
        L_0x0053:
            boolean r16 = r15.isIdle()
            if (r16 == 0) goto L_0x0062
            r0 = r19
            r1 = r20
            r15.addRequest(r0, r1)
            r4 = r15
            goto L_0x004f
        L_0x0062:
            r16 = 0
            r0 = r19
            r1 = r16
            r0.setIsKeepAlive(r1)
            com.gau.utils.net.connector.AbstractConnector r4 = r18.productHttpConnector(r19, r20, r21)
            goto L_0x004f
        L_0x0070:
            int r14 = r18.getAliveConnectorSize()
            r0 = r18
            int r0 = r0.mMaxAliveConnectorNum
            r16 = r0
            r0 = r16
            if (r14 >= r0) goto L_0x00b3
            com.gau.utils.net.connector.HttpConnectorAlive r4 = new com.gau.utils.net.connector.HttpConnectorAlive
            r0 = r19
            r1 = r20
            r2 = r21
            r3 = r18
            r4.<init>(r0, r1, r2, r3)
            r16 = r4
            com.gau.utils.net.connector.HttpConnectorAlive r16 = (com.gau.utils.net.connector.HttpConnectorAlive) r16
            r0 = r18
            com.gau.utils.net.util.HeartSetting r0 = r0.mHeartSetting
            r17 = r0
            r16.setHeartSetting(r17)
            r16 = r4
            com.gau.utils.net.connector.HttpConnectorAlive r16 = (com.gau.utils.net.connector.HttpConnectorAlive) r16
            r0 = r16
            r0.setTag(r7)
            r0 = r18
            java.util.Map<java.lang.String, com.gau.utils.net.connector.HttpConnectorAlive> r0 = r0.mAliveConnectorMap
            r17 = r0
            r16 = r4
            com.gau.utils.net.connector.HttpConnectorAlive r16 = (com.gau.utils.net.connector.HttpConnectorAlive) r16
            r0 = r17
            r1 = r16
            r0.put(r7, r1)
            goto L_0x004f
        L_0x00b3:
            r0 = r18
            java.util.Map<java.lang.String, com.gau.utils.net.connector.HttpConnectorAlive> r0 = r0.mAliveConnectorMap
            r16 = r0
            java.util.Set r16 = r16.entrySet()
            java.util.Iterator r10 = r16.iterator()
            java.lang.String r11 = ""
            if (r10 != 0) goto L_0x00c9
            r16 = 0
            r5 = r4
            goto L_0x0052
        L_0x00c9:
            r12 = 0
        L_0x00cb:
            boolean r16 = r10.hasNext()
            if (r16 == 0) goto L_0x00f2
            java.lang.Object r6 = r10.next()
            java.util.Map$Entry r6 = (java.util.Map.Entry) r6
            if (r6 == 0) goto L_0x00cb
            java.lang.Object r15 = r6.getValue()
            com.gau.utils.net.connector.HttpConnectorAlive r15 = (com.gau.utils.net.connector.HttpConnectorAlive) r15
            if (r15 == 0) goto L_0x00cb
            long r8 = r15.getIdleTime()
            int r16 = (r8 > r12 ? 1 : (r8 == r12 ? 0 : -1))
            if (r16 <= 0) goto L_0x00cb
            r12 = r8
            java.lang.Object r11 = r6.getKey()
            java.lang.String r11 = (java.lang.String) r11
            r4 = r15
            goto L_0x00cb
        L_0x00f2:
            r16 = r4
            com.gau.utils.net.connector.HttpConnectorAlive r16 = (com.gau.utils.net.connector.HttpConnectorAlive) r16
            r0 = r16
            r1 = r19
            r2 = r20
            r0.addRequest(r1, r2)
            r16 = r4
            com.gau.utils.net.connector.HttpConnectorAlive r16 = (com.gau.utils.net.connector.HttpConnectorAlive) r16
            r0 = r16
            r0.setTag(r7)
            r0 = r18
            java.util.Map<java.lang.String, com.gau.utils.net.connector.HttpConnectorAlive> r0 = r0.mAliveConnectorMap
            r16 = r0
            r0 = r16
            r0.remove(r11)
            r0 = r18
            java.util.Map<java.lang.String, com.gau.utils.net.connector.HttpConnectorAlive> r0 = r0.mAliveConnectorMap
            r17 = r0
            r16 = r4
            com.gau.utils.net.connector.HttpConnectorAlive r16 = (com.gau.utils.net.connector.HttpConnectorAlive) r16
            r0 = r17
            r1 = r16
            r0.put(r7, r1)
            goto L_0x004f
        */
        throw new UnsupportedOperationException("Method not decompiled: com.gau.utils.net.connector.ConnectorFactory.productAliveConnector(com.gau.utils.net.request.THttpRequest, com.gau.utils.net.IConnectListener, android.content.Context):com.gau.utils.net.connector.AbstractConnector");
    }

    public void removeConnectorByRequest(THttpRequest request) {
        if (request != null && !request.getIsKeepAlive() && this.mHttpConnectorMap != null && !this.mHttpConnectorMap.isEmpty()) {
            this.mHttpConnectorMap.remove(request);
        }
    }

    public AbstractConnector isExistConnector(THttpRequest request) {
        if (request != null && !request.getIsKeepAlive() && this.mHttpConnectorMap != null && !this.mHttpConnectorMap.isEmpty()) {
            return this.mHttpConnectorMap.get(request);
        }
        return null;
    }

    private int getAliveConnectorSize() {
        if (this.mAliveConnectorMap == null || this.mAliveConnectorMap.isEmpty()) {
            return 0;
        }
        return this.mAliveConnectorMap.size();
    }

    public void cleanup() {
        closeAllAliveTask();
    }

    /* JADX WARNING: Code restructure failed: missing block: B:26:?, code lost:
        return;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void closeAllAliveTask() {
        /*
            r4 = this;
            monitor-enter(r4)
            java.util.Map<java.lang.String, com.gau.utils.net.connector.HttpConnectorAlive> r3 = r4.mAliveConnectorMap     // Catch:{ all -> 0x001d }
            if (r3 == 0) goto L_0x000d
            java.util.Map<java.lang.String, com.gau.utils.net.connector.HttpConnectorAlive> r3 = r4.mAliveConnectorMap     // Catch:{ all -> 0x001d }
            boolean r3 = r3.isEmpty()     // Catch:{ all -> 0x001d }
            if (r3 == 0) goto L_0x000f
        L_0x000d:
            monitor-exit(r4)     // Catch:{ all -> 0x001d }
        L_0x000e:
            return
        L_0x000f:
            java.util.Map<java.lang.String, com.gau.utils.net.connector.HttpConnectorAlive> r3 = r4.mAliveConnectorMap     // Catch:{ all -> 0x001d }
            java.util.Set r3 = r3.entrySet()     // Catch:{ all -> 0x001d }
            java.util.Iterator r2 = r3.iterator()     // Catch:{ all -> 0x001d }
            if (r2 != 0) goto L_0x0020
            monitor-exit(r4)     // Catch:{ all -> 0x001d }
            goto L_0x000e
        L_0x001d:
            r3 = move-exception
            monitor-exit(r4)     // Catch:{ all -> 0x001d }
            throw r3
        L_0x0020:
            boolean r3 = r2.hasNext()     // Catch:{ all -> 0x001d }
            if (r3 == 0) goto L_0x0038
            java.lang.Object r1 = r2.next()     // Catch:{ all -> 0x001d }
            java.util.Map$Entry r1 = (java.util.Map.Entry) r1     // Catch:{ all -> 0x001d }
            if (r1 == 0) goto L_0x0020
            java.lang.Object r0 = r1.getValue()     // Catch:{ all -> 0x001d }
            com.gau.utils.net.connector.AbstractConnector r0 = (com.gau.utils.net.connector.AbstractConnector) r0     // Catch:{ all -> 0x001d }
            r0.closeConnect()     // Catch:{ all -> 0x001d }
            goto L_0x0020
        L_0x0038:
            java.util.Map<java.lang.String, com.gau.utils.net.connector.HttpConnectorAlive> r3 = r4.mAliveConnectorMap     // Catch:{ all -> 0x001d }
            r3.clear()     // Catch:{ all -> 0x001d }
            r3 = 0
            r4.mAliveConnectorMap = r3     // Catch:{ all -> 0x001d }
            monitor-exit(r4)     // Catch:{ all -> 0x001d }
            goto L_0x000e
        */
        throw new UnsupportedOperationException("Method not decompiled: com.gau.utils.net.connector.ConnectorFactory.closeAllAliveTask():void");
    }

    public void setMaxAliveNum(int num) {
        this.mMaxAliveConnectorNum = num;
    }

    public int getMaxAliveNum() {
        return this.mMaxAliveConnectorNum;
    }

    public String putCommonHeartUrl(String host, String heartUrl) {
        if (this.mHeartSetting != null) {
            return this.mHeartSetting.putHeartUrl(host, heartUrl);
        }
        return null;
    }

    public String removeHeartUrl(String host) {
        if (this.mHeartSetting != null) {
            return this.mHeartSetting.removeHeartUrl(host);
        }
        return null;
    }

    public String getHeartUrl(String host) {
        if (this.mHeartSetting != null) {
            return this.mHeartSetting.getHeartUrl(host);
        }
        return null;
    }

    public Map<String, String> getAllHeartUrl() {
        if (this.mHeartSetting != null) {
            return this.mHeartSetting.getAllHeartUrl();
        }
        return null;
    }

    public void setCommonHeartTime(long heartTime) {
        if (this.mHeartSetting != null) {
            this.mHeartSetting.setCommonHeartTime(heartTime);
        }
    }

    public long getCommonHeartTime() {
        if (this.mHeartSetting != null) {
            return this.mHeartSetting.getCommonHeartTime();
        }
        return -1;
    }

    private void removeFromMap(AbstractConnector connector) {
        synchronized (this) {
            if (this.mAliveConnectorMap != null) {
                this.mAliveConnectorMap.remove((String) ((HttpConnectorAlive) connector).getTag());
            }
        }
    }

    public void onConnectorDestroy(AbstractConnector connector) {
        removeFromMap(connector);
    }
}
