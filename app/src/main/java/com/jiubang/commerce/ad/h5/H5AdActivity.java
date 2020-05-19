package com.jiubang.commerce.ad.h5;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

public class H5AdActivity extends Activity {
    public static final String AD_URL = "ad_url";
    private ProgressWebView mWebView;

    public static boolean openLink(Context context, String url) {
        if (context == null || TextUtils.isEmpty(url)) {
            return false;
        }
        Intent intent = new Intent(context, H5AdActivity.class);
        intent.putExtra(AD_URL, url);
        intent.addFlags(268435456);
        context.startActivity(intent);
        return true;
    }

    /* access modifiers changed from: protected */
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView();
        loadData();
    }

    private void initView() {
        getWindow().requestFeature(1);
        getWindow().requestFeature(2);
        this.mWebView = new ProgressWebView(this);
        this.mWebView.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
        this.mWebView.getSettings().setJavaScriptEnabled(true);
        this.mWebView.setWebViewClient(new WebViewClient() {
            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                super.onReceivedError(view, errorCode, description, failingUrl);
                Toast.makeText(this, "Opps! " + description, 0).show();
            }
        });
        setContentView(this.mWebView);
    }

    private void loadData() {
        String adUrl = getIntent().getStringExtra(AD_URL);
        if (TextUtils.isEmpty(adUrl)) {
            finish();
        }
        this.mWebView.loadUrl(adUrl);
    }

    /* access modifiers changed from: protected */
    public void onDestroy() {
        super.onDestroy();
        this.mWebView = null;
    }
}
