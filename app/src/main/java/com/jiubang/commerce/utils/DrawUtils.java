package com.jiubang.commerce.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Paint;
import android.graphics.Point;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.ViewConfiguration;
import android.view.WindowManager;
import java.lang.reflect.Method;

public class DrawUtils {
    private static Class<?> sClass = null;
    public static float sDensity = 1.0f;
    public static int sDensityDpi;
    public static float sFontDensity;
    public static int sHeightPixels = -1;
    private static Method sMethodForHeight = null;
    private static Method sMethodForWidth = null;
    public static int sRealHeightPixels = -1;
    public static int sRealWidthPixels = -1;
    public static int sTouchSlop;
    public static float sVirtualDensity = -1.0f;
    public static float sVirtualDensityDpi = -1.0f;
    public static int sWidthPixels = -1;

    public static int dip2px(float dipVlue) {
        return (int) ((sDensity * dipVlue) + 0.5f);
    }

    public static int px2dip(float pxValue) {
        return (int) ((pxValue / sDensity) + 0.5f);
    }

    public static int sp2px(float spValue) {
        return (int) (sDensity * spValue);
    }

    public static int px2sp(float pxValue) {
        return (int) (pxValue / sDensity);
    }

    @SuppressLint({"NewApi"})
    public static synchronized void resetDensity(Context context) {
        synchronized (DrawUtils.class) {
            if (context != null) {
                if (context.getResources() != null) {
                    DisplayMetrics metrics = context.getResources().getDisplayMetrics();
                    sDensity = metrics.density;
                    sFontDensity = metrics.scaledDensity;
                    sDensityDpi = metrics.densityDpi;
                    try {
                        Display display = ((WindowManager) context.getSystemService("window")).getDefaultDisplay();
                        sWidthPixels = display.getWidth();
                        sHeightPixels = display.getHeight();
                        Class<?> clazz = Class.forName("android.view.Display");
                        Point realSize = new Point();
                        clazz.getMethod("getRealSize", new Class[]{Point.class}).invoke(display, new Object[]{realSize});
                        sRealWidthPixels = realSize.x;
                        sRealHeightPixels = realSize.y;
                    } catch (Throwable e) {
                        e.printStackTrace();
                        sRealWidthPixels = sWidthPixels;
                        sRealHeightPixels = sHeightPixels;
                    }
                    try {
                        ViewConfiguration configuration = ViewConfiguration.get(context);
                        if (configuration != null) {
                            sTouchSlop = configuration.getScaledTouchSlop();
                        }
                    } catch (Throwable e2) {
                        e2.printStackTrace();
                    }
                }
            }
        }
        return;
    }

    public static int getTabletScreenWidth(Context context) {
        int width = 0;
        if (context != null) {
            try {
                Display display = ((WindowManager) context.getSystemService("window")).getDefaultDisplay();
                if (sClass == null) {
                    sClass = Class.forName("android.view.Display");
                }
                if (sMethodForWidth == null) {
                    sMethodForWidth = sClass.getMethod("getRealWidth", new Class[0]);
                }
                width = ((Integer) sMethodForWidth.invoke(display, new Object[0])).intValue();
            } catch (Exception e) {
            }
        }
        if (width == 0) {
            return getRealWidth(context);
        }
        return width;
    }

    public static int getTabletScreenHeight(Context context) {
        int height = 0;
        if (context != null) {
            Display display = ((WindowManager) context.getSystemService("window")).getDefaultDisplay();
            try {
                if (sClass == null) {
                    sClass = Class.forName("android.view.Display");
                }
                if (sMethodForHeight == null) {
                    sMethodForHeight = sClass.getMethod("getRealHeight", new Class[0]);
                }
                height = ((Integer) sMethodForHeight.invoke(display, new Object[0])).intValue();
            } catch (Exception e) {
            }
        }
        if (height == 0) {
            return getRealHeight(context);
        }
        return height;
    }

    public static void setVirtualDensity(float density) {
        sVirtualDensity = density;
    }

    public static void setVirtualDensityDpi(float densityDpi) {
        sVirtualDensityDpi = densityDpi;
    }

    public static int getRealWidth(Context context) {
        if (sRealWidthPixels == -1 || sWidthPixels == -1) {
            resetDensity(context);
        }
        if (SystemUtils.IS_SDK_ABOVE_KITKAT) {
            return sRealWidthPixels;
        }
        return sWidthPixels;
    }

    public static int getRealHeight(Context context) {
        if (sRealHeightPixels == -1 || sHeightPixels == -1) {
            resetDensity(context);
        }
        if (SystemUtils.IS_SDK_ABOVE_KITKAT) {
            return sRealHeightPixels;
        }
        return sHeightPixels;
    }

    public static double getFontHeight(float fontSize) {
        Paint paint = new Paint();
        paint.setTextSize(fontSize);
        Paint.FontMetrics fm = paint.getFontMetrics();
        return (double) (((int) Math.ceil((double) (fm.descent - fm.top))) + dip2px(1.0f));
    }

    public static int getScreenWidth(Context context) {
        return context.getResources().getDisplayMetrics().widthPixels;
    }

    public static int getScreenHeight(Context context) {
        return context.getResources().getDisplayMetrics().heightPixels;
    }

    public static int getScreenDPI(Context context) {
        return context.getResources().getDisplayMetrics().densityDpi;
    }

    public static boolean isLandScreen(Context context) {
        if (getScreenWidth(context) > getScreenHeight(context)) {
            return true;
        }
        return false;
    }
}
