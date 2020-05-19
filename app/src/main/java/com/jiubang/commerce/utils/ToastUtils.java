package com.jiubang.commerce.utils;

import android.content.Context;
import android.widget.Toast;

public class ToastUtils {
    public static void makeEventToast(Context context, String text, boolean isLongToast) {
        Toast toast;
        if (isLongToast) {
            toast = Toast.makeText(context, text, 1);
        } else {
            toast = Toast.makeText(context, text, 0);
        }
        if (toast != null) {
            toast.show();
        }
    }
}
