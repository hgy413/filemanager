package com.jb.filemanager.function.applock.dialog;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.support.annotation.ArrayRes;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.jb.filemanager.R;

/**
 * Created by nieyh on 2017/1/1.
 * 列表提示的对话框
 */

public class ListDialog implements View.OnClickListener {

    private LinearLayout mContentView;
    private TextView[] mListTxts;
    private String[] mArrayRes;
    private TextView mResponseTxt;
    private PopupWindow mPopupWindow;
    private int mCurrentPos;
    private PopupWindow.OnDismissListener mOnDismissListener;

    public ListDialog(TextView responseTxtView, @ArrayRes int arrayResId) {
        // TODO: 17-7-10 背景的阴影
        /**
         * 距离1dp
         * 模糊值2dp 000
         * */
        mResponseTxt = responseTxtView;
        mArrayRes = responseTxtView.getContext().getResources().getStringArray(arrayResId);
        mListTxts = new TextView[mArrayRes.length];
        mResponseTxt.setText(mArrayRes[0]);
        mCurrentPos = 0;
    }

    public void showUnderView(View attachView) {
        createQuestionView(attachView);
        if (mPopupWindow == null) {
            mPopupWindow = new PopupWindow(mContentView,
                    ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, true);

            mPopupWindow.setTouchable(true);
            mPopupWindow.setOutsideTouchable(true);
            mPopupWindow.setBackgroundDrawable(new BitmapDrawable(attachView.getResources(), (Bitmap) null));
            mPopupWindow.setOnDismissListener(mOnDismissListener);
        }
        int xoff = attachView.getWidth() - mContentView.getWidth();
        // 设置好参数之后再show
        mPopupWindow.showAsDropDown(attachView, xoff, 0);
    }

    /**
     * 构造问题列表View
     */
    private void createQuestionView(View attachView) {
        if (attachView == null) {
            return;
        }
        if (mContentView == null) {
            Context context = attachView.getContext();
            float itemHeight = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 40, context.getResources().getDisplayMetrics());
            float itemWidth = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 230, context.getResources().getDisplayMetrics());
            mContentView = new LinearLayout(context);
            mContentView.setBackgroundResource(R.drawable.bg_popup_window);
            mContentView.setOrientation(LinearLayout.VERTICAL);
            if (mArrayRes != null) {
                for (int i = 0; i < mArrayRes.length; i++) {
                    String question = mArrayRes[i];
                    TextView questionView = new TextView(context);
                    questionView.setOnClickListener(this);
                    questionView.setText(question);
                    questionView.setTextSize(14);
                    questionView.setTextColor(0xB2000000);
                    questionView.setGravity(Gravity.CENTER);
                    questionView.setBackgroundResource(R.drawable.common_item_selector);
                    questionView.setTag(i);
                    mListTxts[i] = questionView;
                    mContentView.addView(questionView, new LinearLayout.LayoutParams((int) itemWidth, (int) itemHeight));
                }
            }
        }
    }

    @Override
    public void onClick(View v) {
        if (mResponseTxt != null && v instanceof TextView) {
            mResponseTxt.setText(((TextView) v).getText());
            mCurrentPos = (int) v.getTag();
            mPopupWindow.dismiss();
        }
    }

    /**
     * 设置消失监控
     */
    public void setOnDismissListener(PopupWindow.OnDismissListener onDismissListener) {
        mOnDismissListener = onDismissListener;
    }

    public int getCurrentPos() {
        return mCurrentPos;
    }
}
