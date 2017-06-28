package com.jb.filemanager.function.applock.view;

import android.animation.ObjectAnimator;
import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.PorterDuff;
import android.os.Build;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.CycleInterpolator;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.jb.filemanager.R;

/**
 * Created by nieyh on 2017/2/27.
 */

public class NumberLockerView extends FrameLayout implements View.OnClickListener {

    public final static int DARK_STYLE = 1;

    public final static int LIGHT_STYLE = 2;

    private int mStyle = DARK_STYLE;

    private final int MAX_PROGRESS = 4;

    public NumberLockerView(Context context) {
        super(context);
    }

    private final int MIN_PROGRESS = 0;

    /**
     * 顶部代表进度的四个圈
     */
    private ImageView mNumberProgrees[];

    /**
     * 1-9、0数字 最后一个为 0
     */
    private RippleView mLockerNumber[];

    /**
     * 右下角的删除按钮
     */
    private ImageView mDelete;

    /**
     * 数字进度整体布局
     */
    private View mNumberProgressLayout;

    /**
     * 结果值
     */
    private String mResult[] = new String[4];

    private OnNumberListener mOnNumberListener;

    //当前进行的位置
    private int mCurrentProgress = MIN_PROGRESS;

    private ObjectAnimator mShakeAnimator;

    public NumberLockerView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public NumberLockerView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public NumberLockerView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        View root = LayoutInflater.from(getContext()).inflate(R.layout.layout_num_lock, this, true);
        mNumberProgressLayout = root.findViewById(R.id.layout_cycle);
        //四个进度圈
        mNumberProgrees = new ImageView[4];
        mNumberProgrees[0] = (ImageView) root.findViewById(R.id.num_point_1);
        mNumberProgrees[1] = (ImageView) root.findViewById(R.id.num_point_2);
        mNumberProgrees[2] = (ImageView) root.findViewById(R.id.num_point_3);
        mNumberProgrees[3] = (ImageView) root.findViewById(R.id.num_point_4);
        for (ImageView numberProgree : mNumberProgrees) {
            numberProgree.setSelected(false);
        }
        //所有数字
        mLockerNumber = new RippleView[10];
        mLockerNumber[0] = (RippleView) root.findViewById(R.id.number_1);
        mLockerNumber[1] = (RippleView) root.findViewById(R.id.number_2);
        mLockerNumber[2] = (RippleView) root.findViewById(R.id.number_3);
        mLockerNumber[3] = (RippleView) root.findViewById(R.id.number_4);
        mLockerNumber[4] = (RippleView) root.findViewById(R.id.number_5);
        mLockerNumber[5] = (RippleView) root.findViewById(R.id.number_6);
        mLockerNumber[6] = (RippleView) root.findViewById(R.id.number_7);
        mLockerNumber[7] = (RippleView) root.findViewById(R.id.number_8);
        mLockerNumber[8] = (RippleView) root.findViewById(R.id.number_9);
        mLockerNumber[9] = (RippleView) root.findViewById(R.id.number_0);
        for (RippleView textView : mLockerNumber) {
            textView.setOnClickListener(this);
        }
        //删除按钮
        mDelete = (ImageView) root.findViewById(R.id.number_del);
        mDelete.setOnClickListener(this);
        initColorStyle();
    }

    /**
     * 初始化风格
     */
    private void initColorStyle() {
        switch (mStyle) {
            case DARK_STYLE:
                if (mNumberProgrees != null) {
                    for (ImageView numberProgree : mNumberProgrees) {
                        numberProgree.setBackgroundResource(R.drawable.selector_num_lock);
                    }
                }
                if (mLockerNumber != null) {
                    for (TextView textView : mLockerNumber) {
                        textView.setTextColor(0xff0d96fc);
                    }
                }
                mDelete.setColorFilter(0xff0d96fc, PorterDuff.Mode.SRC_ATOP);
                break;
            case LIGHT_STYLE:
                if (mNumberProgrees != null) {
                    for (ImageView numberProgree : mNumberProgrees) {
                        numberProgree.setBackgroundResource(R.drawable.selector_num_lock_light);
                    }
                }
                if (mLockerNumber != null) {
                    for (TextView textView : mLockerNumber) {
                        textView.setTextColor(0xffffffff);
                    }
                }
                mDelete.setColorFilter(0xffffffff, PorterDuff.Mode.SRC_ATOP);
                break;
        }
    }

    @Override
    public void onClick(View v) {
        if (v == mDelete) {
            //点击删除ba
            mCurrentProgress--;
            if (mCurrentProgress < MIN_PROGRESS) {
                mCurrentProgress = MIN_PROGRESS;
            }
            if (mCurrentProgress == MIN_PROGRESS) {
                if (mOnNumberListener != null) {
                    mOnNumberListener.onNumberAllDeleted();
                }
            }
            handleProgressChg();
        } else {
            if (mCurrentProgress < MAX_PROGRESS) {
                //其他的点击
                String number = ((TextView) v).getText().toString().trim();
                if (mOnNumberListener != null) {
                    mOnNumberListener.onNumberInput(number);
                }
                mResult[mCurrentProgress] = number;
                mCurrentProgress++;
                handleProgressChg();
            }
        }
    }

    /**
     * 重新绘制顶部进度
     */
    private void handleProgressChg() {
        if (mNumberProgrees != null) {
            for (int i = 0; i < mNumberProgrees.length; i++) {
                if (i < mCurrentProgress) {
                    mNumberProgrees[i].setSelected(true);
                } else {
                    mNumberProgrees[i].setSelected(false);
                }
            }
            if (mCurrentProgress == MAX_PROGRESS) {
                if (mOnNumberListener != null) {
                    mOnNumberListener.onNumberFinish(mResult);
                }
                mNumberProgressLayout.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        clean();
                    }
                }, 500);
            }
        }
    }


    public void setOnNumberListener(OnNumberListener onNumberListener) {
        this.mOnNumberListener = onNumberListener;
    }

    /**
     * 当文字书写完毕
     */
    public interface OnNumberListener {
        void onNumberFinish(String[] numbers);

        void onNumberInput(String number);

        void onNumberAllDeleted();
    }

    /**
     * 摇晃数字布局
     */
    public void shakeNumberLayout() {
        if (mShakeAnimator == null) {
            mShakeAnimator = ObjectAnimator.ofFloat(mNumberProgressLayout, "translationX", 5);
            mShakeAnimator.setInterpolator(new CycleInterpolator(30));
            mShakeAnimator.setDuration(400);
        }
        mShakeAnimator.start();
    }

    /**
     * 清理记录
     */
    public void clean() {
        mResult = new String[4];
        mCurrentProgress = MIN_PROGRESS;
        handleProgressChg();
    }

    public void release() {
        if (mShakeAnimator != null) {
            mShakeAnimator.cancel();
        }
    }

    /**
     * 设置颜色风格
     * <ol>
     * <li>{@link #DARK_STYLE}</li>
     * <li>{@link #LIGHT_STYLE}</li>
     * </ol>
     */
    public void setColorStyle(int style) {
        mStyle = style;
        if (mNumberProgrees != null && mLockerNumber != null) {
            initColorStyle();
        }
    }
}
