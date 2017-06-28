package com.jb.filemanager.function.applock.dialog;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.support.annotation.IntDef;
import android.support.v4.content.ContextCompat;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.jb.filemanager.R;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Created by nieyh on 2017/1/1.
 * 描述：<br>
 * 此对话框展示找回密码时候需要的问题列表 <br>
 * 使用说明：<br>
 * {@link #QuestionListDialog(TextView, int)} 传入需要改变的TextView，以及默认选择的文字常量 <br>
 * <ol>
 * <li>{@link #WEN_BIRTHDAY} 生日时间</li>
 * <li>{@link #WCH_MOVIE_LIKE} 最喜欢的电影</li>
 * <li>{@link #WHT_NAME} 你的名字</li>
 * </ol>
 */

public class QuestionListDialog implements View.OnClickListener {

    @IntDef({WCH_MOVIE_LIKE, WHT_NAME, WEN_BIRTHDAY})
    @Retention(RetentionPolicy.SOURCE)
    public @interface QuestionType {
    }

    public static final int WCH_MOVIE_LIKE = 0;
    public static final int WHT_NAME = 1;
    public static final int WEN_BIRTHDAY = 2;

    private LinearLayout mContentView;
    private TextView[] mQuestionTxts = new TextView[3];
    private View[] mLines = new View[2];
    private TextView mResponseTxt;
    private int mCurrentChoiceQuestion;
    private PopupWindow mPopupWindow;
    private PopupWindow.OnDismissListener mOnDismissListener;

    public QuestionListDialog(TextView responseTxtView, @QuestionType int defaultValue) {
        mCurrentChoiceQuestion = defaultValue;
        mResponseTxt = responseTxtView;
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
        // 设置好参数之后再show
        mPopupWindow.showAsDropDown(attachView);
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
            float itemHeight = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 50, context.getResources().getDisplayMetrics());
            float lineHeight = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 0.5f, context.getResources().getDisplayMetrics());
            float txtSize = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 13, context.getResources().getDisplayMetrics());
            String[] questions = context.getResources().getStringArray(R.array.pupup_window_reset_psd_questions);
            mContentView = new LinearLayout(context);
            mContentView.setOrientation(LinearLayout.VERTICAL);
            if (questions != null) {
                for (int i = 0; i < questions.length; i++) {
                    String question = questions[i];
                    TextView questionView = new TextView(context);
                    questionView.setOnClickListener(this);
                    questionView.setText(question);
//                    ColorStateList csl = ContextCompat.getColorStateList(context, R.color.question_drapdown_color_selector);
//                    if (csl != null) {
//                        questionView.setTextColor(csl);
//                    }
                    questionView.setTextSize(TypedValue.COMPLEX_UNIT_PX, txtSize);
                    questionView.setGravity(Gravity.CENTER);
                    questionView.setBackgroundColor(0xffffffff);
                    questionView.setTag(i);
                    if (i == mCurrentChoiceQuestion) {
                        questionView.setVisibility(View.GONE);
                    }
                    mQuestionTxts[i] = questionView;
                    mContentView.addView(questionView, new LinearLayout.LayoutParams(attachView.getWidth(), (int) itemHeight));
                    if (i != questions.length - 1) {
                        View line = new View(context);
                        line.setBackgroundColor(0xffdddddd);
                        if (i == mCurrentChoiceQuestion) {
                            line.setVisibility(View.GONE);
                        }
                        mLines[i] = line;
                        mContentView.addView(line, new LinearLayout.LayoutParams(attachView.getWidth(), (int) lineHeight));
                    }
                }
            }
        } else {
            for (int i = 0; i < mQuestionTxts.length; i++) {
                TextView tempView = mQuestionTxts[i];
                if (i == mCurrentChoiceQuestion) {
                    tempView.setVisibility(View.GONE);
                } else {
                    tempView.setVisibility(View.VISIBLE);
                }
            }

            for (int i = 0; i < mLines.length; i++) {
                View tempView = mLines[i];
                if (i == mCurrentChoiceQuestion) {
                    tempView.setVisibility(View.GONE);
                } else {
                    tempView.setVisibility(View.VISIBLE);
                }
            }
        }
    }

    @Override
    public void onClick(View v) {
        if (mResponseTxt != null && v instanceof TextView) {
            mResponseTxt.setText(((TextView) v).getText());
            mCurrentChoiceQuestion = (int) v.getTag();
            mPopupWindow.dismiss();
        }
    }

    /**
     * 设置消失监控
     */
    public void setOnDismissListener(PopupWindow.OnDismissListener onDismissListener) {
        mOnDismissListener = onDismissListener;
    }
}
