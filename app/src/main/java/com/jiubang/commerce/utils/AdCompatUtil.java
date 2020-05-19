package com.jiubang.commerce.utils;

import android.graphics.drawable.Drawable;
import android.net.Uri;
import java.lang.reflect.InvocationTargetException;

public class AdCompatUtil {
    public static Drawable getAdMobImageDrawable(Object obj) {
        if (obj == null) {
            return null;
        }
        try {
            return (Drawable) obj.getClass().getMethod("getDrawable", new Class[0]).invoke(obj, new Object[0]);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e2) {
            e2.printStackTrace();
        } catch (IllegalAccessException e3) {
            e3.printStackTrace();
        } catch (ClassCastException e4) {
            e4.printStackTrace();
        }
        return null;
    }

    public static Uri getAdMobImageUri(Object obj) {
        if (obj == null) {
            return null;
        }
        try {
            return (Uri) obj.getClass().getMethod("getUri", new Class[0]).invoke(obj, new Object[0]);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e2) {
            e2.printStackTrace();
        } catch (IllegalAccessException e3) {
            e3.printStackTrace();
        } catch (ClassCastException e4) {
            e4.printStackTrace();
        }
        return null;
    }
}
