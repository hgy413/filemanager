package com.jb.filemanager.function.feedback;

import android.content.Intent;
import android.net.Uri;
import android.view.View;

import com.jb.filemanager.R;
import com.jb.filemanager.TheApplication;
import com.jb.filemanager.function.rate.RateManager;
import com.jb.filemanager.manager.spm.IPreferencesIds;
import com.jb.filemanager.manager.spm.SharedPreferencesManager;
import com.jb.filemanager.util.NetworkUtil;
import com.jb.filemanager.util.device.DeviceUtil;

/**
 * Created by bill wang on 2017/6/21.
 *
 */

class FeedbackSupport implements FeedbackContract.Support {

    @Override
    public String[] getProblemArray() {
        return TheApplication.getAppContext().getResources().getStringArray(R.array.feedback_common_question_list);
    }

    @Override
    public boolean isNetWork() {
        return NetworkUtil.isNetworkOK(TheApplication.getAppContext());
    }

    @Override
    public void addRateFactor() {
        RateManager.getsInstance().commitRateSuccess();
    }

    @Override
    public String getFeedbackDeviceInfo(String content) {
        return DeviceUtil.getFeedbackDeviceInfo(TheApplication.getAppContext(), content);
    }

    @Override
    public String getFeedbackContent() {
        return TheApplication.getAppContext().getResources().getString(R.string.feedback_content);
    }

    @Override
    public Intent newFeedBackIntent(String text, String titleContent, String tos) {
        Intent emailIntent = new Intent(Intent.ACTION_SEND);
        emailIntent.setType("message/rfc822");
        emailIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        emailIntent.putExtra(Intent.EXTRA_TEXT, text);
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, titleContent);
        Uri uri = Uri.parse("mailto:" + tos);
        emailIntent.setAction(Intent.ACTION_SENDTO);
        emailIntent.setData(uri);
        return emailIntent;
    }

    @Override
    public boolean isNeedDismissWarnLayout() {
        return SharedPreferencesManager.getInstance(TheApplication.getAppContext()).getBoolean(IPreferencesIds.KEY_FEEDBACK_WARN_TIP_SHOWED, false);
    }

    @Override
    public boolean isNeedDismissWarn2Layout() {
        return SharedPreferencesManager.getInstance(TheApplication.getAppContext()).getBoolean(IPreferencesIds.KEY_FEEDBACK_WARN_TIP2_SHOWED, false);
    }

    @Override
    public void setWarnLayoutShowed() {
        SharedPreferencesManager.getInstance(TheApplication.getAppContext()).commitBoolean(IPreferencesIds.KEY_FEEDBACK_WARN_TIP_SHOWED, true);
    }

    @Override
    public void setWarn2LayoutShowed() {
        SharedPreferencesManager.getInstance(TheApplication.getAppContext()).commitBoolean(IPreferencesIds.KEY_FEEDBACK_WARN_TIP2_SHOWED, true);
    }
}
