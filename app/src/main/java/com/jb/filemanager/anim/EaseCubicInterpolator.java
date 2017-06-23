package com.jb.filemanager.anim;

import android.graphics.PointF;
import android.view.animation.Interpolator;

import com.jb.filemanager.util.MathUtil;

/**
 * 缓动三次方曲线插值器.(基于三次方贝塞尔曲线)
 *
 * @author laojiale
 *
 */
@SuppressWarnings("unused")
public class EaseCubicInterpolator implements Interpolator {

    private final static int ACCURACY = 4096;
    private int mLastI = 0;
    private final PointF mControlPoint1 = new PointF();
    private final PointF mControlPoint2 = new PointF();

    /**
     * 设置中间两个控制点.<br>
     * 在线工具: http://cubic-bezier.com/<br>
     *
     * @param x1 x1
     * @param y1 y1
     * @param x2 x2
     * @param y2 y2
     */
    public EaseCubicInterpolator(float x1, float y1, float x2, float y2) {
        mControlPoint1.x = x1;
        mControlPoint1.y = y1;
        mControlPoint2.x = x2;
        mControlPoint2.y = y2;
    }

    @Override
    public float getInterpolation(float input) {
        float t = input;
        // 近似求解t的值[0,1]
        for (int i = mLastI; i < ACCURACY; i++) {
            t = 1.0f * i / ACCURACY;
            double x = MathUtil.cubicCurves(t, 0, mControlPoint1.x,
                    mControlPoint2.x, 1);
            if (x >= input) {
                mLastI = i;
                break;
            }
        }
        double value = MathUtil.cubicCurves(t, 0, mControlPoint1.y,
                mControlPoint2.y, 1);
        if (value > 0.999d) {
            value = 1;
            mLastI = 0;
        }
        return (float) value;
    }

}
