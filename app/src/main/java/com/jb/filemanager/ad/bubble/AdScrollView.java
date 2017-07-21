package com.jb.filemanager.ad.bubble;

import android.app.Activity;
import android.content.Context;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.facebook.ads.NativeAdScrollView;
import com.facebook.ads.NativeAdsManager;
import com.jb.filemanager.R;
import com.jb.filemanager.util.APIUtil;

/**
 * Created by bill wang on 15/9/15.
 *
 */
public class AdScrollView extends FrameLayout {

    private Context mContext;

    private NativeAdScrollView mAdScrollView;

    public AdScrollView(Context context) {
        super(context);
        init(context);
    }

    public AdScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    private void init(Context context) {
        mContext = context;
        // 透明背景
        setBackgroundColor(APIUtil.getColor(context, R.color.black_70));

        // 屏蔽触摸事件
        setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return v != mAdScrollView || v.onTouchEvent(event);
            }
        });
    }

    public void show(Activity activity, NativeAdsManager manager) {

        if (mAdScrollView != null) {
            removeView(mAdScrollView);
            mAdScrollView = null;
        }

//        mAdScrollView = new NativeAdScrollView(activity, manager, NativeAdView.Type.HEIGHT_400);

        mAdScrollView = new NativeAdScrollView(activity, manager, new AdUnitView(mContext));

        LayoutParams layout = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
        layout.gravity = Gravity.CENTER_VERTICAL;
        addView(mAdScrollView, layout);

        LayoutParams containerLayout = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
        activity.addContentView(this, containerLayout);
    }

    public void dismiss() {
        ViewGroup vg = (ViewGroup)getParent();
        vg.removeView(this);
    }

}