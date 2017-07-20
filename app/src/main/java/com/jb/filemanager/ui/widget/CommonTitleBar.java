package com.jb.filemanager.ui.widget;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.StringRes;
import android.util.AttributeSet;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.jb.filemanager.R;
import com.jb.filemanager.TheApplication;
import com.jb.filemanager.function.image.ImageManagerFragment;
import com.jb.filemanager.function.search.view.SearchActivity;
import com.jb.filemanager.util.QuickClickGuard;

/**
 * Created by nieyh on 17-7-13.
 * 通用顶部标题栏 <br/>
 * 描述： <br/>
 * <p>
 *     1. 此标题需要一个默认的标题显示 {@link #setBarDefaultTitle(int)}, 如果不设置将使用默认应用标题 {@link com.jb.filemanager.R.string#app_name}
 *     2. 当你需要通知顶部更新UI时可直接使用{@link #notifyChoose(int, int)} <br/>
 *     3. 当你需要监听选项按钮被选中时 则可以监听{@link #setOnActionListener(OnActionListener)} <br/>
 *     4. 当用户点击返回键的时候 需要调用{@link #onBackPressed()} 来标题栏做出反应 并且需要根据返回值来做出相应处理 例子：{@link ImageManagerFragment#onBackPressed()}<br/>
 *     5. 当用户需使用FrameLayout作为父布局时可以剔除掉阴影 自行实现. 需要调用{@link #removeShadow()}
 * </p>
 */

public class CommonTitleBar extends LinearLayout {

    //返回
    private ImageView mBack;
    //取消
    private ImageView mCancel;
    //标题
    private TextView mTitle;
    //搜索
    private ImageView mSearch;
    //父布局
    private View mCheckBoxFl;
    //复选框
    private ImageView mGroupSelectBox;
    //默认标题
    private String mBarDefaultTitle;
    //选项选中事件
    private OnActionListener mOnActionListener;
    //防快速点击
    private QuickClickGuard mQuickClickGuard;
    //是否处于选择状态
    private boolean isChooseState = false;
    //阴影
    private View mShadow;

    public CommonTitleBar(Context context, AttributeSet attrs) {
        super(context, attrs);
        View.inflate(context, R.layout.view_common_title_bar, this);
        setOrientation(VERTICAL);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        mQuickClickGuard = new QuickClickGuard();
        mBack = (ImageView) findViewById(R.id.view_common_title_bar_back);
        mCancel = (ImageView) findViewById(R.id.view_common_title_bar_cancel);
        mTitle = (TextView) findViewById(R.id.view_common_title_bar_title);
        mShadow = findViewById(R.id.view_common_title_bar_shadow);
        mSearch = (ImageView) findViewById(R.id.view_common_title_bar_search);
        mGroupSelectBox = (ImageView) findViewById(R.id.view_common_title_bar_check_group);
        mCheckBoxFl = findViewById(R.id.view_common_title_bar_search_check_group_fl);
        mGroupSelectBox.setTag(false);
        mBarDefaultTitle = getContext().getString(R.string.app_name);
        mCheckBoxFl.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!mQuickClickGuard.isQuickClick(v.getId())) {
                    boolean isCheck = (boolean) mGroupSelectBox.getTag();
                    isCheck = !isCheck;
                    mGroupSelectBox.setTag(isCheck);
                    mGroupSelectBox.setImageResource(isCheck ? R.drawable.choose_all : R.drawable.choose_none_black);
                    if (mOnActionListener != null) {
                        mOnActionListener.onCheckAction(isCheck);
                    }
                    if (!isCheck) {
                        chgBarState(false);
                    }
                }
            }
        });
        mBack.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mOnActionListener != null) {
                    mOnActionListener.onBackAction();
                }
            }
        });
        mCancel.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                chgBarState(false);
                if (mOnActionListener != null) {
                    mOnActionListener.onCancelAction();
                }
            }
        });
        mSearch.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                SearchActivity.showSearchResult(TheApplication.getAppContext(), mOnActionListener.getCategoryType());
            }
        });
        chgBarState(false);
        setBackgroundColor(Color.TRANSPARENT);
    }

    /**
     * 设置标题默认文案
     * */
    public void setBarDefaultTitle(@StringRes int barTitleResId) {
        mBarDefaultTitle = getContext().getString(barTitleResId);
        mTitle.setText(mBarDefaultTitle);
    }

    /**
     * 当用户需使用FrameLayout作为父布局时可以剔除掉阴影 自行实现.
     * */
    public void removeShadow() {
        mShadow.setVisibility(GONE);
    }

    /**
     * 通知选择
     * @param chooseNum 已经选择应用的数目 <br/>
     * @param allNum 全部应用的数目
     * */
    public void notifyChoose(int chooseNum, int allNum) {
        if (chooseNum > 0) {
            isChooseState = true;
            chgBarState(isChooseState);
            mTitle.setText(getContext().getString(R.string.common_title_bar_choose_tip, chooseNum));
            if (chooseNum == allNum) {
                mGroupSelectBox.setTag(true);
                mGroupSelectBox.setImageResource(R.drawable.choose_all);
            } else {
                mGroupSelectBox.setImageResource(R.drawable.choose_none_black);
                mGroupSelectBox.setTag(false);
            }
        } else {
            isChooseState = false;
            chgBarState(isChooseState);
            mTitle.setText(mBarDefaultTitle);
            mGroupSelectBox.setTag(false);
            mGroupSelectBox.setImageResource(R.drawable.choose_none_black);
        }
    }

    /**
     * 通知返回按钮
     * @return
     * <ol>
     *      <li><b>true</b> 代表标题栏需要消耗返回事件</li>
     *      <li><b>false</b> 代表标题栏不需要消耗返回事件</li>
     * </ol>
     * */
    public boolean onBackPressed() {
        if (isChooseState) {
            isChooseState = false;
            //执行取消按钮
            mCancel.callOnClick();
            return true;
        }
        return false;
    }

    /**
     * 修改标题状态
     * */
    private void chgBarState(boolean isHaveChoose) {
        if (isHaveChoose) {
            mBack.setVisibility(INVISIBLE);
            mCancel.setVisibility(VISIBLE);
            mCheckBoxFl.setVisibility(VISIBLE);
            mSearch.setVisibility(INVISIBLE);
        } else {
            mBack.setVisibility(VISIBLE);
            mCancel.setVisibility(INVISIBLE);
            mCheckBoxFl.setVisibility(INVISIBLE);
            mSearch.setVisibility(VISIBLE);
            mTitle.setText(mBarDefaultTitle);
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        mOnActionListener = null;
        super.onDetachedFromWindow();
    }

    /**
     * 设置事件监听器
     * */
    public void setOnActionListener(OnActionListener onActionListener) {
        mOnActionListener = onActionListener;
    }

    /**
     * 选项选中事件
     * */
    public interface OnActionListener {
        void onCheckAction(boolean isCheck);
        void onBackAction();
        void onCancelAction();
        int getCategoryType();
    }
}
