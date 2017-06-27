package com.jb.filemanager.ui.dialog;


import android.app.Activity;
import android.app.Dialog;
import android.view.Window;
import android.view.WindowManager;

import com.jb.filemanager.R;

/**
 * 自定义dialog的基类<br>
 * 所有自定义dialog继承些类，以便统一扩展共同的特点<br>
 *
 */
public abstract class BaseDialog extends Dialog {

    protected Activity mActivity;

    public BaseDialog(Activity act) {
        this(act, false);
    }

    public BaseDialog(Activity act, int style) {
        this(act, style, false);
    }

    public BaseDialog(Activity act, boolean cancelOutside) {
        this(act, R.style.base_dialog_theme, cancelOutside);
    }

    public BaseDialog(Activity act, int style, boolean cancelOutside) {
        super(act, style);
        mActivity = act;
        //modify by nieyh 所有对话框都可以点击外部消失
        setCanceledOnTouchOutside(true);
    }

    /**
     * 设置Dialog的大小<br>
     *
     * @param width
     * @param height
     */
    public void setSize(int width, int height) {
        getWindow().setLayout(width, height);
    }

    /**
     * 设置Dialog的显示位置<br>
     * <p>
     * lp.x与lp.y表示相对于原始位置的偏移.
     * 当参数值包含Gravity.LEFT时,对话框出现在左边,所以lp.x就表示相对左边的偏移,负值忽略.
     * 当参数值包含Gravity.RIGHT时,对话框出现在右边,所以lp.x就表示相对右边的偏移,负值忽略.
     * 当参数值包含Gravity.TOP时,对话框出现在上边,所以lp.y就表示相对上边的偏移,负值忽略.
     * 当参数值包含Gravity.BOTTOM时,对话框出现在下边,所以lp.y就表示相对下边的偏移,负值忽略.
     * 当参数值包含Gravity.CENTER_HORIZONTAL时
     * ,对话框水平居中,所以lp.x就表示在水平居中的位置移动lp.x像素,正值向右移动,负值向左移动.
     * 当参数值包含Gravity.CENTER_VERTICAL时
     * ,对话框垂直居中,所以lp.y就表示在垂直居中的位置移动lp.y像素,正值向下移动,负值向上移动.
     * gravity的默认值为Gravity.CENTER,即Gravity.CENTER_HORIZONTAL |
     * Gravity.CENTER_VERTICAL.
     * </P>
     *
     * @param x
     * @param y
     * @param gravity
     */
    public void setXY(int x, int y, int gravity) {
        final Window window = getWindow();
        WindowManager.LayoutParams lp = window.getAttributes();
        lp.x = x;
        lp.y = y;
        window.setGravity(gravity);
        window.setAttributes(lp);
    }

    /**
     * 获取文本
     */
    protected String getString(int resId) {
        return mActivity.getString(resId);
    }

}