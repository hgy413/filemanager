package com.jb.filemanager.function.applock.model.dao;

import android.content.Context;
import android.text.TextUtils;

import com.jb.filemanager.manager.spm.IPreferencesIds;
import com.jb.filemanager.manager.spm.SharedPreferencesManager;
import com.jb.filemanager.util.AesUtils;

import java.util.Locale;

/**
 * 密码管理
 * @author nieyh
 */
public class LockerSecureDao {

    /**
     * 安全表的数据库管理
     */
    private SharedPreferencesManager mSharedPreferencesManager;

    public LockerSecureDao(Context context) {
        mSharedPreferencesManager = SharedPreferencesManager.getInstance(context);
    }

    /**
     * 获取安全问题的题目
     */
    public String getLockerSecureQuestionName() {
        String name = mSharedPreferencesManager.getString(IPreferencesIds.KEY_APP_LOCK_QUESTION_NAME, "");
        try {
            name = AesUtils.decrypt(AesUtils.DEFAULT_KEY, name);
        } catch (Exception e) {
            e.printStackTrace();
            name = null;
        }
        return name;
	}

    /**
     * 获取安全问题的答案
     */
    public String getLockerSecureQuestionResult() {
        String result = mSharedPreferencesManager.getString(IPreferencesIds.KEY_APP_LOCK_QUESTION_RESULT, "");
        try {
            result = AesUtils.decrypt(AesUtils.DEFAULT_KEY, result);
        } catch (Exception e) {
            e.printStackTrace();
            result = null;
        }
        return result;
    }

    /**
     * 保存安全问题的题目
     */
    public void saveLockerSecureQuestionName(String name) {
        try {
            name = AesUtils.encrypt(AesUtils.DEFAULT_KEY, name);
        } catch (Exception e) {
            e.printStackTrace();
        }
        mSharedPreferencesManager.commitString(IPreferencesIds.KEY_APP_LOCK_QUESTION_NAME, name);
    }

    /**
     * 保存安全问题的答案
     */
    public void saveLockerSecureQuestionResult(String result) {
        try {
            result = AesUtils.encrypt(AesUtils.DEFAULT_KEY, result);
        } catch (Exception e) {
            e.printStackTrace();
        }
        mSharedPreferencesManager.commitString(IPreferencesIds.KEY_APP_LOCK_QUESTION_RESULT, result);
    }

    /**
     * 检测安全问题回答是否正确
     * */
    public boolean checkSecureQuestionAnswerState(String result) {
        boolean isCheck = false;
        String lastResult = getLockerSecureQuestionResult();
        if (TextUtils.isEmpty(result) || TextUtils.isEmpty(lastResult)) {
            return isCheck;
        }
        lastResult = lastResult.toLowerCase(Locale.US);
        result = result.toLowerCase(Locale.US);
        if (lastResult.equals(result)) {
            isCheck = true;
        }
        return isCheck;
    }

    /**
     * 获取应用锁密码
     */
    public String getLockerPassWord() {
        String password = mSharedPreferencesManager.getString(IPreferencesIds.KEY_APP_LOCK_PASSWORD, "");
        if (TextUtils.isEmpty(password)) {
            return null;
        }
        try {
            password = AesUtils.decrypt(AesUtils.DEFAULT_KEY, password);
        } catch (Exception e) {
            e.printStackTrace();
            password = null;
        }
        return password;
    }

    /**
     * 修改应用锁密码
     */
    public boolean modifyLockerPassword(String newPassword) {
        try {
            newPassword = AesUtils.encrypt(AesUtils.DEFAULT_KEY, newPassword);
        } catch (Exception e) {
            return false;
        }
        mSharedPreferencesManager.commitString(IPreferencesIds.KEY_APP_LOCK_PASSWORD, newPassword);
        return true;
    }

    /**
     * 是否是图案面膜 默认是 图案密码
     * */
    public boolean isPatternPsd() {
        return mSharedPreferencesManager.getBoolean(IPreferencesIds.KEY_APP_LOCK_PSD_TYPE, true);
    }

    /**
     * 存储图案密码
     * */
    public void modifylockerPsdType(boolean isPatternPsd) {
        mSharedPreferencesManager.commitBoolean(IPreferencesIds.KEY_APP_LOCK_PSD_TYPE, isPatternPsd);
    }
}
