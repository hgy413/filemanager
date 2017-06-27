package com.jb.filemanager.util;

import android.content.res.Resources;
import android.view.Window;
import android.view.WindowManager;

/**
 * 窗口工具
 *
 * @author chenbenbin
 */
public class WindowUtil {

    public static int getStatusBarHeight() {
        Resources resources = Resources.getSystem();
        int resourceId = Resources.getSystem().getIdentifier(
                "status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            return resources.getDimensionPixelSize(resourceId);
        }
        return 0;
    }

    public static int getNavigationBarHeight() {
        Resources resources = Resources.getSystem();
        int resourceId = resources.getIdentifier("navigation_bar_height",
                "dimen", "android");
        if (resourceId > 0) {
            return resources.getDimensionPixelSize(resourceId);
        }
        return 0;
    }
    
    /**
     * 设置窗口是否常亮<br>
     * @param window
     * @param keep
     */
	public static void keepScreenOn(Window window, boolean keep) {
		if (keep) {
			// 开启屏幕常亮
			window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		} else {
			// 取消屏幕常亮
			window.clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		}
	}
	
}
