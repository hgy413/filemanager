package com.jb.filemanager.function.tip.view;

import android.content.Context;
import android.support.annotation.IdRes;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

/**
 * Created by nieyh on 17-7-17.
 * 抽象层
 */

public abstract class BaseLayer extends RelativeLayout {

    public BaseLayer(@NonNull Context context) {
        super(context);
        onCreateView(context);
    }

    /**
     * 视图创建器
     * 描述：<br/>
     * <ol>
     *     <li>调用{@link #inflateView(int)} 引入布局</li>
     *     <li>调用{@link #findView(int)} 载入视图</li>
     * </ol>
     * */
    public abstract void onCreateView(Context context);

    //创建视图
    protected final void inflateView(@LayoutRes int layoutResId) {
        View.inflate(getContext(), layoutResId, this);
    }

    /**
     * 获取指定视图
     * @param resId 资源id
     * */
    protected final <T extends View> T findView(@IdRes int resId) {
        return (T) findViewById(resId);
    }

    /**
     * 添加视图到父布局中
     * @param root 指定父布局
     * */
    public final void addView2Root(ViewGroup root, ViewGroup.LayoutParams layoutParams) {
        if (root == null) {
            return;
        }
        if (this.getParent() != null) {
            return;
        }
        root.addView(this, layoutParams);
    }

    /**
     * 从父布局中移除视图
     * @param root 指定父布局
     * */
    public final void removeView2Root(ViewGroup root) {
        if (root == null) {
            return;
        }
        if (this.getParent() != root) {
            return;
        }
        root.removeView(this);
    }
}
