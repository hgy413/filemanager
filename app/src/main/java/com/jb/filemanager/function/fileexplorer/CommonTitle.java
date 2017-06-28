package com.jb.filemanager.function.fileexplorer;


import android.content.Context;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.GradientDrawable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.jb.filemanager.R;
import com.jb.filemanager.TheApplication;
import com.jb.filemanager.util.APIUtil;
import com.jb.filemanager.util.ColorStatusBarUtil;


/**
 * 公用Title
 *
 * @author chenbenbin
 */
public class CommonTitle extends FrameLayout implements View.OnClickListener {

    private Context mContext;

    private TextView mTitle;
    private View mBackLayout;
    private View mExtraLayout;
    private ImageView mExtraBtn;
    private OnBackListener mBackListener;
    private OnExtraListener mExtraListener;

    private View mBackgroundColorView;
    private View mBackgroundImageView;
    private View mContentLayout;

    public CommonTitle(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        mTitle = (TextView) findViewById(R.id.common_title_main_text);
        mBackLayout = findViewById(R.id.common_title_back_and_text);
        mExtraBtn = (ImageView) findViewById(R.id.common_title_main_extra_btn);
        mExtraLayout = findViewById(R.id.common_title_main_extra_layout);
        mBackLayout.setOnClickListener(this);
        mExtraLayout.setOnClickListener(this);

        mBackgroundColorView = findViewById(R.id.common_title_panel_background_color);
        mBackgroundImageView = findViewById(R.id.common_title_panel_background_view);
        mContentLayout = findViewById(R.id.common_title_panel_content_layout);
        GradientDrawable temperatureBg = new GradientDrawable(GradientDrawable.Orientation.LEFT_RIGHT,
                new int[]{Color.parseColor("#0084ff"), Color.parseColor("#3bd6f2")});
        temperatureBg.setShape(GradientDrawable.RECTANGLE);
        APIUtil.setBackground(mBackgroundColorView, temperatureBg);
        APIUtil.setBackground(mContentLayout, temperatureBg);
//        String theme = LauncherModel.getInstance().getSettingManager().getAppTheme();
//        mBackgroundColorView.setBackgroundColor(ThemeUtil.getThemeColor(mContext, theme));
//        if (theme.equals(ThemeConstant.THEME_ID_CLASSIC)) {
//
//        } else if (theme.equals(ThemeConstant.THEME_ID_SIMPLE)) {
//            mBackgroundImageView.setVisibility(View.VISIBLE);
//        }

        ColorStatusBarUtil.appendStatusBarTopMargin(mContentLayout);
        ColorStatusBarUtil.appendStatusBarHeight(this);
    }

    /**
     * 设置为透明
     */
    public void setBackGroundTransparent() {
        mBackgroundColorView.setVisibility(View.INVISIBLE);
        mBackgroundImageView.setVisibility(View.INVISIBLE);
    }

    /**
     * 注册返回事件监听器
     */
    public void setOnBackListener(OnBackListener backListener) {
        mBackListener = backListener;
    }

    /**
     * 注册拓展按钮点击事件监听器
     *
     * @param listener
     */
    public void setOnExtraListener(OnExtraListener listener) {
        mExtraListener = listener;
    }

    /**
     * 设置标题字符串的资源ID
     */
    public void setTitleName(int id) {
        setTitleName(TheApplication.getAppContext().getString(id));
    }

    /**
     * 设置标题字符串
     */
    public void setTitleName(String title) {
        mTitle.setText(title);
    }

    /**
     * 设置拓展图标的资源ID
     */
    public void setExtraBtn(int id) {
        mExtraLayout.setVisibility(View.VISIBLE);
        mExtraBtn.setImageResource(id);
    }

    /**
     * 设置拓展图标的ColorFilter
     *
     * @param color
     * @param mode
     */
    public void setExtraBtnColorFilter(int color, PorterDuff.Mode mode) {
        mExtraBtn.setColorFilter(color, mode);
    }

    /**
     * 展示/隐藏拓展图标
     *
     * @param v
     */
    public void toggleExtraBtn(int v) {
        int visibility = mExtraLayout.getVisibility();
        if (visibility != v) {
            mExtraLayout.setVisibility(v);
        }
    }

    /**
     * 设置扩展按钮的透明度 <br>
     * 若想要改变按钮的Enable状态，可以使用{@link #setExtraBtnEnabled(boolean)} (已封装不可使用的显示状态)
     *
     * @param alpha
     */
    @SuppressWarnings("deprecation")
    public void setExtraBtnAlpha(int alpha) {
        mExtraBtn.setAlpha(alpha);
    }

    /**
     * 获取拓展按钮的可用性
     */
    public void setExtraBtnEnabled(boolean enabled) {
        mExtraLayout.setEnabled(enabled);
    }

    /**
     * 返回按钮事件监听器
     *
     * @author chenbenbin
     */
    public interface OnBackListener {
        /**
         * 点击返回按钮
         */
        public void onBackClick();
    }

    /**
     * 拓展按钮点击事件监听器
     *
     * @author chenbenbin
     */
    public interface OnExtraListener {
        /**
         * 点击拓展按钮
         */
        public void onExtraClick();
    }

    public void setMainTextTileLine() {

    }

    @Override
    public void onClick(View v) {
        if (v.equals(mBackLayout) && mBackListener != null) {
            mBackListener.onBackClick();
        } else if (v.equals(mExtraLayout) && mExtraListener != null) {
            mExtraListener.onExtraClick();
        }
    }
}