package com.gau.utils.net.request;

import com.gau.utils.net.IConnectListener;
import com.gau.utils.net.INetRecord;
import com.gau.utils.net.asrFiltier.IAsrFilter;
import com.gau.utils.net.operator.ByteArrayHttpOperatror;
import com.gau.utils.net.operator.IHttpOperator;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import org.apache.http.Header;
import org.apache.http.message.BasicHeader;

public class THttpRequest {
    private static final int DEFAULT_CONNECTION_TIMEOUT = 15000;
    private static final int DEFAULT_RETRY_TIME = 2;
    private static final int DEFAULT_SO_TIMEOUT_VALUE = 30000;
    public static final int PROTOCOL_GET = 0;
    public static final int PROTOCOL_NOT_DEFINE = -1;
    public static final int PROTOCOL_POST = 1;
    private final int DEFAULT_THREAD_PRIORITY;
    IAsrFilter mAsrFilter;
    private int mCurRetryTime;
    private URI mCurrentUrl;
    List<Header> mHeaderStore;
    private long mHeartTime;
    private URI mHeartUrl;
    private boolean mIsAlive;
    private boolean mIsAsync;
    private boolean mIsCanceled;
    private boolean mIsMustAlive;
    private INetRecord mNetRecord;
    IHttpOperator mOperator;
    private HashMap<String, String> mParamMap;
    private byte[] mPostData;
    private int mProtocol;
    IConnectListener mReceiver;
    private int mRequestPriority;
    public int mRequestType;
    private int mRetryTime;
    int mSocketTimeoutValue;
    int mTimeoutValue;
    private List<URI> mUrlList;

    public THttpRequest(String url, byte[] postData, IConnectListener receiver) throws IllegalArgumentException, URISyntaxException {
        this.mRetryTime = 2;
        this.mCurRetryTime = -1;
        this.mRequestType = 0;
        this.mIsCanceled = false;
        this.mTimeoutValue = 15000;
        this.mSocketTimeoutValue = DEFAULT_SO_TIMEOUT_VALUE;
        this.DEFAULT_THREAD_PRIORITY = 8;
        this.mRequestPriority = 5;
        this.mIsAsync = true;
        this.mIsAlive = false;
        this.mIsMustAlive = false;
        this.mHeartTime = -1;
        this.mProtocol = -1;
        if (url == null) {
            throw new IllegalArgumentException("url==null");
        } else if (receiver == null) {
            throw new IllegalArgumentException("receiver==null");
        } else {
            setUrl(url);
            this.mPostData = postData;
            this.mReceiver = receiver;
        }
    }

    public THttpRequest(String url, IConnectListener receiver) throws IllegalArgumentException, URISyntaxException {
        this(url, (byte[]) null, receiver);
    }

    public void addAlternateUrl(String url) throws URISyntaxException {
        if (this.mUrlList == null) {
            this.mUrlList = new ArrayList();
        }
        this.mUrlList.add(new URI(url));
    }

    public List<URI> getAlternateUrl() {
        if (this.mUrlList == null || this.mUrlList.size() <= 1) {
            return null;
        }
        return this.mUrlList.subList(1, this.mUrlList.size());
    }

    public void setHeartUrl(String heartUrl) throws URISyntaxException {
        this.mHeartUrl = new URI(heartUrl);
    }

    public URI getHeartUrl() {
        return this.mHeartUrl;
    }

    public List<URI> getAllUrl() {
        return this.mUrlList;
    }

    public URI getCurrentUrl() {
        return this.mCurrentUrl;
    }

    public void setCurrentUrl(URI url) {
        this.mCurrentUrl = url;
    }

    public IAsrFilter getAsrFilter() {
        return this.mAsrFilter;
    }

    public void setAsrFilter(IAsrFilter asrFilter) {
        this.mAsrFilter = asrFilter;
    }

    public IHttpOperator getOperator() {
        if (this.mOperator == null) {
            this.mOperator = new ByteArrayHttpOperatror();
        }
        return this.mOperator;
    }

    public void setOperator(IHttpOperator operator) {
        this.mOperator = operator;
    }

    public int getCurRetryTime() {
        if (this.mCurRetryTime == -1) {
            this.mCurRetryTime = this.mRetryTime;
        }
        return this.mCurRetryTime;
    }

    public void setCurRetryTime(int curRetryTime) {
        this.mCurRetryTime = curRetryTime;
    }

    public int getRetryTime() {
        return this.mRetryTime;
    }

    public void setRetryTime(int retryTime) {
        this.mRetryTime = retryTime;
    }

    public int getTimeoutValue() {
        return this.mTimeoutValue;
    }

    public void setTimeoutValue(int timeoutValue) {
        this.mTimeoutValue = timeoutValue;
    }

    public int getSocketTimeoutValue() {
        return this.mSocketTimeoutValue;
    }

    public void setSocketTimeoutValue(int socketTimeoutValue) {
        this.mSocketTimeoutValue = socketTimeoutValue;
    }

    public IConnectListener getReceiver() {
        return this.mReceiver;
    }

    public void setReceiver(IConnectListener receiver) {
        this.mReceiver = receiver;
    }

    public URI getUrl() {
        if (this.mUrlList == null || this.mUrlList.size() <= 0) {
            return null;
        }
        return this.mUrlList.get(0);
    }

    public void setUrl(String url) throws URISyntaxException {
        if (url != null) {
            URI uri = new URI(url);
            if (this.mUrlList == null) {
                this.mUrlList = new ArrayList();
            }
            if (this.mUrlList.isEmpty()) {
                this.mUrlList.add(uri);
            } else {
                this.mUrlList.set(0, uri);
            }
        }
    }

    public byte[] getPostData() {
        return this.mPostData;
    }

    public void setPostData(byte[] postData) {
        this.mPostData = postData;
    }

    public void setIsAsync(boolean isAsync) {
        this.mIsAsync = isAsync;
    }

    public boolean getIsAsync() {
        return this.mIsAsync;
    }

    public void setIsKeepAlive(boolean isAlive) {
        this.mIsAlive = isAlive;
    }

    public void setIsMustAlive(boolean isAlwaysAlive) {
        this.mIsMustAlive = isAlwaysAlive;
    }

    public boolean getIsMustAlive() {
        return this.mIsMustAlive;
    }

    public boolean getIsKeepAlive() {
        return this.mIsAlive;
    }

    public void addHeader(String name, String value) {
        if (name == null || value == null) {
            throw new IllegalArgumentException("AddHeader must have name and value");
        }
        BasicHeader header = new BasicHeader(name, value);
        if (this.mHeaderStore == null) {
            this.mHeaderStore = new ArrayList();
        }
        this.mHeaderStore.add(header);
    }

    public List<Header> getHeader() {
        return this.mHeaderStore;
    }

    public synchronized boolean isCanceled() {
        return this.mIsCanceled;
    }

    public synchronized void setCanceled(boolean canceled) {
        this.mIsCanceled = canceled;
    }

    public THttpRequest clone() {
        return null;
    }

    public int getRequestPriority() {
        return this.mRequestPriority;
    }

    public void setRequestPriority(int requestPriority) {
        if (requestPriority < 1 || requestPriority > 10) {
            this.mRequestPriority = 5;
        } else {
            this.mRequestPriority = requestPriority;
        }
    }

    public void setNetRecord(INetRecord record) {
        this.mNetRecord = record;
    }

    public INetRecord getNetRecord() {
        return this.mNetRecord;
    }

    public void setHeartTime(long heartTime) {
        this.mHeartTime = heartTime;
    }

    public long getHeartTime() {
        return this.mHeartTime;
    }

    public void cleanup() {
        if (this.mUrlList != null) {
            this.mUrlList.clear();
            this.mUrlList = null;
        }
        if (this.mPostData != null) {
            this.mPostData = null;
        }
        if (this.mHeaderStore != null) {
            this.mHeaderStore.clear();
            this.mHeaderStore = null;
        }
        if (this.mNetRecord != null) {
            this.mNetRecord = null;
        }
        if (this.mReceiver != null) {
            this.mReceiver = null;
        }
        if (this.mAsrFilter != null) {
            this.mAsrFilter = null;
        }
        if (this.mOperator != null) {
            this.mOperator = null;
        }
    }

    public void setParamMap(HashMap<String, String> mParamMap2) {
        this.mParamMap = mParamMap2;
    }

    public HashMap<String, String> getParamMap() {
        return this.mParamMap;
    }

    public void setProtocol(int type) {
        this.mProtocol = type;
    }

    public int getProtocol() {
        return this.mProtocol;
    }
}
