package com.jb.filemanager.function.privacy;

import android.content.Context;

import com.jb.filemanager.R;
import com.jb.filemanager.TheApplication;
import com.jb.filemanager.eventbus.AgreePrivacyEvent;
import com.jb.filemanager.manager.spm.IPreferencesIds;
import com.jb.filemanager.manager.spm.SharedPreferencesManager;
import com.jb.filemanager.util.AppUtils;

import org.greenrobot.eventbus.EventBus;

/**
 * 隐私声名相关的辅助方法<br>
 *
 * @author laojiale
 */
public class PrivacyHelper {

    /**
     * 是否同意协义<br>
     * 注意确保在全局数据加载完成后才可调用<br>
     *
     * @return result
     */
    public static boolean isAgreePrivacy() {
        final SharedPreferencesManager spm = SharedPreferencesManager.getInstance(TheApplication.getAppContext());
        return spm.getBooleanFromSP(IPreferencesIds.KEY_AGREE_PRIVACY, false);
    }

    /**
     * 同意隐私协义<br>
     */
    public static void agreePrivacy() {
        final SharedPreferencesManager spm = SharedPreferencesManager.getInstance(TheApplication.getAppContext());
        spm.commitBoolean(IPreferencesIds.KEY_AGREE_PRIVACY, true);
        EventBus.getDefault().post(new AgreePrivacyEvent());
    }

    /**
     * 是否参与用户体验计划<br>
     *
     * @return result
     */
    public static boolean isJoinUepPlan() {
        final SharedPreferencesManager spm = SharedPreferencesManager.getInstance(TheApplication.getAppContext());
        return spm.getBoolean(IPreferencesIds.KEY_JOIN_USER_EXPERIENCE_PROGRAM, false);
    }

    /**
     * 设置是否参与用户体验计划<br>
     */
    public static void joinUepPlan(boolean isJoin) {
        final SharedPreferencesManager spm = SharedPreferencesManager.getInstance(TheApplication.getAppContext());
        spm.commitBoolean(IPreferencesIds.KEY_JOIN_USER_EXPERIENCE_PROGRAM, isJoin);
    }

    /**
     * 跳转到隐私说明页<br>
     *
     * @param context context
     */
    public static void gotoPrivacyInfoPage(Context context) {
        final String url = context.getResources().getString(R.string.about_law_url);
        AppUtils.openBrowser(context, url);
    }

    /**
     * 跳转到用户体验计划说明页<br>
     *
     * @param context context
     */
    public static void gotoUepInfoPage(Context context) {
        final String url = context.getResources().getString(R.string.privacy_uep_plan_info_page);
        AppUtils.openBrowser(context, url);
    }
}
