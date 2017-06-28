package com.jb.filemanager.function.fileexplorer;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.TextView;

/**
 * 通用的列表空界面
 *
 * @author chenbenbin
 */
public class CommonEmptyView extends TextView {

    public CommonEmptyView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CommonEmptyView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    /**
     * 设置提示文字
     */
    public void setTips(int resId) {
        setText(resId);
    }

    /**
     * 设置提示文字
     */
    public void setTips(String tips) {
        setText(tips);
    }

}
