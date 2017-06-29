package com.jb.filemanager.util;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;

import com.jb.filemanager.Const;
import com.jb.ga0.commerce.util.http.GoHttpHeadUtil;

/**
 * 集中与NetworkSpeed应用本身相关的方法
 * 
 * @author lishen
 */
public class FileManagerUtil {

	private static int sChannel;

	/**
	 * 获取桌面渠道号的方法
	 * 
	 * @param ctx
	 * @return
	 */
	public static int getChannelN(Context ctx) {
		if (sChannel == 0) {
			try {
				ApplicationInfo appInfo = ctx.getPackageManager().getApplicationInfo(ctx.getPackageName(), PackageManager.GET_META_DATA);
				sChannel = appInfo.metaData.getInt("com.jb.filemanager.channel");
			} catch (Exception e) {
				sChannel = 200;
			}
		}
		return sChannel;
	}

    public static String getChannel(Context ctx) {
        String channel;
        try {
            channel = String.valueOf(getChannelN(ctx));
        } catch (Exception e) {
            channel = "200";
        }
        return channel;
    }
	
	/**
	 * 是否为官方包<br>
	 * @param ctx
	 * @return
	 */
	public static boolean isOfficial(Context ctx) {
		return "200".equals(getChannel(ctx));
	}

	/**
	 * 获取版本名
	 * 
	 * @return
	 */
	public static String getVersionName(Context context) {
		String name = "";
		PackageManager pm = context.getPackageManager();
		try {
			PackageInfo info = pm.getPackageInfo(context.getPackageName(), 0);
			name = info.versionName;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return name;
	}

	/**
	 * 获取版本号
	 * 
	 * @param context
	 * @return
	 */
	public static int getVersionCode(Context context) {
		int code = 0;
		PackageManager pm = context.getPackageManager();
		try {
			PackageInfo info = pm.getPackageInfo(context.getPackageName(), 0);
			code = info.versionCode;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return code;
	}

	/**
	 * 获取语言包请求的product id <li>
	 * 产品id请查看后台：http://pbasi18n01.rmz.gomo.com:8088/admin
	 * 
	 * @return
	 */
	public static String getLangProductID() {
		return "1004";
	}

	/**
	 * 是否已安装GO桌面
	 * 
	 * @param context
	 * @return
	 */
	public static boolean isGoLauncherInstalled(Context context) {
		return AppUtils.isAppExist(context, "com.gau.go.launcherex");
	}

	/**
	 * 是否已按装极桌面
	 * 
	 * @param context
	 * @return
	 */
	public static boolean isZeroLauncherInstalled(Context context) {
		return AppUtils.isAppExist(context, "com.zeroteam.zerolauncher");
	}

	/**
	 * 是否安装有FB客户端
	 * 
	 * @param context
	 * @return
	 */
	public static boolean isFacebookInstalled(Context context) {
		return AppUtils.isAppExist(context, Const.PACKAGE_FB)
				|| AppUtils.isAppExist(context, Const.PACKAGE_FB_LITE);
	}

	/**
	 * 是否安装了Google Play客户端
	 * 
	 * @param context
	 * @return
	 */
	public static boolean isGooglePlayInstalled(Context context) {
		return AppUtils.isAppExist(context, Const.GP_PACKAGE);
	}

	/**
	 * 是否为国内用户<br>
	 * 
	 * @param context
	 * @return
	 */
	public static boolean isCnUser(Context context) {
		String local = GoHttpHeadUtil.getLocal(context);
		return "cn".equalsIgnoreCase(local);
	}

	/**
	 * 时候安装了Next桌面
	 * @param context
	 * @return
     */
	public static boolean isNextLauncherInstalled(Context context) {
		return AppUtils.isAppExist(context, "com.gtp.nextlauncher");
	}

}
