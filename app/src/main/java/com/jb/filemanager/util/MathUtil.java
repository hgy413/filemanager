package com.jb.filemanager.util;

import java.util.Random;

/**
 * 数学工具集
 */
@SuppressWarnings("unused")
public class MathUtil {

    public final static Random RANDOM = new Random(System.currentTimeMillis());

    /**
     * 计算殴几里得几何平面内两点间的距离.<br>
     *
     * @param x1 x1
     * @param y1 y1
     * @param x2 x2
     * @param y2 y2
     * @return result
     */
    public static double distance(double x1, double y1, double x2, double y2) {
        return Math.sqrt(Math.pow(x2 - x1, 2) + Math.pow(y2 - y1, 2));
    }

    /**
     * 获取一组数中的最大值
     *
     * @param values value array
     * @return result
     */
    public static double max(double... values) {
        double max = values[0];
        for (double d : values) {
            if (d > max) {
                max = d;
            }
        }
        return max;
    }

    /**
     * 获取一组数中的最小值
     *
     * @param values value array
     * @return result
     */
    public static double min(double... values) {
        double min = values[0];
        for (double d : values) {
            if (d < min) {
                min = d;
            }
        }
        return min;
    }

    /**
     * 计算两点定义的矢量的角度.(以3点钟方向为0度,顺时序方向旋转为正角度)<br>
     *
     * @param startX start X
     * @param startY start Y
     * @param endX end X
     * @param endY end Y
     * @return 弧度制的角度
     */
    public static double radians(double startX, double startY, double endX,
                                 double endY) {
        double c = distance(startX, startY, endX, endY); // 斜边
        return Math.asin(-(endY - startY) / c);
    }

    /**
     * 同{@link #radians(double, double, double, double)}
     *
     * @param startX start X
     * @param startY start Y
     * @param endX end X
     * @param endY end Y
     * @return 以度为单位的角度
     */
    public static double degree(double startX, double startY, double endX,
                                double endY) {
        // Loger.d("LJL", "degrees: " + degrees);
        return Math.toDegrees(radians(startX, startY, endX, endY));
    }

    /**
     * 求三次贝塞尔曲线(四个控制点)一个点某个维度的值.<br>
     * 参考资料: <em> http://devmag.org.za/2011/04/05/bzier-curves-a-tutorial/ </em>
     *
     * @param t
     *            取值[0, 1]
     * @param value0 value 0
     * @param value1 value 1
     * @param value2 value 2
     * @param value3 value 3
     * @return result
     */
    public static double cubicCurves(double t, double value0, double value1,
                                     double value2, double value3) {
        double value;
        double u = 1 - t;
        double tt = t * t;
        double uu = u * u;
        double uuu = uu * u;
        double ttt = tt * t;

        value = uuu * value0;
        value += 3 * uu * t * value1;
        value += 3 * u * tt * value2;
        value += ttt * value3;
        return value;
    }

    /**
     * 求解一元一次方程的参数A,B<br>
     * y(x) = Ax + B<br>
     *
     * @return double[]{A, B}
     */
    public static double[] getLinearEquationAB(double x1, double y1, double x2,
                                               double y2) {
        double a, b;
        a = (y1 - y2) / (x1 - x2);
        b = y1 - a * x1;
        return new double[] { a, b };
    }

    /**
     * 求解一元一次方程的X值<br>
     * y(x) = Ax + B<br>
     *
     * @param x1 x1
     * @param y1 y1
     * @param x2 x2
     * @param y2 y2
     * @param y y
     * @return result
     */
    public static double getLinearEquationX(double x1, double y1, double x2,
                                            double y2, double y) {
        double[] ab = getLinearEquationAB(x1, y1, x2, y2);
        double a, b;
        a = ab[0];
        b = ab[1];
        return (y - b) / a;
    }

    /**
     * 求解一元一次方程的Y值<br>
     * y(x) = Ax + B<br>
     *
     * @param x1 x1
     * @param y1 y1
     * @param x2 x2
     * @param y2 y2
     * @param x x
     * @return result
     */
    public static double getLinearEquationY(double x1, double y1, double x2,
                                            double y2, double x) {
        double[] ab = getLinearEquationAB(x1, y1, x2, y2);
        double a, b;
        a = ab[0];
        b = ab[1];
        return a * x + b;
    }

    /**
     * 求解一元一次方程的B值<br>
     * y(x) = Ax + B<br>
     *
     * @param x x
     * @param y y
     * @param a a
     * @return result
     */
    public static double getLinearEquationB(double x, double y, double a) {
        return y - a * x;
    }

    /**
     * 求解一元一次方程的B值<br>
     * y(x) = Ax + B<br>
     *
     * @param x x
     * @param y y
     * @param b b
     * @return result
     */
    public static double getLinearEquationA(double x, double y, double b) {
        return (y - b) / x;
    }

    /**
     * 求解一元一次方程的Y值<br>
     * y(x) = Ax + B<br>
     *
     * @param a a
     * @param b b
     * @param x x
     * @return result
     */
    public static double getLinearEquationY(double a, double b, double x) {
        return a * x + b;
    }

    /**
     * 求解一元一次方程的X值<br>
     * y(x) = Ax + B<br>
     *
     * @param a a
     * @param b b
     * @param y y
     * @return result
     */
    public static double getLinearEquationX(double a, double b, double y) {
        return (y - b) / a;
    }

}