package com.jiubang.commerce.ad.h5;

import android.content.Context;
import android.util.AttributeSet;
import android.webkit.WebView;
import android.widget.AbsoluteLayout;
import android.widget.ProgressBar;

public class ProgressWebView extends WebView {
    /* access modifiers changed from: private */
    public ProgressBar mProgressbar;

    public ProgressWebView(Context context) {
        super(context);
        init();
    }

    public ProgressWebView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        this.mProgressbar = new ProgressBar(getContext(), (AttributeSet) null, 16842872);
        this.mProgressbar.setLayoutParams(new AbsoluteLayout.LayoutParams(-1, 3, 0, 0));
        addView(this.mProgressbar);
        setWebChromeClient(new WebChromeClient());
    }

    public class WebChromeClient extends android.webkit.WebChromeClient {
        public WebChromeClient() {
        }

        public void onProgressChanged(WebView view, int newProgress) {
            if (newProgress == 100) {
                ProgressWebView.this.mProgressbar.setVisibility(8);
            } else {
                if (ProgressWebView.this.mProgressbar.getVisibility() == 8) {
                    ProgressWebView.this.mProgressbar.setVisibility(0);
                }
                ProgressWebView.this.mProgressbar.setProgress(newProgress);
            }
            super.onProgressChanged(view, newProgress);
        }
    }

    /* access modifiers changed from: protected */
    public void onScrollChanged(int l, int t, int oldl, int oldt) {
        AbsoluteLayout.LayoutParams lp = (AbsoluteLayout.LayoutParams) this.mProgressbar.getLayoutParams();
        lp.x = l;
        lp.y = t;
        this.mProgressbar.setLayoutParams(lp);
        super.onScrollChanged(l, t, oldl, oldt);
    }
}
