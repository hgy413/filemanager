package com.jb.filemanager.function.trash.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.SystemClock;
import android.util.AttributeSet;
import android.view.View;

import com.jb.filemanager.TheApplication;
import com.jb.filemanager.util.DrawUtils;



/**
 * Created by xiaoyu on 2017/2/8 15:09.
 */

public class CircleDotsProgress extends View {

    private static final float POINT_WIDTH = 5;
    private static final int POINT_RADIUS = DrawUtils.dip2px(2);
    private static final String TAG = "SCN_CircleDotsLoadingProgress";
    private Paint mPaint;

    private int mPointCount = 8;
    private float mDelayAngle;
    private float baseAnble = 0;
    private long mDuration = 100;
    private Thread mThread;

    private volatile boolean mIsRunAnimation = true;

    public void stop() {
        mIsRunAnimation = false;
    }

    public CircleDotsProgress(Context context) {
        super(context);
        initializeIngredient();
    }

    public CircleDotsProgress(Context context, AttributeSet attrs) {
        super(context, attrs);
        initializeIngredient();
    }

    public CircleDotsProgress(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initializeIngredient();
    }

    private void initializeIngredient() {
        //创建画笔
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);

        //设置画笔的相关属性
        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setAntiAlias(true);
        mPaint.setAlpha(255);
        mPaint.setColor(Color.WHITE);

        //求出偏移角度
        mDelayAngle = 360 * 1.0f / mPointCount;

        //开启动画
        AnimationsRunnable aniamaRunnable = new AnimationsRunnable();
        mThread = new Thread(aniamaRunnable);
        mThread.start();

    }
    @Override
    public void draw(Canvas canvas) {
        super.draw(canvas);
        //获取到圆心
        int a = getWidth() / 2;
        int b = getHeight() / 2;

        if (a == 0 || b == 0) {
            throw new IllegalArgumentException("请设置一下大小嘛！");
        }

        //这里的圆心是相对于外框的。比如说，我们用ImageView来承载
        //ImageView的大小为100dp*100dp，那么不管这个ImageView在个位置
        //圆心还是为(50dp,50dp)

        //这里循环把所有的点画出来
        for (int i = 0; i < mPointCount; i++) {

            //算出点的偏转角度
            float sweepAngle = i * mDelayAngle + baseAnble;
            //根据角度算出弧度
            double pointAngle = (Math.PI / 180 * (sweepAngle));

            //计算Cos角度值和Sin角度值
            double sinNum = Math.sin(pointAngle);
            double cosNum = Math.cos(pointAngle);

            //这里是因为呀，这个计算机的计算精确度，有时候呢，会过小，有时候会比实际的要大一点占。
            if (sinNum < -1.0) {
                sinNum = -1.0;
            } else if (sinNum > 1.0) {
                sinNum = 1.0;
            } else if (cosNum < -1.0) {
                cosNum = -1.0;
            } else if (cosNum > 1.0) {
                cosNum = 1.0;
            }

            //根据角度来算出每个点的位置
            //计算圆圈的位置
            //计算出坐标
            float x = (float) (a + cosNum * (a - POINT_RADIUS));
            float y = (float) (b + sinNum * (b - POINT_RADIUS));


            //根据象限来作一些偏移，否则都画到边上去了
            /*if (sinNum > 0) {
                y -= POINT_WIDTH;
            } else if (sinNum < 0) {
                y += POINT_WIDTH;
            }
            //
            if (cosNum > 0) {
                x -= POINT_WIDTH;
            } else if (cosNum < 0) {
                x += POINT_WIDTH;
            }*/

            //画出所有的圆点，此处应有掌声
            mPaint.setAlpha(255 / mPointCount * i);
            canvas.drawCircle(x, y, POINT_RADIUS, mPaint);

        }
        //叠加角度
        baseAnble += mDelayAngle;

        //归零
        if (baseAnble >= 360) {
            baseAnble = 0;
        }
    }

    private class AnimationsRunnable implements Runnable {

        @Override
        public void run() {
            while (mIsRunAnimation) {

                //睡一下
                SystemClock.sleep(mDuration);

                //跑在主线程上
                TheApplication.postRunOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        //画一次
                        invalidate();
                    }
                });
            }
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        mIsRunAnimation = false;
        super.onDetachedFromWindow();
    }

    public void setColor(int color) {
        mPaint.setColor(color);
        invalidate();
    }
}
