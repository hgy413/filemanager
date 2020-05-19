package com.gau.utils.net.connector;

import android.content.Context;
import com.gau.utils.net.IConnectListener;
import com.gau.utils.net.INetRecord;
import com.gau.utils.net.NetException;
import com.gau.utils.net.asrFiltier.IAsrFilter;
import com.gau.utils.net.request.THttpRequest;
import com.gau.utils.net.response.IResponse;
import com.gau.utils.net.util.NetLog;
import com.gau.utils.net.util.NetUtil;
import java.io.IOException;
import java.net.SocketTimeoutException;
import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import org.apache.http.Header;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.conn.ConnectTimeoutException;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

public class HttpConnector extends AbstractConnector implements Runnable {
    private Thread mConnectThread;
    private int mSwitchUriTimes = 0;

    public HttpConnector(THttpRequest httpRequest, Context context) throws IllegalArgumentException {
        super(httpRequest, context);
    }

    public HttpConnector(THttpRequest httpRequest, IConnectListener connectReceiver, Context context) throws IllegalArgumentException {
        super(httpRequest, connectReceiver, context);
    }

    public void connectAsynchronous() {
        if (this.mConnectThread == null) {
            this.mConnectThread = new Thread(this);
            this.mConnectThread.setPriority(this.mRequest.getRequestPriority());
            this.mConnectThread.start();
            NetLog.info("testBattery, End HttpConnector connectAsynchronous", (Throwable) null);
        }
    }

    public void cancelCurrentConnect() {
    }

    public void connect() {
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

    public void closeConnect() {
    }

    public void run() {
        connect();
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

    private void configClient(HttpClient httpClient) throws IllegalAccessException {
        int connectType = NetUtil.getNetWorkType(this.mContext);
        if (2 == connectType) {
            httpClient.getParams().setParameter("http.route.default-proxy", NetUtil.getProxy(this.mContext));
        } else if (connectType == -1) {
            throw new IllegalAccessException();
        }
        httpClient.getParams().setParameter("http.socket.timeout", Integer.valueOf(this.mRequest.getSocketTimeoutValue()));
        httpClient.getParams().setParameter("http.connection.timeout", Integer.valueOf(this.mRequest.getTimeoutValue()));
    }

    private void shutDown(DefaultHttpClient client) {
        if (client != null) {
            NetLog.debug("shutDown client ", (Throwable) null);
            client.getConnectionManager().shutdown();
        }
    }

    private URI selectURI(THttpRequest request) {
        if (request == null || request.getAllUrl() == null || this.mSwitchUriTimes >= request.getAllUrl().size()) {
            return null;
        }
        return request.getAllUrl().get(this.mSwitchUriTimes);
    }

    private IResponse connectSynchronous(THttpRequest request) throws ClientProtocolException, IOException, IllegalAccessException, Exception {
        IResponse reponseData;
        IResponse reponseData2;
        HttpResponse response;
        NetLog.info("StartConnect url= " + request.getUrl(), (Throwable) null);
        NetLog.info("testBattery, Begin HttpConnector connectSynchronous url = " + request.getUrl(), (Throwable) null);
        DefaultHttpClient client = new DefaultHttpClient();
        long nowTime = System.currentTimeMillis();
        INetRecord record = request.getNetRecord();
        try {
            URI uri = selectURI(request);
            if (uri == null) {
                throw new NetException(6);
            }
            request.setCurrentUrl(uri);
            HttpHost httphost = new HttpHost(uri.getHost(), uri.getPort());
            configClient(client);
            int protocol = request.getProtocol();
            if (protocol == 0 || (protocol == -1 && request.getPostData() == null)) {
                HttpGet get = new HttpGet(uri);
                configHttpHeader(get);
                if (record != null) {
                    record.onStartConnect(request, (Object) null, (Object) null);
                }
                response = client.execute(httphost, get);
                if (record != null) {
                    record.onConnectSuccess(request, (Object) null, (Object) null);
                }
            } else {
                HttpPost httpPost = new HttpPost(uri);
                configHttpHeader(httpPost);
                if (request.getPostData() != null) {
                    ByteArrayEntity byteArrayEntity = new ByteArrayEntity(request.getPostData());
                    byteArrayEntity.setChunked(false);
                    httpPost.setEntity(byteArrayEntity);
                } else {
                    HashMap<String, String> paramMap = request.getParamMap();
                    if (paramMap != null && !paramMap.isEmpty()) {
                        ArrayList<NameValuePair> nameValuePairs = new ArrayList<>();
                        for (String key : paramMap.keySet()) {
                            nameValuePairs.add(new BasicNameValuePair(key, paramMap.get(key)));
                        }
                        UrlEncodedFormEntity formEntity = new UrlEncodedFormEntity(nameValuePairs, "UTF-8");
                        formEntity.setChunked(false);
                        httpPost.setEntity(formEntity);
                    }
                }
                if (record != null) {
                    record.onStartConnect(request, (Object) null, (Object) null);
                }
                response = client.execute(httphost, httpPost);
                if (record != null) {
                    record.onConnectSuccess(request, (Object) null, (Object) null);
                }
            }
            int responseCode = response.getStatusLine().getStatusCode();
            long connectOverTime = System.currentTimeMillis();
            NetLog.info("connectTime= " + (connectOverTime - nowTime), (Throwable) null);
            NetLog.debug("responseCode= " + responseCode, (Throwable) null);
            if (responseCode == 200) {
                IAsrFilter filter = request.getAsrFilter();
                if (filter == null || !filter.isAsrResponse(response)) {
                    IResponse reponseData3 = request.getOperator().operateHttpResponse(request, response);
                    if (record != null) {
                        record.onTransFinish(request, (Object) null, (Object) null);
                    }
                    NetLog.info("DataTrafficTime= " + (System.currentTimeMillis() - connectOverTime), (Throwable) null);
                    shutDown(client);
                    request.setCurrentUrl((URI) null);
                    IResponse iResponse = reponseData3;
                    return reponseData3;
                }
                NetLog.debug("find AsrResponse", (Throwable) null);
                int retryTime = request.getCurRetryTime();
                if (retryTime > 0) {
                    request.setCurRetryTime(retryTime - 1);
                    reponseData = connectSynchronous(request);
                } else {
                    this.mSwitchUriTimes++;
                    if (this.mSwitchUriTimes < request.getAllUrl().size()) {
                        reponseData = connectSynchronous(request);
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
                    reponseData = connectSynchronous(request);
                } else {
                    this.mSwitchUriTimes++;
                    if (this.mSwitchUriTimes < request.getAllUrl().size()) {
                        reponseData = connectSynchronous(request);
                    } else {
                        throw new NetException(responseCode);
                    }
                }
            }
            NetLog.info("testBattery, end HttpConnector connectSynchronous url = " + request.getUrl(), (Throwable) null);
            IResponse iResponse2 = reponseData;
            return reponseData;
        } catch (Exception e) {
            if (record != null) {
                record.onException(e, (Object) null, (Object) null);
            }
            int retryTime3 = request.getCurRetryTime();
            if (retryTime3 > 0) {
                request.setCurRetryTime(retryTime3 - 1);
                reponseData2 = connectSynchronous(request);
            } else {
                this.mSwitchUriTimes++;
                if (this.mSwitchUriTimes < request.getAllUrl().size()) {
                    reponseData2 = connectSynchronous(request);
                } else if (e instanceof SocketTimeoutException) {
                    throw new NetException(11);
                } else if (e instanceof ConnectTimeoutException) {
                    throw new NetException(12);
                } else {
                    throw e;
                }
            }
        } finally {
            shutDown(client);
            request.setCurrentUrl((URI) null);
        }
    }
}
