package com.jb.filemanager.ad.bubble;

import android.app.Activity;
import android.content.Context;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.jb.filemanager.R;
import com.jb.filemanager.util.APIUtil;
import com.jb.filemanager.util.DrawUtils;

/**
 * Created by bill wang on 15/9/15.
 *
 */
public class AdPresetView extends FrameLayout {

    private ImageView mAdView;
    private Listener mListener;

    public AdPresetView(Context context) {
        super(context);
        init(context);
    }

    public AdPresetView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public void setListener(Listener listener) {
        mListener = listener;
    }

    private void init(Context context) {
        // 透明背景
        setBackgroundColor(APIUtil.getColor(context, R.color.black_70));

        // 屏蔽触摸事件
        setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return v != mAdView || v.onTouchEvent(event);
            }
        });
    }

    public void show(Activity activity, int resId) {


        if (mAdView != null) {
            removeView(mAdView);
            mAdView = null;
        }

        mAdView = new ImageView(activity);
        mAdView.setImageResource(resId);

        mAdView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mListener != null) {
                    mListener.onClick();
                }
            }
        });

        LayoutParams layout = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
        layout.gravity = Gravity.CENTER_VERTICAL;
        int margin = DrawUtils.dip2px(10);
        layout.setMargins(margin, 0, margin, 0);
        addView(mAdView, layout);

        LayoutParams containerLayout = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
        activity.addContentView(this, containerLayout);
    }

    public void dismiss() {
        ViewGroup vg = (ViewGroup)getParent();
        vg.removeView(this);
    }

    public interface Listener {
        void onClick();
    }
}