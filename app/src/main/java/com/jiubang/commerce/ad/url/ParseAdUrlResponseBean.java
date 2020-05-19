package com.jiubang.commerce.ad.url;

import android.content.Context;
import com.jiubang.commerce.ad.manager.AdSdkManager;
import com.jiubang.commerce.statistics.AdUrlParseResultsStatistic;

public class ParseAdUrlResponseBean {
    public static final int STATUS_EXCEPTION = 2;
    public static final int STATUS_FAIL = 1;
    public static final int STATUS_SUCCESS = 3;
    private String mMsg;
    private long mParseTimeDuration;
    private String mParsedAdUrl;
    private int mStatus;
    private int mStatusCode;

    public ParseAdUrlResponseBean() {
    }

    public ParseAdUrlResponseBean(int statusCode, int status, String parsedAdUrl, String msg, long parseTimeDuration) {
        this.mStatusCode = statusCode;
        this.mStatus = status;
        this.mParsedAdUrl = parsedAdUrl;
        this.mMsg = msg;
        this.mParseTimeDuration = parseTimeDuration;
    }

    public String getParsedAdUrl() {
        return this.mParsedAdUrl;
    }

    public void setParsedAdUrl(String parsedAdUrl) {
        this.mParsedAdUrl = parsedAdUrl;
    }

    public int getStatusCode() {
        return this.mStatusCode;
    }

    public void setStatusCode(int statusCode) {
        this.mStatusCode = statusCode;
    }

    public int getStatus() {
        return this.mStatus;
    }

    public void setStatus(int status) {
        this.mStatus = status;
    }

    public String getMsg() {
        return this.mMsg;
    }

    public void setMsg(String msg) {
        this.mMsg = msg;
    }

    public long getParseTimeDuration() {
        return this.mParseTimeDuration;
    }

    public void setParseTimeDuration(long parseTimeDuration) {
        this.mParseTimeDuration = parseTimeDuration;
    }

    public String getString() {
        return " 状态码:" + this.mStatusCode + " 解析状态:" + getStatusMsg(this.mStatus) + " 备注:" + this.mMsg;
    }

    public String getStatusMsg(int status) {
        if (this.mStatus == 1) {
            return "invalid";
        }
        if (this.mStatus == 2) {
            return "exception";
        }
        return "sucessful";
    }

    public void uploadParseUrlStatusStatistic(Context context, String moduleId, String mapId, String aId) {
        AdUrlParseResultsStatistic.uploadAdStatusStatistic(context, mapId, this.mStatus, AdSdkManager.getInstance().getCid(), aId, moduleId, String.valueOf(this.mParseTimeDuration), this.mMsg);
    }
}
