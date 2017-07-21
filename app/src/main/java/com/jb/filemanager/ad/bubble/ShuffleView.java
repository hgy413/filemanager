package com.jb.filemanager.ad.bubble;

import android.animation.Animator;
import android.animation.TypeEvaluator;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.PixelFormat;
import android.graphics.PointF;
import android.graphics.PorterDuff;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.jb.filemanager.R;
import com.jb.filemanager.util.AppUtils;
import com.jb.filemanager.util.DrawUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by bill wang on 2017/7/21.
 * 气球view 可调用start()方法放炮 调用stop()方法停止 调用restart()方法继续放炮
 */

public class ShuffleView extends RelativeLayout {

    private static final int CREATE_BALLOON_INTERVAL = 100;
    private static final float ANIM_DURATION = 3f;
    protected static final int MSG_CREATE_BALLOON = 0;

    private int mScreenWidth;
    private int mScreenHeight;

    private Drawable[] mBalloon;

    private Context mContext;

    private List<ValueAnimator> mAnimatorPool;

    private boolean mIsContinue = true;

    private IShuffleListener mShuffleListener;

    // ===================== 随机数的范围 =======================
    private float mMinStartX;
    private float mMaxStartX;

    private float mMinStartY;
    private float mMaxStartY;

    private float mMinVx;
    private float mMaxVx;

    private float mMinVy;
    private float mMaxVy;

    private float mMinAccerator;
    private float mMaxAccerator;

    public ShuffleView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public ShuffleView(Context context) {
        super(context);
        init(context);
    }

    public void setShuffleListener(IShuffleListener listener) {
        mShuffleListener = listener;
    }

    /**
     * 开始气泡动画
     */
    public void start() {
        new Thread(mCreateBalloonRunnable).start();
    }

    /**
     * 结束气泡动画
     */
    @SuppressLint("NewApi")
    public void stop() {
        mIsContinue = false;
        mHandler.postDelayed(new Runnable() {

            @Override
            public void run() {
                for (ValueAnimator animator : mAnimatorPool) {

                    if (animator.isRunning()) {
                        animator.end();
                    }
                }
                mAnimatorPool.clear();
            }
        }, 100);
    }

    public void detach() {
        stop();
        ViewGroup vg = (ViewGroup)getParent();
        vg.removeView(this);
    }

    /**
     * 重新启动(第一次调start后,后面想继续放气球,须调此方法)
     */
    public void restart() {
        mIsContinue = true;
        start();
    }

    private void init(Context context) {
        mContext = context;
        // 初始化DrawKits
        Drawable balloon = getResources().getDrawable(
                R.drawable.img_balloon);
        mBalloon = new Drawable[4];
        mBalloon[0] = balloon;
        mBalloon[0].setColorFilter(0xffff007e, PorterDuff.Mode.SRC_IN);
        mBalloon[1] = zoomDrawable(context, balloon, DrawUtils.dip2px(64),
                DrawUtils.dip2px(127));
        mBalloon[1].setColorFilter(0xff00e4ff, PorterDuff.Mode.SRC_IN);
        mBalloon[2] = zoomDrawable(context, balloon, DrawUtils.dip2px(72),
                DrawUtils.dip2px(143));
        mBalloon[2].setColorFilter(0xff00ff9c, PorterDuff.Mode.SRC_IN);
        mBalloon[3] = zoomDrawable(context, balloon, DrawUtils.dip2px(68),
                DrawUtils.dip2px(135));
        mBalloon[3].setColorFilter(0xfffff000, PorterDuff.Mode.SRC_IN);
        mScreenWidth = DrawUtils.getScreenWidth();
        mScreenHeight = DrawUtils.getScreenHeight();

        mMinStartX = DrawUtils.dip2px(98);
        mMaxStartX = mMinStartX;

        mMinStartY = mScreenHeight / 4f;
        mMaxStartY = mScreenHeight * 0.8f;

        mMinVx = (mScreenWidth + mMaxStartX) / ANIM_DURATION;
        mMaxVx = mScreenWidth * 1.2f;

        mMinVy = 0;
        mMaxVy = mScreenHeight / 3f;

        mMinAccerator = mScreenHeight / 2;
        mMaxAccerator = mScreenHeight / 1;

        mAnimatorPool = new ArrayList<ValueAnimator>();
        // 透明背景
        setBackgroundColor(Color.TRANSPARENT);
        // 屏蔽触摸事件
        setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return v.onTouchEvent(event) || true;
            }
        });
    }



    /**
     * 每隔0.5秒生成一个气球
     */
    private Runnable mCreateBalloonRunnable = new Runnable() {

        @Override
        public void run() {

            while (mIsContinue) {
                try {
                    Thread.sleep(CREATE_BALLOON_INTERVAL);
                    Message msg = mHandler.obtainMessage();
                    msg.what = MSG_CREATE_BALLOON;
                    mHandler.sendMessage(msg);
                } catch (Exception e) {
                }
            }

            // 如果mStopFlag为false, 说明广告加载不成功
        }
    };

    @SuppressLint("HandlerLeak")
    private Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            if (msg.what == MSG_CREATE_BALLOON) {
                createBalloon();
            }
        };
    };

    /**
     * <br>
     * 功能简述: 创造一个由随机数生成的气球 <br>
     * 功能详细描述: <br>
     * 注意:
     */
    @SuppressWarnings("deprecation")
    private void createBalloon() {
        // 取反,从屏幕左侧进入
        float startX = -getRandomValue(mMinStartX, mMaxStartX);
        float startY = getRandomValue(mMinStartY, mMaxStartY);
        float vx = getRandomValue(mMinVx, mMaxVx);
        float vy = getRandomValue(mMinVy, mMaxVy);
        // 取反,加速度向上
        float accerator = -getRandomValue(mMinAccerator, mMaxAccerator);
        BalloonParams balloonParams = new BalloonParams(startX, startY, vx, vy,
                accerator);

        ImageView view = new ImageView(mContext);
        // view.setBackgroundColor(0xffffaa);
        view.setBackgroundDrawable(mBalloon[(int) getRandomValue(0, 3)]);
        startAnim(view, balloonParams);
    }

    /**
     * <br>
     * 功能简述: 生成随机数 <br>
     * 功能详细描述: <br>
     * 注意:
     *
     * @param min
     * @param max
     */
    private float getRandomValue(float min, float max) {
        return min + (float) (Math.random() * (max - min + 1));
    }

    /**
     * <br>
     * 功能简述: 根据参数对气球开始动画 <br>
     * 功能详细描述: <br>
     * 注意:
     *
     * @param view
     * @param params
     */
    @SuppressLint("NewApi")
    private void startAnim(final View view, final BalloonParams params) {
        LayoutParams lp = new LayoutParams(
                LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);

        // 防止在屏幕上出现
        lp.leftMargin = -mScreenWidth / 2;
        addView(view, lp);
        final ValueAnimator valueAnimator = new ValueAnimator();
        mAnimatorPool.add(valueAnimator);
        valueAnimator.setDuration((long) (ANIM_DURATION * 1000));
        valueAnimator
                .setObjectValues(new PointF(params.mStartX, params.mStartY));
        valueAnimator.setInterpolator(new LinearInterpolator());
        valueAnimator.setEvaluator(new TypeEvaluator<PointF>() {
            @Override
            public PointF evaluate(float fraction, PointF startValue,
                                   PointF endValue) {
                PointF point = new PointF();
				/*
				 * fraction = t / duration -> t = fraction * duration
				 */
                float t = fraction * ANIM_DURATION;
                point.x = params.mStartX + params.mVx * t;
                point.y = params.mStartY + params.mVy * t + 0.5f
                        * params.mAccerator * t * t;
                return point;
            }
        });

        valueAnimator.addListener(new Animator.AnimatorListener() {

            @Override
            public void onAnimationStart(Animator animation) {
            }

            @Override
            public void onAnimationRepeat(Animator animation) {
            }

            @Override
            public void onAnimationEnd(Animator animation) {
            }

            @Override
            public void onAnimationCancel(Animator animation) {
            }
        });

        valueAnimator.start();

        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                PointF point = (PointF) animation.getAnimatedValue();
                view.setX(point.x);
                view.setY(point.y);
            }
        });
    }

    private BitmapDrawable zoomDrawable(Context context, Drawable drawable,
                                        int w, int h) {
        if (drawable != null) {
            int width = drawable.getIntrinsicWidth();
            int height = drawable.getIntrinsicHeight();
            Bitmap oldbmp = null;
            // drawable 转换成 bitmap
            if (drawable instanceof BitmapDrawable) {
                // 如果传入的drawable是BitmapDrawable,就不必要生成新的bitmap
                oldbmp = ((BitmapDrawable) drawable).getBitmap();
            } else {

                oldbmp = createBitmapFromDrawable(drawable);
            }

            Matrix matrix = new Matrix(); // 创建操作图片用的 Matrix 对象
            float scaleWidth = (float) w / width; // 计算缩放比例
            float scaleHeight = (float) h / height;
            matrix.postScale(scaleWidth, scaleHeight); // 设置缩放比例
            Bitmap newbmp = Bitmap.createBitmap(oldbmp, 0, 0, width, height,
                    matrix, true); // 建立新的 bitmap ，其内容是对原 bitmap 的缩放后的图
            matrix = null;
            return new BitmapDrawable(context.getResources(), newbmp); // 把
            // bitmap
            // 转换成
            // drawable
            // 并返回
        }
        return null;
    }

    private Bitmap createBitmapFromDrawable(final Drawable drawable) {
        if (drawable == null) {
            return null;
        }

        Bitmap bitmap = null;
        final int intrinsicWidth = drawable.getIntrinsicWidth();
        final int intrinsicHeight = drawable.getIntrinsicHeight();

        try {
            Bitmap.Config config = drawable.getOpacity() != PixelFormat.OPAQUE ? Bitmap.Config.ARGB_8888
                    : Bitmap.Config.RGB_565;
            bitmap = Bitmap.createBitmap(intrinsicWidth, intrinsicHeight,
                    config);
        } catch (OutOfMemoryError e) {
            return null;
        }

        Canvas canvas = new Canvas(bitmap);
        // canvas.setBitmap(bitmap);
        drawable.setBounds(0, 0, intrinsicWidth, intrinsicHeight);
        drawable.draw(canvas);
        canvas = null;
        return bitmap;
    }

    /**
     * 气泡参数
     *
     * @author yuanweinan
     *
     */
    private class BalloonParams {

        /**
         * 初始的x值
         */
        public float mStartX;
        /**
         * 初始的y值
         */
        public float mStartY;
        /**
         * 球的x轴速度
         */
        public float mVx;
        /**
         * 球的y轴速度
         */
        public float mVy;
        /**
         * 球的加速度
         */
        public float mAccerator;

        public BalloonParams(float startX, float startY, float vx, float vy,
                             float accerator) {
            mStartX = startX;
            mStartY = startY;
            mVx = vx;
            mVy = vy;
            mAccerator = accerator;
        }
    }

    /**
     * 监听回调
     * @author yuanweinan
     *
     */
    public interface IShuffleListener {
        void onStop();
    }
}