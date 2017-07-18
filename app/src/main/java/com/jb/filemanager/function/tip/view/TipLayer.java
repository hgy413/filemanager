package com.jb.filemanager.function.tip.view;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.DrawableRes;
import android.support.annotation.IntDef;
import android.support.annotation.NonNull;
import android.support.annotation.StringRes;
import android.support.v4.util.SparseArrayCompat;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.jb.filemanager.R;
import com.jb.filemanager.TheApplication;
import com.jb.filemanager.function.tip.event.LayerCloseEvent;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Created by nieyh on 17-7-17.
 */

public class TipLayer extends BaseLayer implements View.OnClickListener {

    //释放空间提示层
    public static final int FREE_SPACE_TIP_LAYER = 1;
    //usb状态提示层
    public static final int USB_STATE_TIP_LAYER = 2;

    /**
     * {@link #FREE_SPACE_TIP_LAYER} <br/>
     * {@link #USB_STATE_TIP_LAYER} <br/>
     */
    @IntDef({FREE_SPACE_TIP_LAYER, USB_STATE_TIP_LAYER})
    @Retention(value = RetentionPolicy.SOURCE)
    public @interface LAYER_TYPE {}
    private int mLayerType = -1;

    protected ImageView mIcon;
    protected TextView mTxt;
    protected TextView mBtu;
    protected ImageView mClose;

    protected static SparseArrayCompat<Boolean> mExitTipLayerMap = new SparseArrayCompat<>(2);

    public TipLayer(@NonNull Context context, @LAYER_TYPE int layerType) {
        super(context);
        this.mLayerType = layerType;
        setBackgroundColor(Color.WHITE);
        //强制当前视图吃掉事件
        setClickable(true);
    }

    @Override
    public void onCreateView(Context context) {
        inflateView(R.layout.view_tip);
        mIcon = findView(R.id.view_tip_icon);
        mTxt = findView(R.id.view_tip_txt);
        mBtu = findView(R.id.view_tip_btu);
        mClose = findView(R.id.view_tip_close);
        mClose.setOnClickListener(this);
    }

    @Override
    protected void onAttachedToWindow() {
        mExitTipLayerMap.put(mLayerType, Boolean.TRUE);
        super.onAttachedToWindow();
    }

    @Override
    protected void onDetachedFromWindow() {
        mExitTipLayerMap.put(mLayerType, Boolean.FALSE);
        super.onDetachedFromWindow();
    }

    public boolean _isShow() {
        return mExitTipLayerMap.get(mLayerType, false);
    }

    protected final void setIcon(@DrawableRes int resId) {
        if (mIcon != null) {
            mIcon.setImageResource(resId);
        }
    }

    protected final void setContentTxt(@StringRes int resId) {
        if (mTxt != null) {
            mTxt.setText(resId);
        }
    }

    protected final void setBtuTxt(@StringRes int resId) {
        if (mBtu != null) {
            mBtu.setText(resId);
        }
    }

    protected final void setBtuActionListener(View.OnClickListener operateListener) {
        if (mBtu != null && operateListener != null) {
            mBtu.setOnClickListener(operateListener);
        }
    }

    protected final void postEvent(Object event) {
        if (event != null) {
            TheApplication.getGlobalEventBus().post(event);
        }
    }

    @Override
    public void onClick(View v) {
        if (v == mClose) {
            postEvent(new LayerCloseEvent(mLayerType));
        }
    }

    protected void close() {
        postEvent(new LayerCloseEvent(mLayerType));
    }

    @Override
    public int getLayerType() {
        return mLayerType;
    }
}
