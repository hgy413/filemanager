package com.jb.filemanager.function.feedback;

import android.content.Intent;
import android.net.Uri;

import com.jb.filemanager.R;
import com.jb.filemanager.TheApplication;
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
    public boolean isNetWork() {
        return NetworkUtil.isNetworkOK(TheApplication.getAppContext());
    }

    @Override
    public String getFeedBackCommon() {
        return TheApplication.getAppContext().getResources().getString(R.string.feedback_common);
    }

    @Override
    public String getFeedBackSuggestion() {
        return TheApplication.getAppContext().getResources().getString(R.string.feedback_suggestion);
    }

    @Override
    public String getFeedBackProblem() {
        return TheApplication.getAppContext().getResources().getString(R.string.feedback_problem);
    }

    @Override
    public String getFeedBackForceInstall() {
        return TheApplication.getAppContext().getResources().getString(R.string.feedback_force_install);
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
    public void setWarnLayoutShowed() {
        SharedPreferencesManager.getInstance(TheApplication.getAppContext()).commitBoolean(IPreferencesIds.KEY_FEEDBACK_WARN_TIP_SHOWED, true);
    }
}
