package com.gau.utils.net;

import android.content.Context;
import com.gau.utils.net.request.THttpRequest;
import java.util.Map;

public class HttpAdapter {
    private HttpConnectScheduler mConnectScheduler = null;

    public HttpAdapter(Context context) {
        this.mConnectScheduler = new HttpConnectScheduler(context);
    }

    public void addTask(THttpRequest request) {
        if (request != null && this.mConnectScheduler != null) {
            this.mConnectScheduler.addRequest(request);
        }
    }

    public void cancelTask(THttpRequest request) {
        if (this.mConnectScheduler != null) {
            this.mConnectScheduler.cancelRequest(request);
        }
    }

    public void cancelTask(String url) {
        if (this.mConnectScheduler != null) {
            this.mConnectScheduler.cancelRequest(url);
        }
    }

    public void cleanup() {
        if (this.mConnectScheduler != null) {
            this.mConnectScheduler.cleanup();
        }
    }

    public void setMaxConnectThreadNum(int maxConnectThreadNum) {
        if (this.mConnectScheduler != null) {
            this.mConnectScheduler.setMaxConnectThreadNum(maxConnectThreadNum);
        }
    }

    public int getMaxConnectThreadNum() {
        if (this.mConnectScheduler != null) {
            return this.mConnectScheduler.getMaxConnectThreadNum();
        }
        return 1;
    }

    public String putCommonHeartUrl(String host, String heartUrl) {
        if (this.mConnectScheduler != null) {
            return this.mConnectScheduler.putCommonHeartUrl(host, heartUrl);
        }
        return null;
    }

    public String removeHeartUrl(String host) {
        if (this.mConnectScheduler != null) {
            return this.mConnectScheduler.removeHeartUrl(host);
        }
        return null;
    }

    public String getHeartUrl(String host) {
        if (this.mConnectScheduler != null) {
            return this.mConnectScheduler.getHeartUrl(host);
        }
        return null;
    }

    public Map<String, String> getAllCommonHeartUrl() {
        if (this.mConnectScheduler != null) {
            return this.mConnectScheduler.getAllCommonHeartUrl();
        }
        return null;
    }

    public void setCommonHeartTime(long heartTime) {
        if (this.mConnectScheduler != null) {
            this.mConnectScheduler.setCommonHeartTime(heartTime);
        }
    }

    public long getCommonHeartTime() {
        if (this.mConnectScheduler != null) {
            return this.mConnectScheduler.getCommonHeartTime();
        }
        return -1;
    }
}
